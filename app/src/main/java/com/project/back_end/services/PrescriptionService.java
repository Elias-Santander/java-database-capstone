package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * 1. savePrescription: Guarda una receta en MongoDB.
     * Retorna 201 Created en éxito o 500 en error.
     */
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            // Intento de guardado en la base de datos
            prescriptionRepository.save(prescription);

            response.put("message", "Receta guardada");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            // Manejo de error genérico según instrucciones
            response.put("message", "Error interno al intentar guardar la receta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 2. getPrescription: Recupera la receta asociada a una cita específica.
     * Retorna 200 OK con los detalles o 500 en caso de error.
     */
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Intento de obtener la receta por ID de cita
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);

            if (prescriptions.isEmpty()) {
                response.put("message", "No se encontró ninguna receta para esta cita.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Éxito: devolvemos la receta encontrada
            response.put("prescription", prescriptions.get(0));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Manejo de error en la recuperación
            response.put("message", "Error al recuperar la receta");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}