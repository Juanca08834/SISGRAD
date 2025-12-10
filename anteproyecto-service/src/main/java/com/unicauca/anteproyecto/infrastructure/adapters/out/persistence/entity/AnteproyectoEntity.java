package com.unicauca.anteproyecto.infrastructure.adapters.out.persistence.entity;

import com.unicauca.anteproyecto.domain.model.EstadoAnteproyecto;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para Anteproyecto
 * UBICACIÓN: infrastructure/adapters/out/persistence/entity/AnteproyectoEntity.java
 */
@Entity
@Table(name = "anteproyectos")
public class AnteproyectoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "formato_a_id", nullable = false)
    private Long formatoAId;

    @Column(name = "archivo_url", nullable = false, length = 500)
    private String archivoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoAnteproyecto estado;

    @Column(name = "fecha_subida", nullable = false, updatable = false)
    private LocalDateTime fechaSubida;

    @Column(name = "docente_id", nullable = false)
    private Long docenteId;

    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoAnteproyecto.PENDIENTE_ASIGNACION;
        }
    }

    // Constructor vacío
    public AnteproyectoEntity() {}

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
}