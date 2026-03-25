package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 2. Custom Query Methods:
    // findByEmail:
    // Recupera un Doctor basado en su correo electrónico.
    // Se recomienda usar Optional para manejar casos donde el email no exista.
    Optional<Doctor> findByEmail(String email);

    // findByNameLike:
    // Recupera una lista de doctores cuyo nombre contiene la cadena (case-sensitive).
    // Usamos @Query para aplicar el patrón CONCAT directamente en la consulta.
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    // findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase:
    // Búsqueda combinada: nombre parcial (ignore case) y especialidad exacta (ignore case).
    @Query("SELECT d FROM Doctor d WHERE " +
            "LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND " +
            "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialty, '%'))")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            @Param("name") String name,
            @Param("specialty") String specialty);

    // findBySpecialtyIgnoreCase:
    // Recupera doctores por especialidad, ignorando mayúsculas/minúsculas.
    // Nota: Asegúrate de que el atributo en tu modelo se llame 'specialization' o 'specialty'.
    List<Doctor> findBySpecializationIgnoreCase(String specialty);

}