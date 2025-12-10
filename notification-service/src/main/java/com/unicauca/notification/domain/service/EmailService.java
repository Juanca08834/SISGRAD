package com.unicauca.notification.domain.service;

import com.unicauca.notification.domain.model.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio para envío de emails (simulado con logger avanzado)
 * UBICACIÓN: domain/service/EmailService.java
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void enviarEmail(NotificationMessage notificacion) {
        logger.info("\n" +
            "╔════════════════════════════════════════════════════════════════╗\n" +
            "║           NOTIFICACION ENVIADA (RabbitMQ)                      ║\n" +
            "╠════════════════════════════════════════════════════════════════╣\n" +
            "║ Para:    {:<52} ║\n" +
            "║ Asunto:  {:<52} ║\n" +
            "║ Tipo:    {:<52} ║\n" +
            "║ Fecha:   {:<52} ║\n" +
            "╠════════════════════════════════════════════════════════════════╣\n" +
            "║ MENSAJE:                                                       ║\n" +
            "║ {:<62} ║\n" +
            "╚════════════════════════════════════════════════════════════════╝\n",
            notificacion.getDestinatario(),
            notificacion.getAsunto(),
            notificacion.getTipo(),
            notificacion.getFechaCreacion(),
            notificacion.getMensaje()
        );

        // Aquí iría la lógica real de envío de email
        // Por ejemplo: JavaMailSender, SendGrid, AWS SES, etc.
        
        simularEnvioEmail(notificacion);
    }

    private void simularEnvioEmail(NotificationMessage notificacion) {
        try {
            // Simular tiempo de envío
            Thread.sleep(500);
            logger.info("[OK] Email enviado exitosamente a: {}", notificacion.getDestinatario());
        } catch (InterruptedException e) {
            logger.error("[ERROR] Error al enviar email: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}