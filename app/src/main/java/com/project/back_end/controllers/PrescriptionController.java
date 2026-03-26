package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.SharedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 1. Configurar la Clase Controladora
@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    // 2. Dependencias Autowire (Usando inyección por constructor)
    private final PrescriptionService prescriptionService;
    private final SharedService service;

    public PrescriptionController(PrescriptionService prescriptionService, SharedService service) {
        this.prescriptionService = prescriptionService;
        this.service = service;
    }

    /**
     * 1. Guardar Receta
     * Método: @PostMapping("/{token}")
     */
    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(
            @RequestBody Prescription prescription,
            @PathVariable("token") String token) {

        // Validar el token para asegurar que la solicitud es realizada por un médico
        ResponseEntity<Map<String, String>> auth = service.validateToken(token, "doctor");

        // Si el token es inválido, devuelve el mensaje de error con su estado HTTP
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        // Si el token es válido, guarda la receta usando la lógica del servicio
        // El servicio se encarga de devolver el mensaje de éxito o error
        return prescriptionService.savePrescription(prescription);
    }

    /**
     * 2. Obtener Receta por ID de Cita
     * Método: @GetMapping("/{appointmentId}/{token}")
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable("appointmentId") Long appointmentId,
            @PathVariable("token") String token) {

        // Validar el token para asegurar que la solicitud proviene de un médico válido
        ResponseEntity<Map<String, String>> auth = service.validateToken(token, "doctor");

        // Si el token es inválido, devuelve el error
        if (auth.getStatusCode() != HttpStatus.OK) {
            return auth;
        }

        // Si el token es válido, recupera la receta
        // El servicio manejará devolver los detalles si la encuentra, o el mensaje si no existe
        return prescriptionService.getPrescription(appointmentId);
    }
}