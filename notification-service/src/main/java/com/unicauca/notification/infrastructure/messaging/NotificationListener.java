package com.unicauca.notification.infrastructure.messaging;

import com.unicauca.notification.domain.model.NotificationMessage;
import com.unicauca.notification.domain.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener de mensajes de RabbitMQ
 * UBICACIÓN: infrastructure/messaging/NotificationListener.java
 */
@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);
    private final EmailService emailService;

    public NotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Escucha mensajes de la cola de Formato A
     */
    @RabbitListener(queues = "${rabbitmq.queue.formato-a}")
    public void procesarNotificacionFormatoA(NotificationMessage mensaje) {
        logger.info("[INFO] Mensaje recibido de cola formato-a: {}", mensaje);
        try {
            emailService.enviarEmail(mensaje);
        } catch (Exception e) {
            logger.error("[ERROR] Error procesando notificación Formato A: {}", e.getMessage());
            throw e; // Reintentará según configuración
        }
    }

    /**
     * Escucha mensajes de la cola de Anteproyecto
     */
    @RabbitListener(queues = "${rabbitmq.queue.anteproyecto}")
    public void procesarNotificacionAnteproyecto(NotificationMessage mensaje) {
        logger.info("[INFO] Mensaje recibido de cola anteproyecto: {}", mensaje);
        try {
            emailService.enviarEmail(mensaje);
        } catch (Exception e) {
            logger.error("[ERROR] Error procesando notificación Anteproyecto: {}", e.getMessage());
            throw e;
        }
    }
}