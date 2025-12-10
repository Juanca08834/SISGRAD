package com.unicauca.anteproyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del Anteproyecto Service
 * UBICACIÓN: AnteproyectoServiceApplication.java (paquete base)
 */
@SpringBootApplication
public class AnteproyectoServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AnteproyectoServiceApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("🚀 ANTEPROYECTO SERVICE INICIADO");
        System.out.println("===========================================");
        System.out.println("📍 URL: http://localhost:8083");
        System.out.println("📚 Swagger UI: http://localhost:8083/swagger-ui.html");
        System.out.println("🔍 API Docs: http://localhost:8083/api-docs");
        System.out.println("❤️  Health: http://localhost:8083/actuator/health");
        System.out.println("🗃️  H2 Console: http://localhost:8083/h2-console");
        System.out.println("===========================================");
        System.out.println("⭐ REQUISITO 8 implementado:");
        System.out.println("   POST /api/anteproyectos/{id}/asignar-evaluadores");
        System.out.println("===========================================\n");
    }
}