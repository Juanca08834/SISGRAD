package com.unicauca.anteproyecto.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear anteproyecto
 * UBICACIÓN: application/dto/CrearAnteproyectoRequest.java
 */
@Schema(description = "Datos requeridos para crear un nuevo anteproyecto (REQUISITO 6)")
public class CrearAnteproyectoRequest {
    
    @Schema(description = "ID del Formato A aprobado asociado al anteproyecto",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del Formato A es obligatorio")
    private Long formatoAId;
    
    @Schema(description = "URL del archivo PDF del anteproyecto",
            example = "/uploads/anteproyecto_2024_001.pdf",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La URL del archivo es obligatoria")
    private String archivoUrl;
    
    @Schema(description = "ID del docente que sube el anteproyecto",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del docente es obligatorio")
    private Long docenteId;

    // Constructores
    public CrearAnteproyectoRequest() {}

    // Getters y Setters
    public Long getFormatoAId() {
        return formatoAId;
    }

    public void setFormatoAId(Long formatoAId) {
        this.formatoAId = formatoAId;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }
}