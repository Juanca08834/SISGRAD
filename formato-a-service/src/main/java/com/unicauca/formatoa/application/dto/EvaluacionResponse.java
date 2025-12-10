package com.unicauca.formatoa.application.dto;

import com.unicauca.formatoa.domain.model.ResultadoEvaluacion;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para Evaluación
 */
public class EvaluacionResponse {
    private Long id;
    private Long formatoAId;
    private Long coordinadorId;
    private ResultadoEvaluacion resultado;
    private String observaciones;
    private LocalDateTime fechaEvaluacion;

    // Constructor vacío
    public EvaluacionResponse() {}

    // Constructor completo
    public EvaluacionResponse(Long id, Long formatoAId, Long coordinadorId,
                             ResultadoEvaluacion resultado, String observaciones,
                             LocalDateTime fechaEvaluacion) {
        this.id = id;
        this.formatoAId = formatoAId;
        this.coordinadorId = coordinadorId;
        this.resultado = resultado;
        this.observaciones = observaciones;
        this.fechaEvaluacion = fechaEvaluacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFormatoAId() {
        return formatoAId;
    }

    public void setFormatoAId(Long formatoAId) {
        this.formatoAId = formatoAId;
    }

    public Long getCoordinadorId() {
        return coordinadorId;
    }

    public void setCoordinadorId(Long coordinadorId) {
        this.coordinadorId = coordinadorId;
    }

    public ResultadoEvaluacion getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoEvaluacion resultado) {
        this.resultado = resultado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }
}