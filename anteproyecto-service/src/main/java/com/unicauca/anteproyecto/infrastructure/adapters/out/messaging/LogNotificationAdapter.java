package com.unicauca.anteproyecto.infrastructure.adapters.out.messaging;

import com.unicauca.anteproyecto.domain.ports.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Adaptador de notificaciones (Anteproyecto):
 *  - Mantiene logs simulando email
 *  - Publica mensajes reales a RabbitMQ para que notification-service los consuma (Requisito 8)
 */
@Component
public class LogNotificationAdapter implements NotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(LogNotificationAdapter.class);

    private final RabbitTemplate rabbitTemplate;
    private final String anteproyectoQueue;

    public LogNotificationAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.queue.anteproyecto}") String anteproyectoQueue
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.anteproyectoQueue = anteproyectoQueue;
    }

    @Override
    public void notificarNuevoAnteproyecto(String jefeEmail, Long anteproyectoId) {
        // 1) Logs
        logger.info("===========================================");
        logger.info("📧 NOTIFICACIÓN - NUEVO ANTEPROYECTO");
        logger.info("===========================================");
        logger.info("Para: {}", jefeEmail);
        logger.info("Asunto: Nuevo anteproyecto registrado");
        logger.info("Mensaje:");
        logger.info("  Se ha registrado un nuevo anteproyecto con ID: {}", anteproyectoId);
        logger.info("===========================================\n");

        // 2) RabbitMQ
        Map<String, Object> payload = new HashMap<>();
        payload.put("destinatario", jefeEmail);
        payload.put("asunto", "Nuevo anteproyecto registrado");
        payload.put("mensaje", "Se ha registrado un nuevo anteproyecto con ID: " + anteproyectoId);
        payload.put("tipo", "NUEVO_ANTEPROYECTO");
        payload.put("fechaCreacion", null);

        rabbitTemplate.convertAndSend(anteproyectoQueue, payload);
        logger.info("📤 Mensaje publicado en RabbitMQ. Cola: {}", anteproyectoQueue);
    }

    @Override
    public void notificarEvaluadoresAsignados(String evaluadorEmail, Long anteproyectoId) {
        // 1) Logs
        logger.info("===========================================");
        logger.info("📧 NOTIFICACIÓN - EVALUADOR ASIGNADO (REQUISITO 8)");
        logger.info("===========================================");
        logger.info("Para: {}", evaluadorEmail);
        logger.info("Asunto: Asignación como evaluador de anteproyecto");
        logger.info("Mensaje:");
        logger.info("  Ha sido designado como evaluador del anteproyecto ID: {}", anteproyectoId);
        logger.info("  Por favor, proceda con la evaluación del documento.");
        logger.info("===========================================\n");

        // 2) RabbitMQ
        Map<String, Object> payload = new HashMap<>();
        payload.put("destinatario", evaluadorEmail);
        payload.put("asunto", "Asignación como evaluador de anteproyecto");
        payload.put("mensaje", "Ha sido designado como evaluador del anteproyecto ID: " + anteproyectoId +
                ". Por favor, proceda con la evaluación del documento.");
        payload.put("tipo", "EVALUADOR_ASIGNADO");
        payload.put("fechaCreacion", null);

        rabbitTemplate.convertAndSend(anteproyectoQueue, payload);
        logger.info("📤 Mensaje de asignación publicado en RabbitMQ. Cola: {}", anteproyectoQueue);
    }
}
