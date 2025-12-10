package com.unicauca.notification.infrastructure.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ
 * UBICACIÓN: infrastructure/config/RabbitMQConfig.java
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.formato-a:formato-a-notifications}")
    private String formatoAQueue;

    @Value("${rabbitmq.queue.anteproyecto:anteproyecto-notifications}")
    private String anteproyectoQueue;

    /**
     * Cola para notificaciones de Formato A
     */
    @Bean
    public Queue formatoAQueue() {
        return new Queue(formatoAQueue, true); // durable = true
    }

    /**
     * Cola para notificaciones de Anteproyecto
     */
    @Bean
    public Queue anteproyectoQueue() {
        return new Queue(anteproyectoQueue, true);
    }

    /**
     * Converter para serializar/deserializar JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate con converter JSON
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}