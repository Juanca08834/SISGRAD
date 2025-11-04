package com.unicauca.fiet.notification.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Emisor de correo simulado para el perfil kafka (solo deja evidencia en logs). */
@Slf4j
@Component
@Profile("kafka")
public class KafkaEmailSender implements EmailSender {

    @Override
    public void send(String to, String subject, String body) {
        log.info("[KAFKA] Email simulado -> to={} | subject={} | body={}", to, subject, body);
    }
}
