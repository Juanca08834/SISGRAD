package com.unicauca.anteproyecto.domain.ports.out;

/**
 * Puerto de salida para notificaciones
 * UBICACIÓN: domain/ports/out/NotificationPort.java
 */
public interface NotificationPort {
    /**
     * Notifica al jefe de departamento sobre nuevo anteproyecto
     */
    void notificarNuevoAnteproyecto(String jefeEmail, Long anteproyectoId);
    
    /**
     * Notifica a evaluadores que fueron asignados (REQUISITO 8)
     */
    void notificarEvaluadoresAsignados(String evaluadorEmail, Long anteproyectoId);
}