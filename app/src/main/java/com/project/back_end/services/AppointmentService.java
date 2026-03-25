package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    // 2. Constructor Injection for Dependencies
    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final SharedService sharedService;
    private final TokenService tokenService;

    public AppointmentService(AppointmentRepository appointmentRepo,
                              PatientRepository patientRepo,
                              DoctorRepository doctorRepo,
                              SharedService sharedService,
                              TokenService tokenService) {
        this.appointmentRepo = appointmentRepo;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.sharedService = sharedService;
        this.tokenService = tokenService;
    }

    /**
     * 4. Book Appointment Method
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepo.save(appointment);
            return 1;
        } catch (Exception e) {
            System.err.println("Error booking appointment: " + e.getMessage());
            return 0;
        }
    }

    /**
     * updateAppointment: Actualiza una cita existente previa validación.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        // Sugerencia: Verificar si la cita existe
        return appointmentRepo.findById(appointment.getId()).map(existingApp -> {

            // Sugerencia: Verificar si la actualización es válida (reglas de negocio)
            if (sharedService.validateAppointment(appointment) == 1) {
                appointmentRepo.save(appointment);
                response.put("message", "Success: Appointment updated.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed: Invalid appointment data or schedule conflict.");
                return ResponseEntity.badRequest().body(response);
            }

        }).orElseGet(() -> {
            response.put("message", "Error: Appointment ID not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }

    /**
     * cancelAppointment: Cancela una cita eliminándola de la base de datos.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        return appointmentRepo.findById(id).map(appointment -> {

            // CORRECCIÓN: Extraer identificador (email) y buscar al paciente
            String emailFromToken = tokenService.extractIdentifier(token);

            // Validar que el paciente que cancela sea el dueño de la cita
            if (!appointment.getPatient().getEmail().equals(emailFromToken)) {
                response.put("message", "Unauthorized: Token mismatch.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            appointmentRepo.delete(appointment);
            response.put("message", "Success: Appointment cancelled.");
            return ResponseEntity.ok(response);

        }).orElseGet(() -> {
            response.put("message", "Error: Appointment not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }

    /**
     * getAppointment: Recupera citas para un doctor y fecha, con filtro opcional de nombre.
     */
    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        // CORRECCIÓN: Extraer el identifier (email del doctor)
        String doctorEmail = tokenService.extractIdentifier(token);

        // Buscar al doctor para obtener su ID real
        Long doctorId = doctorRepo.findByEmail(doctorEmail)
                .map(Doctor::getId)
                .orElse(null);

        if (doctorId == null) {
            response.put("appointments", new ArrayList<>());
            return response;
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;
        if (pname == null || pname.trim().isEmpty() || pname.equals("null")) {
            appointments = appointmentRepo.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            appointments = appointmentRepo.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, pname, start, end
            );
        }

        // Convertir la lista a DTOs usando el método corregido anteriormente
        response.put("appointments", appointments.stream().map(this::convertToDTO).collect(Collectors.toList()));
        return response;
    }

    /**
     * 8. Change Status Method
     */
    @Transactional
    public void changeStatus(Long appointmentId, int newStatus) {
        appointmentRepo.updateStatus(newStatus, appointmentId);
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
