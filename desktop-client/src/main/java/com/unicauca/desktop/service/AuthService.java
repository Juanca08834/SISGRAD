package com.unicauca.desktop.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicauca.desktop.config.AppConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para autenticación y gestión de usuarios
 */
public class AuthService {

    private final HttpClientService httpClient;
    private final ObjectMapper objectMapper;

    public AuthService() {
        this.httpClient = new HttpClientService();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Realiza el login de un usuario
     */
    public boolean login(String email, String password) throws IOException {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("password", password);

        String url = AppConfig.AUTH_SERVICE_URL + "/login";
        String response = httpClient.post(url, loginData);

        // Parse response
        JsonNode jsonNode = objectMapper.readTree(response);
        String token = jsonNode.get("token").asText();
        String userEmail = jsonNode.get("email").asText();
        String rol = jsonNode.get("rol").asText();
        Long id = jsonNode.get("id").asLong();
        String nombres = jsonNode.get("nombres").asText();
        String apellidos = jsonNode.get("apellidos").asText();
        String fullName = nombres + " " + apellidos;

        // Guardar sesión
        AppConfig.setSession(token, userEmail, rol, id, fullName);

        return true;
    }

    /**
     * Registra un nuevo usuario
     */
    public boolean register(Map<String, Object> userData) throws IOException {
        String url = AppConfig.AUTH_SERVICE_URL + "/registro";
        String response = httpClient.post(url, userData);

        // Parse response
        JsonNode jsonNode = objectMapper.readTree(response);
        String token = jsonNode.get("token").asText();
        String userEmail = jsonNode.get("email").asText();
        String rol = jsonNode.get("rol").asText();
        Long id = jsonNode.get("id").asLong();
        String nombres = jsonNode.get("nombres").asText();
        String apellidos = jsonNode.get("apellidos").asText();
        String fullName = nombres + " " + apellidos;

        // Guardar sesión automáticamente después de registrarse
        AppConfig.setSession(token, userEmail, rol, id, fullName);

        return true;
    }

    /**
     * Cierra la sesión actual
     */
    public void logout() {
        AppConfig.clearSession();
    }

    /**
     * Valida si el token actual es válido
     */
    public boolean validateToken() throws IOException {
        if (!AppConfig.isLoggedIn()) {
            return false;
        }

        try {
            String url = AppConfig.AUTH_SERVICE_URL + "/validar";
            httpClient.get(url);
            return true;
        } catch (IOException e) {
            // Token inválido o expirado
            AppConfig.clearSession();
            return false;
        }
    }
}

