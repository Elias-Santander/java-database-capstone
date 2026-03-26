package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.SharedService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// 1. **Set Up the Controller Class**
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    // 2. **Autowire Dependencies (Constructor Injection)**
    private final AppointmentService appointmentService;
    private final SharedService sharedService;

    public AppointmentController(AppointmentService appointmentService, SharedService sharedService) {
        this.appointmentService = appointmentService;
        this.sharedService = sharedService;
    }

    // 3. **Define the getAppointments Method**
    @GetMapping("/{date}/{pname}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("pname") String pname,
            @PathVariable("token") String token) {

        // Validamos el token para el rol "doctor"
        ResponseEntity<Map<String, String>> auth = sharedService.validateToken(token, "doctor");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        // Si es válido, retornamos las citas (el service ya devuelve el Map con DTOs)
        return ResponseEntity.ok(appointmentService.getAppointment(pname, date, token));
    }

    /**
     * 4. bookAppointment
     * Adaptado para manejar el retorno int del service.
     */
    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable("token") String token) {

        ResponseEntity<Map<String, String>> auth = sharedService.validateToken(token, "patient");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        // 1. Validar disponibilidad mediante SharedService
        int check = sharedService.validateAppointment(appointment);

        Map<String, String> response = new HashMap<>();

        if (check == 1) {
            // 2. Si es válido, procedemos a agendar en AppointmentService
            // Asumiendo que bookAppointment en el service devuelve un int (1 éxito, 0 error)
            int result = appointmentService.bookAppointment(appointment);
            if (result == 1) {
                response.put("message", "Appointment booked successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("message", "Error: Could not save the appointment");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else if (check == 0) {
            response.put("message", "The doctor is not available at that time");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 5. **Define the updateAppointment Method**
    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable("token") String token) {

        ResponseEntity<Map<String, String>> auth = sharedService.validateToken(token, "patient");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        return appointmentService.updateAppointment(appointment);
    }

    // 6. **Define the cancelAppointment Method**
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable("id") Long id,
            @PathVariable("token") String token) {

        ResponseEntity<Map<String, String>> auth = sharedService.validateToken(token, "patient");
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        return appointmentService.cancelAppointment(id, token);
    }
}