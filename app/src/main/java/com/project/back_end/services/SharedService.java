package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SharedService {

    // Declaración de servicios y repositorios como privados y finales
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    // Inyección por constructor
    public SharedService(TokenService tokenService,
                         AdminRepository adminRepository,
                         DoctorRepository doctorRepository,
                         PatientRepository patientRepository,
                         DoctorService doctorService,
                         PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * 3. validateToken: Verifica la validez de un token para un usuario dado.
     */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Unauthorized: Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 4. validateAdmin: Valida las credenciales de inicio de sesión de un administrador.
     */
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin != null && admin.getPassword().equals(receivedAdmin.getPassword())) {
                String token = tokenService.generateToken( "admin");
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
            response.put("message", "Unauthorized: Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("message", "Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 5. filterDoctor: Filtra doctores basándose en nombre, especialidad y tiempo.
     */
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        // Lógica combinatoria delegando a doctorService
        if (name != null && specialty != null && time != null) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (name != null && specialty != null) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (name != null && time != null) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (specialty != null && time != null) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (name != null) {
            return doctorService.findDoctorByName(name);
        } else if (specialty != null) {
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (time != null) {
            return doctorService.filterDoctorsByTime(time);
        } else {
            // Por defecto devuelve todos los doctores
            Map<String, Object> allDoctors = new HashMap<>();
            allDoctors.put("doctors", doctorService.getDoctors());
            return allDoctors;
        }
    }

    /**
     * 6. validateAppointment: Valida si una cita está disponible según el horario del doctor.
     */
    public int validateAppointment(Appointment appointment) {
        return doctorRepository.findById(appointment.getDoctor().getId()).map(doctor -> {
            // Obtenemos disponibilidad para la fecha de la cita
            var availableSlots = doctorService.getDoctorAvailability(
                    doctor.getId(),
                    appointment.getAppointmentTime().toLocalDate()
            );

            // Extraemos la hora solicitada (ej. "09:00")
            String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();

            return availableSlots.contains(requestedTime) ? 1 : 0;
        }).orElse(-1); // El doctor no existe
    }

    /**
     * 7. validatePatient: Verifica si un paciente existe por email o teléfono.
     */
    public boolean validatePatient(Patient patient) {
        // Si findByEmailOrPhone encuentra algo, el paciente ya existe (retorna false)
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()).isEmpty();
    }

    /**
     * 8. validatePatientLogin: Valida las credenciales de un paciente.
     */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            return patientRepository.findByEmail(login.getEmail()).map(patient -> {
                if (patient.getPassword().equals(login.getPassword())) {
                    String token = tokenService.generateToken("patient");
                    response.put("token", token);
                    return ResponseEntity.ok(response);
                }
                response.put("message", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }).orElseGet(() -> {
                response.put("message", "Email not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            });
        } catch (Exception e) {
            response.put("message", "Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 9. filterPatient: Filtra citas de pacientes según condición y/o nombre del doctor.
     */
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        try {
            // Identificar al paciente mediante el token
            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email).orElse(null);

            if (patient == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (condition != null && name != null) {
                return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
            } else if (condition != null) {
                return patientService.filterByCondition(condition, patient.getId());
            } else if (name != null) {
                return patientService.filterByDoctor(name, patient.getId());
            } else {
                return patientService.getPatientAppointment(patient.getId(), token);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}