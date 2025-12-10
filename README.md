# Sistema de Gestión de Anteproyectos de Trabajo de Grado

Sistema basado en microservicios para la gestión de anteproyectos de trabajo de grado en la Universidad del Cauca.

## 📋 Descripción

Este proyecto implementa un sistema completo para gestionar el ciclo de vida de los anteproyectos de trabajo de grado, desde la creación del Formato A hasta la asignación de evaluadores.

## 🏗️ Arquitectura

El sistema utiliza una arquitectura de microservicios con los siguientes componentes:
Api Gateway: 8080
Auth: 8081
Formato A: 8082
Anteproyecto: 8083
Notification: 8084

## 🛠️ Tecnologías

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Cloud Gateway**
- **Spring Security + JWT**
- **Maven**
- **PostgreSQL / MySQL**
- **Swagger/OpenAPI 3.0**

## 📦 Microservicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| `api-gateway` | 8080 | Punto de entrada único |
| `auth-service` | 8081 | Autenticación y autorización |
| `formato-a-service` | 8082 | Gestión de Formato A |
| `anteproyecto-service` | 8083 | Gestión de anteproyectos |

## 🚀 Instalación

### Prerrequisitos

- JDK 17 o superior
- Maven 3.8+
- Base de datos (PostgreSQL/MySQL)

### Compilar el proyecto

```mvn clean install```

# Ejecutar los servicios

# Terminal 1 - Auth Service
cd auth-service
mvn spring-boot:run

# Terminal 2 - Formato A Service
cd formato-a-service
mvn spring-boot:run

# Terminal 3 - Anteproyecto Service
cd anteproyecto-service
mvn spring-boot:run

# Terminal 4 - API Gateway
cd api-gateway
mvn spring-boot:run

# 📚 Documentacion API

Anteproyecto: http://localhost:8083/swagger-ui.html

Formato A: http://localhost:8082/swagger-ui.html

Auth: http://localhost:8081/swagger-ui.html

# 🔐 Autenticación

El sistema utiliza JWT para autenticación:
### Obtener token
```
POST /api/auth/login
{
  "username": "usuario",
  "password": "contraseña"
}
```
### Usar token en peticiones
Authorization: Bearer <token>
