package com.unicauca.notification.domain.model;

import java.time.LocalDateTime;

/**
 * Modelo de mensaje de notificación
 * UBICACIÓN: domain/model/NotificationMessage.java
 */
public class NotificationMessage {
    private String destinatario;
    private String asunto;
    private String mensaje;
    private TipoNotificacion tipo;
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public NotificationMessage() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor completo
    public NotificationMessage(String destinatario, String asunto, 
                              String mensaje, TipoNotificacion tipo) {
        this();
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.tipo = tipo;
    }

    // Getters y Setters
    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "destinatario='" + destinatario + '\'' +
                ", asunto='" + asunto + '\'' +
                ", tipo=" + tipo +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}