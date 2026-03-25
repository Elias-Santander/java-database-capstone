package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * findByEmail:
     * Recupera un paciente utilizando su dirección de correo electrónico.
     *
     * @param email El correo electrónico del paciente.
     * @return Un Optional que contiene al paciente si se encuentra.
     */
    Optional<Patient> findByEmail(String email);

    /**
     * findByEmailOrPhone:
     * Recupera un paciente buscando por su email o por su número de teléfono.
     * Útil para procesos de login o validación de duplicados.
     *
     * @param email El correo electrónico a buscar.
     * @param phone El número de teléfono a buscar.
     * @return Un Optional con el paciente que coincida con cualquiera de los dos parámetros.
     */
    Optional<Patient> findByEmailOrPhone(String email, String phone);

}

