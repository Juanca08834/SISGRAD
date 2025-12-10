package com.unicauca.formatoa.domain.ports.out;

/**
 * Puerto de salida para envío de notificaciones
 */
public interface NotificationPort {
    /**
     * Envía una notificación de nuevo Formato A al coordinador
     * @param coordinadorEmail Email del coordinador
     * @param formatoAId ID del Formato A
     * @param titulo Título del proyecto
     */
    void notificarNuevoFormato(String coordinadorEmail, Long formatoAId, String titulo);
    
    /**
     * Envía notificación de evaluación a docentes y estudiantes
     * @param docenteEmail Email del docente
     * @param formatoAId ID del Formato A
     * @param resultado Resultado de la evaluación
     * @param observaciones Observaciones
     */
    void notificarEvaluacion(String docenteEmail, Long formatoAId, 
                            String resultado, String observaciones);
}