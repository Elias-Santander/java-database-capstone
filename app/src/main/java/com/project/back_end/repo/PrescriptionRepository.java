package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    /**
     * - Recupera todas las recetas asociadas a un ID de cita específico.
     * - Aunque MongoDB no maneja "Foreign Keys" de forma nativa como SQL,
     * este método permite realizar la búsqueda por referencia de ID.
     * * @param appointmentId ID de la cita (proviene de la base de datos relacional).
     * @return List<Prescription> Lista de recetas encontradas.
     */
    List<Prescription> findByAppointmentId(Long appointmentId);

}

