package com.unicauca.fiet.notification.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Emisor de correo simulado que deja evidencia en logs (perfil dev). */
@Component
@Profile("!kafka")
public class LoggerEmailSender implements EmailSender {
    private static final Logger log = LoggerFactory.getLogger(LoggerEmailSender.class);
    @Override public void send(String to, String subject, String body) {
        log.info("[DEV] Email simulado -> to={} | subject={} | body={}", to, subject, body);
    }
}
