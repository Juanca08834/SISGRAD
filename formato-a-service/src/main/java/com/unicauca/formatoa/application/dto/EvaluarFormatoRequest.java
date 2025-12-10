package com.unicauca.formatoa.application.dto;

import com.unicauca.formatoa.domain.model.ResultadoEvaluacion;
import jakarta.validation.constraints.*;

/**
 * DTO para evaluar un Formato A
 */
public class EvaluarFormatoRequest {
    
    @NotNull(message = "El ID del formato es obligatorio")
    private Long formatoAId;
    
    @NotNull(message = "El ID del coordinador es obligatorio")
    private Long coordinadorId;
    
    @NotNull(message = "El resultado de la evaluación es obligatorio")
    private ResultadoEvaluacion resultado;
    
    @NotBlank(message = "Las observaciones son obligatorias")
    @Size(min = 10, max = 2000, message = "Las observaciones deben tener entre 10 y 2000 caracteres")
    private String observaciones;

    // Constructores
    public EvaluarFormatoRequest() {}

    // Getters y Setters
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
}