package com.unicauca.anteproyecto.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger/OpenAPI
 * UBICACIÓN: infrastructure/config/SwaggerConfig.java
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Anteproyecto Service API")
                .version("1.0.0")
                .description("""
                    API REST para gestión de Anteproyectos de Trabajo de Grado.
                    
                    **Funcionalidades principales:**
                    - **REQUISITO 6:** Docente sube anteproyecto después de aprobación del Formato A
                    - **REQUISITO 7:** Jefe de departamento lista anteproyectos para asignar evaluadores
                    - **REQUISITO 8:** Jefe de departamento asigna 2 docentes evaluadores
                    
                    **Autenticación:**
                    Todas las operaciones requieren un token JWT válido en el header Authorization.
                    """)
            )
            .servers(List.of(
                new Server().url("http://localhost:8083").description("Servidor de desarrollo"),
                new Server().url("http://localhost:8080").description("API Gateway")
            ))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Ingrese el token JWT obtenido del auth-service")
                )
            );
    }
}