package com.unicauca.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unicauca.desktop.config.AppConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestión de Anteproyectos
 */
public class AnteproyectoService {

    private final HttpClientService httpClient;
    private final ObjectMapper objectMapper;

    public AnteproyectoService() {
        this.httpClient = new HttpClientService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Crea un nuevo anteproyecto
     */
    public Map<String, Object> crearAnteproyecto(Map<String, Object> anteproyectoData) throws IOException {
        String url = AppConfig.ANTEPROYECTO_SERVICE_URL;
        String response = httpClient.post(url, anteproyectoData);
        return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Obtiene un anteproyecto por ID
     */
    public Map<String, Object> obtenerAnteproyecto(Long id) throws IOException {
        String url = AppConfig.ANTEPROYECTO_SERVICE_URL + "/" + id;
        String response = httpClient.get(url);
        return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Obtiene todos los anteproyectos
     */
    public List<Map<String, Object>> obtenerTodosAnteproyectos() throws IOException {
        String url = AppConfig.ANTEPROYECTO_SERVICE_URL;
        String response = httpClient.get(url);
        return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * Asigna evaluadores a un anteproyecto
     */
    public List<Map<String, Object>> asignarEvaluadores(Long id, List<Map<String, Object>> evaluadores) throws IOException {
        String url = AppConfig.ANTEPROYECTO_SERVICE_URL + "/" + id + "/asignar-evaluadores";

        Map<String, Object> request = new java.util.HashMap<>();
        request.put("anteproyectoId", id);
        request.put("evaluadores", evaluadores);

        String response = httpClient.post(url, request);
        return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
    }
}

