package com.unicauca.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del Notification Service
 * UBICACIÓN: NotificationServiceApplication.java (paquete base)
 */
@SpringBootApplication
public class NotificationServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("NOTIFICATION SERVICE INICIADO");
        System.out.println("===========================================");
        System.out.println("URL: http://localhost:8084");
        System.out.println("Health: http://localhost:8084/actuator/health");
        System.out.println("RabbitMQ: http://localhost:15672");
        System.out.println("===========================================");
        System.out.println("Escuchando colas:");
        System.out.println("   - formato-a-notifications");
        System.out.println("   - anteproyecto-notifications");
        System.out.println("===========================================\n");
    }
}