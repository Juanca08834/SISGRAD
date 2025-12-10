package com.unicauca.formatoa.infrastructure.adapters.out.messaging;

import com.unicauca.formatoa.domain.ports.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Adaptador de notificaciones (Formato A):
 *  - Mantiene el comportamiento anterior: logs simulando email
 *  - Integra RabbitMQ: publica mensajes reales al notification-service
 */
@Component
public class LogNotificationAdapter implements NotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(LogNotificationAdapter.class);

    private final RabbitTemplate rabbitTemplate;
    private final String formatoAQueue;

    public LogNotificationAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.queue.formato-a}") String formatoAQueue
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.formatoAQueue = formatoAQueue;
    }

    @Override
    public void notificarNuevoFormato(String coordinadorEmail, Long formatoAId, String titulo) {
        // 1) Logs (simulación de correo)
        logger.info("===========================================");
        logger.info("NOTIFICACION ENVIADA (simulada) - NUEVO FORMATO A");
        logger.info("===========================================");
        logger.info("Para: {}", coordinadorEmail);
        logger.info("Asunto: Nuevo Formato A registrado");
        logger.info("Mensaje:");
        logger.info("  Se ha registrado un nuevo Formato A con ID: {}", formatoAId);
        logger.info("  Título del proyecto: {}", titulo);
        logger.info("===========================================\n");

        // 2) Mensaje real RabbitMQ (JSON)
        Map<String, Object> payload = new HashMap<>();
        payload.put("destinatario", coordinadorEmail);
        payload.put("asunto", "Nuevo Formato A registrado");
        payload.put("mensaje", "Se ha registrado un nuevo Formato A (ID: " + formatoAId + ") con título: " + titulo);
        payload.put("tipo", "NUEVO_FORMATO_A");
        payload.put("fechaCreacion", null); // intencional: evita problemas de deserialización con LocalDateTime

        rabbitTemplate.convertAndSend(formatoAQueue, payload);
        logger.info("[OK] Mensaje publicado en RabbitMQ. Cola: {}", formatoAQueue);
    }

    @Override
    public void notificarEvaluacion(String docenteEmail, Long formatoAId, String resultado, String observaciones) {
        // 1) Logs (simulación de correo)
        logger.info("===========================================");
        logger.info("NOTIFICACION ENVIADA (simulada) - EVALUACION FORMATO A");
        logger.info("===========================================");
        logger.info("Para: {}", docenteEmail);
        logger.info("Asunto: Evaluación de Formato A - {}", resultado);
        logger.info("Mensaje:");
        logger.info("  Su Formato A (ID: {}) ha sido evaluado", formatoAId);
        logger.info("  - Resultado: {}", resultado);
        logger.info("  - Observaciones: {}", observaciones);
        logger.info("===========================================\n");

        // 2) Mensaje real RabbitMQ (JSON)
        Map<String, Object> payload = new HashMap<>();
        payload.put("destinatario", docenteEmail);
        payload.put("asunto", "Evaluación de Formato A - " + resultado);
        payload.put("mensaje",
                "Su Formato A (ID: " + formatoAId + ") ha sido evaluado.\n" +
                "Resultado: " + resultado + "\n" +
                "Observaciones: " + (observaciones == null ? "" : observaciones)
        );
        payload.put("tipo", "EVALUACION_FORMATO_A");
        payload.put("fechaCreacion", null);

        rabbitTemplate.convertAndSend(formatoAQueue, payload);
        logger.info("[OK] Mensaje de evaluación publicado en RabbitMQ. Cola: {}", formatoAQueue);
    }
}
