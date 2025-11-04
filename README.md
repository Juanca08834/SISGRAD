# FIET - Sistema de Gestión de Trabajos de Grado (Microservicios)
Proyecto multi-módulo Maven para IntelliJ (Java 21, Spring Boot 3.3.x). Incluye:
- `identity-service`: registro e inicio de sesión de docentes con validación de contraseña
- `project-service`: envío y versionamiento del Formato A, reglas de negocio y eventos
- `evaluation-service`: evaluación del Formato A por parte del coordinador
- `document-service`: envío del anteproyecto
- `notification-service`: servicio de notificaciones (simulado por logs en perfil `dev`)
- `api-gateway`: puerta de entrada (Spring Cloud Gateway)
- `discovery-server`: registro de servicios (Eureka)
- `common`: eventos y contratos compartidos

## Perfiles
- `dev` (por defecto): usa H2 en memoria y simula eventos (no requiere Kafka).
- `kafka`: produce/consume eventos en Apache Kafka (requiere Docker).

## Inicio rápido (perfil `dev`)
1. Java 21 + Maven 3.9+ + IntelliJ + plugin Lombok
2. En IntelliJ: *Open* → seleccione este `pom.xml` raíz.
3. Ejecute, en este orden:
   - `discovery-server`
   - `api-gateway`
   - `identity-service`, `project-service`, `evaluation-service`,
     `document-service`, `notification-service`

## Swagger (OpenAPI)
- Identity: <http://localhost:8081/swagger-ui/index.html>
- Project: <http://localhost:8082/swagger-ui/index.html>
- Evaluation: <http://localhost:8083/swagger-ui/index.html>
- Document: <http://localhost:8084/swagger-ui/index.html>
- Notification: <http://localhost:8085/swagger-ui/index.html>
