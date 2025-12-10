package com.unicauca.formatoa.infrastructure.adapters.out.persistence.entity;

import com.unicauca.formatoa.domain.model.EstadoFormato;
import com.unicauca.formatoa.domain.model.Modalidad;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para Formato A
 */
@Entity
@Table(name = "formatos_a")
public class FormatoAEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Modalidad modalidad;

    @Column(name = "fecha_presentacion", nullable = false)
    private LocalDateTime fechaPresentacion;

    @Column(nullable = false, length = 200)
    private String director;

    @Column(length = 200)
    private String codirector;

    @Column(name = "objetivo_general", nullable = false, length = 1000)
    private String objetivoGeneral;

    @Column(name = "objetivos_especificos", nullable = false, length = 2000)
    private String objetivosEspecificos;

    @Column(name = "archivo_url", nullable = false, length = 500)
    private String archivoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoFormato estado;

    @Column(name = "numero_intento", nullable = false)
    private Integer numeroIntento;

    @Column(name = "docente_id", nullable = false)
    private Long docenteId;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (numeroIntento == null) {
            numeroIntento = 1;
        }
        if (estado == null) {
            estado = EstadoFormato.EN_PRIMERA_EVALUACION;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Constructor vacío
    public FormatoAEntity() {}

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public void setModalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
    }

    public LocalDateTime getFechaPresentacion() {
        return fechaPresentacion;
    }

    public void setFechaPresentacion(LocalDateTime fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCodirector() {
        return codirector;
    }

    public void setCodirector(String codirector) {
        this.codirector = codirector;
    }

    public String getObjetivoGeneral() {
        return objetivoGeneral;
    }

    public void setObjetivoGeneral(String objetivoGeneral) {
        this.objetivoGeneral = objetivoGeneral;
    }

    public String getObjetivosEspecificos() {
        return objetivosEspecificos;
    }

    public void setObjetivosEspecificos(String objetivosEspecificos) {
        this.objetivosEspecificos = objetivosEspecificos;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public EstadoFormato getEstado() {
        return estado;
    }

    public void setEstado(EstadoFormato estado) {
        this.estado = estado;
    }

    public Integer getNumeroIntento() {
        return numeroIntento;
    }

    public void setNumeroIntento(Integer numeroIntento) {
        this.numeroIntento = numeroIntento;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}