package com.unicauca.anteproyecto.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para datos de evaluador
 * UBICACIÓN: application/dto/EvaluadorDto.java
 */
@Schema(description = "Datos de un docente evaluador del departamento de sistemas")
public class EvaluadorDto {
    
    @Schema(description = "ID del docente evaluador en el sistema",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del docente evaluador es obligatorio")
    private Long docenteEvaluadorId;
    
    @Schema(description = "Nombre completo del docente evaluador",
            example = "Dr. Carlos Andrés Collazos",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre del evaluador es obligatorio")
    private String nombreEvaluador;
    
    @Schema(description = "Correo electrónico institucional del evaluador",
            example = "ccollazos@unicauca.edu.co",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El email del evaluador es obligatorio")
    @Email(message = "Email inválido")
    private String emailEvaluador;

    // Constructores
    public EvaluadorDto() {}

    // Getters y Setters
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
}