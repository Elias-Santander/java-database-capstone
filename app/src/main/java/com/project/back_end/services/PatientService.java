package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * 1. createPatient: Guarda un nuevo paciente.
     * Retorna 1 en éxito, 0 en fallo.
     */
    @Transactional
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            System.err.println("Error al guardar paciente: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 2. getPatientAppointment: Recupera citas validando que el ID coincida con el Token.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String emailFromToken = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(emailFromToken).orElse(null);

            // Verificación de seguridad: ¿El ID del token coincide con el ID solicitado?
            if (patient == null || !patient.getId().equals(id)) {
                response.put("message", "Unauthorized: Identity mismatch.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<AppointmentDTO> dtos = appointmentRepository.findByPatientId(id)
                    .stream().map(this::convertToDTO).collect(Collectors.toList());

            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving appointments.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 3. filterByCondition: Filtra por 'past' (1) o 'future' (0).
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        int status = "past".equalsIgnoreCase(condition) ? 1 : 0;

        try {
            List<AppointmentDTO> dtos = appointmentRepository.findByPatientIdAndStatusOrderByAppointmentTimeAsc(id, status)
                    .stream().map(this::convertToDTO).collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering by condition.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 4. filterByDoctor: Filtra por nombre del doctor y ID del paciente.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AppointmentDTO> dtos = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId)
                    .stream().map(this::convertToDTO).collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering by doctor name.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 5. filterByDoctorAndCondition: Combina nombre del doctor y estado (past/future).
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        int status = "past".equalsIgnoreCase(condition) ? 1 : 0;

        try {
            List<AppointmentDTO> dtos = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status)
                    .stream().map(this::convertToDTO).collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error in combined filter.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 6. getPatientDetails: Recupera detalles del paciente basándose solo en el token.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            return patientRepository.findByEmail(email).map(patient -> {
                response.put("patient", patient);
                return ResponseEntity.ok(response);
            }).orElseGet(() -> {
                response.put("message", "Patient not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            });
        } catch (Exception e) {
            response.put("message", "Invalid token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Método Auxiliar: Convertir Entidad a DTO completo.
     * Mapea todos los atributos definidos en AppointmentDTO desde la entidad Appointment.
     */
    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();

        // 1. Datos básicos de la cita
        dto.setId(appointment.getId());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus());

        // 2. Datos del Doctor (extraídos del objeto Doctor de la entidad)
        if (appointment.getDoctor() != null) {
            dto.setDoctorId(appointment.getDoctor().getId());
            dto.setDoctorName(appointment.getDoctor().getName());
        }

        // 3. Datos del Paciente (extraídos del objeto Patient de la entidad)
        if (appointment.getPatient() != null) {
            dto.setPatientId(appointment.getPatient().getId());
            dto.setPatientName(appointment.getPatient().getName());
            dto.setPatientEmail(appointment.getPatient().getEmail());
            dto.setPatientPhone(appointment.getPatient().getPhone());
            dto.setPatientAddress(appointment.getPatient().getAddress());
        }

        // 4. Campos calculados para facilidad del Frontend
        if (appointment.getAppointmentTime() != null) {
            dto.setAppointmentDate(appointment.getAppointmentTime().toLocalDate());
            dto.setAppointmentTimeOnly(appointment.getAppointmentTime().toLocalTime());

            // Asumiendo una duración estándar (ej. 30 min) si la entidad no tiene endTime
            dto.setEndTime(appointment.getAppointmentTime().plusMinutes(30));
        }

        return dto;
    }
}