package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
// 2. Custom Query Methods:

    // findByDoctorIdAndAppointmentTimeBetween:
    // Retorna citas de un doctor en un rango de tiempo con Eager Fetch de tiempos disponibles.
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH d.availableTimes WHERE d.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween:
    // Filtro por doctor, nombre de paciente (ignore case) y rango de tiempo.
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH a.patient p WHERE d.id = :doctorId AND UPPER(p.name) LIKE UPPER(CONCAT('%', :patientName, '%')) AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("patientName") String patientName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // deleteAllByDoctorId:
    // Borrado masivo de citas de un doctor específico.
    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.doctor.id = :doctorId")
    void deleteAllByDoctorId(@Param("doctorId") Long doctorId);

    // findByPatientId:
    // Recupera todas las citas de un paciente.
    List<Appointment> findByPatientId(Long patientId);

    // findByPatient_IdAndStatusOrderByAppointmentTimeAsc:
    // Filtro por ID de paciente y estatus, ordenado cronológicamente.
    List<Appointment> findByPatientIdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    // filterByDoctorNameAndPatientId:
    // Filtro por nombre del doctor (LIKE) e ID del paciente.
    @Query("SELECT a FROM Appointment a WHERE a.doctor.name LIKE CONCAT('%', :doctorName, '%') AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId);

    // filterByDoctorNameAndPatientIdAndStatus:
    // Filtro por nombre del doctor, ID del paciente y estatus.
    @Query("SELECT a FROM Appointment a WHERE a.doctor.name LIKE CONCAT('%', :doctorName, '%') AND a.patient.id = :patientId AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
            @Param("doctorName") String doctorName,
            @Param("patientId") Long patientId,
            @Param("status") int status);

    // updateStatus:
    // Actualiza el estatus de una cita por su ID.
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(@Param("status") int status, @Param("id") long id);

}
