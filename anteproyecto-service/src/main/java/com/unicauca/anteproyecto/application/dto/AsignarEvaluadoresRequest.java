package com.unicauca.anteproyecto.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO para asignar evaluadores (REQUISITO 8)
 * UBICACIÓN: application/dto/AsignarEvaluadoresRequest.java
 */
@Schema(description = "Datos requeridos para asignar evaluadores a un anteproyecto (REQUISITO 8). " +
        "El jefe de departamento asigna exactamente 2 docentes del departamento para evaluar.")
public class AsignarEvaluadoresRequest {
    
    @Schema(description = "ID del anteproyecto al que se asignarán los evaluadores",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del anteproyecto es obligatorio")
    private Long anteproyectoId;
    
    @Schema(description = "Lista de exactamente 2 evaluadores a asignar",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 2, maxLength = 2)
    @NotNull(message = "Los evaluadores son obligatorios")
    @Size(min = 2, max = 2, message = "Se deben asignar exactamente 2 evaluadores")
    @Valid
    private List<EvaluadorDto> evaluadores;

    // Constructores
    public AsignarEvaluadoresRequest() {}

    // Getters y Setters
    public Long getAnteproyectoId() {
        return anteproyectoId;
    }

    public void setAnteproyectoId(Long anteproyectoId) {
        this.anteproyectoId = anteproyectoId;
    }

    public List<EvaluadorDto> getEvaluadores() {
        return evaluadores;
    }

    public void setEvaluadores(List<EvaluadorDto> evaluadores) {
        this.evaluadores = evaluadores;
    }
}