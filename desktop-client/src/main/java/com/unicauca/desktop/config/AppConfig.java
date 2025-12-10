package com.unicauca.desktop.config;

/**
 * Configuración global de la aplicación
 */
public class AppConfig {

    // URLs de los microservicios
    public static final String AUTH_SERVICE_URL = "http://localhost:8081/api/auth";
    public static final String FORMATO_SERVICE_URL = "http://localhost:8082/api/formatos";
    public static final String ANTEPROYECTO_SERVICE_URL = "http://localhost:8083/api/anteproyectos";

    // Timeout para peticiones HTTP (en segundos)
    public static final int HTTP_TIMEOUT = 30;

    // Información de la aplicación
    public static final String APP_NAME = "Sistema de Gestión de Trabajos de Grado";
    public static final String APP_VERSION = "1.0.0";

    // Token de sesión (se almacena en memoria durante la ejecución)
    private static String authToken = null;
    private static String userEmail = null;
    private static String userRole = null;
    private static Long userId = null;
    private static String userName = null;

    public static void setSession(String token, String email, String role, Long id, String name) {
        authToken = token;
        userEmail = email;
        userRole = role;
        userId = id;
        userName = name;
    }

    public static void clearSession() {
        authToken = null;
        userEmail = null;
        userRole = null;
        userId = null;
        userName = null;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static String getUserRole() {
        return userRole;
    }

    public static Long getUserId() {
        return userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static boolean isLoggedIn() {
        return authToken != null && !authToken.isEmpty();
    }
}

