package com.unicauca.anteproyecto.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para Evaluadores Asignados
 * UBICACIÓN: infrastructure/adapters/out/persistence/entity/EvaluadorAsignadoEntity.java
 */
@Entity
@Table(name = "evaluadores_asignados")
public class EvaluadorAsignadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "anteproyecto_id", nullable = false)
    private Long anteproyectoId;

    @Column(name = "docente_evaluador_id", nullable = false)
    private Long docenteEvaluadorId;

    @Column(name = "nombre_evaluador", nullable = false, length = 200)
    private String nombreEvaluador;

    @Column(name = "email_evaluador", nullable = false, length = 100)
    private String emailEvaluador;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;

    @Column(nullable = false)
    private boolean notificado;

    @PrePersist
    protected void onCreate() {
        fechaAsignacion = LocalDateTime.now();
        notificado = false;
    }

    // Constructor vacío
    public EvaluadorAsignadoEntity() {}

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnteproyectoId() {
        return anteproyectoId;
    }

    public void setAnteproyectoId(Long anteproyectoId) {
        this.anteproyectoId = anteproyectoId;
    }

    public Long getDocenteEvaluadorId() {
        return docenteEvaluadorId;
    }

    public void setDocenteEvaluadorId(Long docenteEvaluadorId) {
        this.docenteEvaluadorId = docenteEvaluadorId;
    }

    public String getNombreEvaluador() {
        return nombreEvaluador;
    }

    public void setNombreEvaluador(String nombreEvaluador) {
        this.nombreEvaluador = nombreEvaluador;
    }

    public String getEmailEvaluador() {
        return emailEvaluador;
    }

    public void setEmailEvaluador(String emailEvaluador) {
        this.emailEvaluador = emailEvaluador;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public boolean isNotificado() {
        return notificado;
    }

    public void setNotificado(boolean notificado) {
        this.notificado = notificado;
    }
}