package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.SharedService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// Configurar la Clase Controladora
@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    // Dependencias Autowire (Inyección por constructor)
    private final DoctorService doctorService;
    private final SharedService service;

    public DoctorController(DoctorService doctorService, SharedService service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    /**
     * 1. Obtener Disponibilidad del Doctor
     * Endpoint: GET /availability/{user}/{doctorId}/{date}/{token}
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable("user") String user,
            @PathVariable("doctorId") Long doctorId,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("token") String token) {

        // Validamos el token contra el rol proporcionado dinámicamente
        ResponseEntity<Map<String, String>> auth = service.validateToken(token, user);
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        // Asumimos que getDoctorAvailability devuelve un ResponseEntity o Map con la disponibilidad
        return ResponseEntity.ok(doctorService.getDoctorAvailability(doctorId, date));
    }

    /**
     * 2. Obtener Lista de Doctores
     * Endpoint: GET /
     * (Público: No requiere validación de token según las instrucciones)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return ResponseEntity.ok(response);
    }

    /**
     * 3. Agregar Nuevo Doctor
     * Endpoint: POST /{token}
     */
    @PostMapping("/{token}")
    public ResponseEntity<?> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable("token") String token) {

        // Solo un administrador puede agregar doctores
        ResponseEntity<Map<String, String>> auth = service.validateToken(token, "admin");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        int result = doctorService.saveDoctor(doctor);
        Map<String, String> response = new HashMap<>();

        if (result == 1) {
            response.put("message", "Doctor agregado a la base de datos");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (result == 0) {
            response.put("message", "El doctor ya existe");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            response.put("message", "Ocurrió un error interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 4. Inicio de Sesión del Doctor
     * Endpoint: POST /login
     */
    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(@RequestBody Login login) {
        // El servicio de doctor maneja la validación de credenciales y generación del token
        return doctorService.validateDoctor(login);
    }

    /**
     * 5. Actualizar Detalles del Doctor
     * Endpoint: PUT /{token}
     */
    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable("token") String token) {

        // Validamos que sea un administrador
        ResponseEntity<Map<String, String>> auth = service.validateToken(token, "admin");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        int result = doctorService.updateDoctor(doctor);
        Map<String, String> response = new HashMap<>();

        if (result == 1) {
            response.put("message", "Doctor actualizado");
            return ResponseEntity.ok(response);
        } else if (result == 0) {
            response.put("message", "Doctor no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("message", "Ocurrió un error interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 6. Eliminar Doctor
     * Endpoint: DELETE /{id}/{token}
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable("id") Long id,
            @PathVariable("token") String token) {

        // Validamos que sea un administrador
        ResponseEntity<Map<String, String>> auth = service.validateToken(token, "admin");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        int result = doctorService.deleteDoctor(id);
        Map<String, String> response = new HashMap<>();

        if (result == 1) {
            response.put("message", "Doctor eliminado exitosamente");
            return ResponseEntity.ok(response);
        } else if (result == 0) {
            response.put("message", "Doctor no encontrado con el id");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("message", "Ocurrió un error interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 7. Filtrar Doctores
     * Endpoint: GET /filter/{name}/{time}/{speciality}
     * (Público: No requiere validación de token según las instrucciones)
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<?> filter(
            @PathVariable("name") String name,
            @PathVariable("time") String time,
            @PathVariable("speciality") String speciality) {

        // Delegamos el filtrado al SharedService (service)
        return ResponseEntity.ok(service.filterDoctor(name, time, speciality));
    }
}