package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "The Doctor cannot be null")
    private Doctor doctor;

    @ManyToOne
    @NotNull(message = "The Patient cannot be null")
    private Patient patient;

    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;

    @NotNull(message = "The Status cannot be null")
    private int status;

    /**
     * Calcula la hora de finalización estimada de la cita (1 hora después del inicio).
     * El uso de @Transient asegura que este valor no se persista en la base de datos MySQL,
     * ya que es un dato derivado.
     */
    @Transient
    private LocalDateTime getEndTime() {
        if (this.appointmentTime == null) {
            return null;
        }
        return this.appointmentTime.plusHours(1);
    }

    /**
     * Extrae únicamente la parte de la fecha (LocalDate) de la fecha y hora de la cita.
     * Útil para agrupaciones en el calendario o filtros de búsqueda por día.
     * La anotación @Transient evita que Hibernate intente mapear este método a una columna en MySQL.
     */
    @Transient
    private LocalDate getAppointmentDate() {
        if (this.appointmentTime == null) {
            return null;
        }
        return this.appointmentTime.toLocalDate();
    }

    /**
     * Extrae únicamente la fracción de tiempo (hora/minutos) del campo appointmentTime.
     * Útil para validaciones de horarios de apertura o para mostrar la hora en la agenda del doctor.
     * DEBE SER PUBLIC PARA QUE FUNCIONE EN Jackson/Thymeleaf
     */
    @Transient
    private LocalTime getAppointmentTimeOnly() {
        if (this.appointmentTime == null) {
            return null;
        }
        return this.appointmentTime.toLocalTime();
    }

    public Appointment() {
    }

    public Appointment(Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status) {
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
}

