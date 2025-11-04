package com.unicauca.fiet.notification.facade;

import com.unicauca.fiet.notification.email.EmailSender;
import org.springframework.stereotype.Component;

/**
 * Facade (GoF) para simplificar el envío de notificaciones por email.
 * Permite centralizar formateo de mensajes y manejar varios canales en el futuro.
 */
@Component
public class NotificationFacade {
    private final EmailSender emailSender;
    public NotificationFacade(EmailSender emailSender) { this.emailSender = emailSender; }

    /** Envía una notificación simple por email. */
    public void notifyByEmail(String to, String subject, String body) {
        emailSender.send(to, subject, body);
    }
}
