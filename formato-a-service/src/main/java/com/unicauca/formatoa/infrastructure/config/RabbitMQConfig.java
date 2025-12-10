package com.unicauca.formatoa.infrastructure.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración RabbitMQ (Producer) - Formato A
 * Se define:
 *  - Cola (si no existe)
 *  - Converter JSON
 *  - RabbitTemplate con JSON
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.formato-a}")
    private String formatoAQueue;

    @Bean
    public Queue formatoAQueue() {
        return new Queue(formatoAQueue, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
