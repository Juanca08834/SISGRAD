package com.unicauca.formatoa.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio FormatoA
 * Representa un Formato A de trabajo de grado
 * Esta clase contiene la lógica de negocio pura
 */
public class FormatoA {
    private Long id;
    private String titulo;
    private Modalidad modalidad;
    private LocalDateTime fechaPresentacion;
    private String director;
    private String codirector;
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private String archivoUrl;
    private EstadoFormato estado;
    private Integer numeroIntento;
    private Long docenteId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructor vacío
    public FormatoA() {
        this.numeroIntento = 1;
        this.estado = EstadoFormato.EN_PRIMERA_EVALUACION;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor completo
    public FormatoA(String titulo, Modalidad modalidad, LocalDateTime fechaPresentacion,
                    String director, String codirector, String objetivoGeneral,
                    String objetivosEspecificos, String archivoUrl, Long docenteId) {
        this();
        this.titulo = titulo;
        this.modalidad = modalidad;
        this.fechaPresentacion = fechaPresentacion;
        this.director = director;
        this.codirector = codirector;
        this.objetivoGeneral = objetivoGeneral;
        this.objetivosEspecificos = objetivosEspecificos;
        this.archivoUrl = archivoUrl;
        this.docenteId = docenteId;
    }

    // ==================== MÉTODOS DE NEGOCIO ====================

    /**
     * Verifica si el formato está en su tercer intento
     */
    public boolean esUltimoIntento() {
        return this.numeroIntento >= 3;
    }

    /**
     * Incrementa el número de intento y actualiza el estado
     */
    public void incrementarIntento() {
        this.numeroIntento++;
        
        if (this.numeroIntento == 2) {
            this.estado = EstadoFormato.EN_SEGUNDA_EVALUACION;
        } else if (this.numeroIntento == 3) {
            this.estado = EstadoFormato.EN_TERCERA_EVALUACION;
        }
        
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Marca el formato como aprobado
     */
    public void aprobar() {
        this.estado = EstadoFormato.ACEPTADO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Marca el formato como rechazado
     * Si es el tercer intento, el rechazo es definitivo
     * Si es el primer o segundo intento, se marca como rechazado temporal
     */
    public void rechazar() {
        if (esUltimoIntento()) {
            this.estado = EstadoFormato.RECHAZADO;
        } else if (this.numeroIntento == 1) {
            this.estado = EstadoFormato.RECHAZADO_PRIMERA_EVALUACION;
        } else if (this.numeroIntento == 2) {
            this.estado = EstadoFormato.RECHAZADO_SEGUNDA_EVALUACION;
        }
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Verifica si el formato puede ser editado
     */
    public boolean puedeSerEditado() {
        return this.estado != EstadoFormato.ACEPTADO 
            && this.estado != EstadoFormato.RECHAZADO;
    }

    /**
     * Actualiza la fecha de modificación
     */
    public void actualizarFechaModificacion() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ==================== GETTERS Y SETTERS ====================

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