package com.unicauca.anteproyecto.application.dto;

import com.unicauca.anteproyecto.domain.model.EstadoAnteproyecto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para anteproyecto
 * UBICACIÓN: application/dto/AnteproyectoResponse.java
 */
@Schema(description = "Respuesta con los datos completos de un anteproyecto")
public class AnteproyectoResponse {

    @Schema(description = "ID único del anteproyecto", example = "1")
    private Long id;

    @Schema(description = "ID del Formato A asociado", example = "5")
    private Long formatoAId;

    @Schema(description = "URL del archivo PDF del anteproyecto",
            example = "/uploads/anteproyecto_2024_001.pdf")
    private String archivoUrl;

    @Schema(description = "Estado actual del anteproyecto",
            example = "PENDIENTE_EVALUADORES",
            implementation = EstadoAnteproyecto.class)
    private EstadoAnteproyecto estado;

    @Schema(description = "Fecha y hora en que se subió el anteproyecto",
            example = "2024-12-09T14:30:00")
    private LocalDateTime fechaSubida;

    @Schema(description = "ID del docente que subió el anteproyecto", example = "3")
    private Long docenteId;

    @Schema(description = "Lista de evaluadores asignados (máximo 2)")
    private List<EvaluadorDto> evaluadores;

    // Constructores
    public AnteproyectoResponse() {}

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

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public EstadoAnteproyecto getEstado() {
        return estado;
    }

    public void setEstado(EstadoAnteproyecto estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public List<EvaluadorDto> getEvaluadores() {
        return evaluadores;
    }

    public void setEvaluadores(List<EvaluadorDto> evaluadores) {
        this.evaluadores = evaluadores;
    }
}