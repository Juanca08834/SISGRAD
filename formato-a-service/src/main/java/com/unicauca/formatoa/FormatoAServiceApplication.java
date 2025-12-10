package com.unicauca.formatoa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del Formato A Service
 */
@SpringBootApplication
public class FormatoAServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FormatoAServiceApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("FORMATO A SERVICE INICIADO CORRECTAMENTE");
        System.out.println("===========================================");
        System.out.println("URL: http://localhost:8082");
        System.out.println("Swagger UI: http://localhost:8082/swagger-ui.html");
        System.out.println("API Docs: http://localhost:8082/api-docs");
        System.out.println("Health: http://localhost:8082/actuator/health");
        System.out.println("H2 Console: http://localhost:8082/h2-console");
        System.out.println("===========================================\n");
    }
}