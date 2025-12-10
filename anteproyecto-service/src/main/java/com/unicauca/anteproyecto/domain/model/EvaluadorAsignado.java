package com.unicauca.anteproyecto.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio para evaluadores asignados
 * REQUISITO 8: Jefe de departamento asigna 2 evaluadores
 */
public class EvaluadorAsignado {
    private Long id;
    private Long anteproyectoId;
    private Long docenteEvaluadorId;
    private String nombreEvaluador;
    private String emailEvaluador;
    private LocalDateTime fechaAsignacion;
    private boolean notificado;

    // Constructor vacío
    public EvaluadorAsignado() {
        this.fechaAsignacion = LocalDateTime.now();
        this.notificado = false;
    }

    // Constructor completo
    public EvaluadorAsignado(Long anteproyectoId, Long docenteEvaluadorId, 
                            String nombreEvaluador, String emailEvaluador) {
        this();
        this.anteproyectoId = anteproyectoId;
        this.docenteEvaluadorId = docenteEvaluadorId;
        this.nombreEvaluador = nombreEvaluador;
        this.emailEvaluador = emailEvaluador;
    }

    // ==================== MÉTODOS DE NEGOCIO ====================

    /**
     * Marca al evaluador como notificado
     */
    public void marcarComoNotificado() {
        this.notificado = true;
    }

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