package com.unicauca.formatoa.infrastructure.adapters.out.persistence.entity;

import com.unicauca.formatoa.domain.model.ResultadoEvaluacion;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para Evaluación
 */
@Entity
@Table(name = "evaluaciones")
public class EvaluacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "formato_a_id", nullable = false)
    private Long formatoAId;

    @Column(name = "coordinador_id", nullable = false)
    private Long coordinadorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoEvaluacion resultado;

    @Column(nullable = false, length = 2000)
    private String observaciones;

    @Column(name = "fecha_evaluacion", nullable = false, updatable = false)
    private LocalDateTime fechaEvaluacion;

    @PrePersist
    protected void onCreate() {
        fechaEvaluacion = LocalDateTime.now();
    }

    // Constructor vacío
    public EvaluacionEntity() {}

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