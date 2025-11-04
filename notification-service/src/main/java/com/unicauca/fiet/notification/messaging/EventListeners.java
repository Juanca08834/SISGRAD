package com.unicauca.fiet.notification.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventListeners {

    @KafkaListener(topics = "events.FormatoASubmitted", groupId = "notification")
    public void handleFormatoASubmitted(String message) {
        log.info("📩 [Listener] Evento recibido: FormatoASubmitted -> {}", message);
    }

    @KafkaListener(topics = "events.FormatoAEvaluated", groupId = "notification")
    public void handleFormatoAEvaluated(String message) {
        log.info("📩 [Listener] Evento recibido: FormatoAEvaluated -> {}", message);
    }

    @KafkaListener(topics = "events.AnteproyectoSubmitted", groupId = "notification")
    public void handleAnteproyectoSubmitted(String message) {
        log.info("📩 [Listener] Evento recibido: AnteproyectoSubmitted -> {}", message);
    }
}
