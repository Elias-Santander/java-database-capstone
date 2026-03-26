package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.SharedService; // Tu SharedService
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// 1. Configurar la Clase del Controlador
@RestController
@RequestMapping("/patient")
public class PatientController {

    // 2. Dependencias Autowired (vía inyección por constructor)
    private final PatientService patientService;
    private final SharedService service;

    public PatientController(PatientService patientService, SharedService service) {
        this.patientService = patientService;
        this.service = service;
    }

    /**
     * 1. Obtener Detalles del Paciente
     * Endpoint: GET /patient/{token}
     */
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable("token") String token) {

        // Validamos el token para el rol "patient"
        ResponseEntity<Map<String, String>> auth = service.validateToken(token, "patient");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth; // Devuelve el error HTTP apropiado (ej. 401 Unauthorized)
        }

        // Obtiene y devuelve los detalles del paciente
        return ResponseEntity.ok(patientService.getPatientDetails(token));
    }

    /**
     * 2. Crear un Nuevo Paciente
     * Endpoint: POST /patient
     * (Público: No requiere token porque es el registro inicial)
     */
    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody Patient patient) {

        // El PatientService se encarga de verificar si existe y de crearlo
        int result = patientService.createPatient(patient);
        Map<String, String> response = new HashMap<>();

        if (result == 1) {
            response.put("message", "Registro exitoso");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (result == 0) {
            response.put("message", "El paciente con el correo electrónico o número de teléfono ya existe");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            response.put("message", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 3. Inicio de Sesión del Paciente
     * Endpoint: POST /patient/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        // Delega la validación de credenciales al SharedService (Service)
        return service.validatePatientLogin(login);
    }

    /**
     * 4. Obtener Citas del Paciente
     * Endpoint: GET /patient/{id}/{token}
     */
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointment(
            @PathVariable("id") Long id,
            @PathVariable("token") String token) {

        // Validar que la solicitud la hace un paciente válido
        ResponseEntity<Map<String, String>> auth = service.validateToken(token, "patient");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        // Obtiene las citas del paciente utilizando el ID
        return ResponseEntity.ok(patientService.getPatientAppointment(id, token));
    }

    /**
     * 5. Filtrar Citas del Paciente
     * Endpoint: GET /patient/filter/{condition}/{name}/{token}
     */
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(
            @PathVariable("condition") String condition,
            @PathVariable("name") String name,
            @PathVariable("token") String token) {

        // Validar el token para asegurar que es un paciente
        ResponseEntity<Map<String, String>> auth = service.validateToken(token, "patient");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        // Llama a service.filterPatient() para aplicar los filtros
        return ResponseEntity.ok(service.filterPatient(condition, name, token));
    }
}