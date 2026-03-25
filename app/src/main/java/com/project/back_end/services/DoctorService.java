package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    // 2. Constructor Injection for Dependencies
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 4. getDoctorAvailability Method
    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) return new ArrayList<>();

        // Obtener citas ya reservadas para ese día
        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, date.atStartOfDay(), date.atTime(23, 59));

        List<String> bookedSlots = bookedAppointments.stream()
                .map(app -> app.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        // Filtrar los slots disponibles del doctor contra los ya reservados
        return doctor.getAvailableTimes().stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    // 5. saveDoctor Method
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
                return -1; // Conflicto: Email ya existe
            }
            doctorRepository.save(doctor);
            return 1; // Éxito
        } catch (Exception e) {
            return 0; // Error interno
        }
    }

    // 6. updateDoctor Method
    @Transactional
    public int updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getId())) {
            return -1;
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 7. getDoctors Method (Eager loading handled by Transactional context)
    @Transactional
    public List<Doctor> getDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        // Forzamos la carga de colecciones Lazy si fuera necesario
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    // 8. deleteDoctor Method
    @Transactional
    public int deleteDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            return -1;
        }
        try {
            // Eliminar citas asociadas primero
            appointmentRepository.deleteAllByDoctorId(doctorId);
            // Eliminar doctor
            doctorRepository.deleteById(doctorId);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();

        // Sugerencia: Usar findByEmail para encontrar al doctor y verificar contraseña
        return doctorRepository.findByEmail(login.getEmail()).map(doctor -> {
            if (doctor.getPassword().equals(login.getPassword())) {
                String token = tokenService.generateToken("doctor");
                response.put("token", token);
                response.put("role", "doctor");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Invalid password.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }).orElseGet(() -> {
            response.put("message", "Doctor not found with email: " + login.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }

    /**
     * findDoctorByName: Coincidencia parcial por nombre.
     */
    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        // Sugerencia: Usar findByNameLike
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        response.put("doctors", doctors);
        return response;
    }

    /**
     * filterDoctorsByNameSpecilityandTime: Filtro triple (Nombre, Especialidad, AM/PM).
     */
    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        // Sugerencia: findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase + filtro por tiempo
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /**
     * filterDoctorByNameAndTime: Filtro por nombre y AM/PM.
     */
    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /**
     * filterDoctorByNameAndSpecility: Filtro por nombre y especialidad.
     */
    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        response.put("doctors", doctors);
        return response;
    }

    /**
     * filterDoctorByTimeAndSpecility: Filtro por especialidad y AM/PM.
     */
    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecializationIgnoreCase(specialty);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    /**
     * filterDoctorBySpecility: Filtro simple por especialidad.
     */
    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecializationIgnoreCase(specialty);
        response.put("doctors", doctors);
        return response;
    }

    /**
     * filterDoctorsByTime: Filtra todos los doctores por AM/PM.
     */
    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> allDoctors = doctorRepository.findAll();
        response.put("doctors", filterDoctorByTime(allDoctors, amOrPm));
        return response;
    }

    /**
     * filterDoctorByTime (PRIVADO): Lógica nuclear de filtrado AM/PM.
     */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        // Sugerencia: Filtrar según horarios disponibles comparando con AM/PM
        return doctors.stream()
                .filter(doctor -> doctor.getAvailableTimes().stream().anyMatch(slot -> {
                    // Extraer hora del slot (ej: "09:00" -> 9)
                    int hour = Integer.parseInt(slot.split(":")[0]);
                    if ("AM".equalsIgnoreCase(amOrPm)) {
                        return hour < 12;
                    } else if ("PM".equalsIgnoreCase(amOrPm)) {
                        return hour >= 12;
                    }
                    return true; // Si no es AM ni PM, no filtra
                }))
                .collect(Collectors.toList());
    }
   
}
