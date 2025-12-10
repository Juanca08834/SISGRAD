package com.unicauca.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unicauca.desktop.config.AppConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestión de Formatos A
 */
public class FormatoService {

    private final HttpClientService httpClient;
    private final ObjectMapper objectMapper;

    public FormatoService() {
        this.httpClient = new HttpClientService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Crea un nuevo Formato A
     */
    public Map<String, Object> crearFormato(Map<String, Object> formatoData) throws IOException {
        String url = AppConfig.FORMATO_SERVICE_URL;
        String response = httpClient.post(url, formatoData);
        return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Obtiene un Formato A por ID
     */
    public Map<String, Object> obtenerFormato(Long id) throws IOException {
        String url = AppConfig.FORMATO_SERVICE_URL + "/" + id;
        String response = httpClient.get(url);
        return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Obtiene todos los Formatos A de un docente
     */
    public List<Map<String, Object>> obtenerFormatosPorDocente(Long docenteId) throws IOException {
        String url = AppConfig.FORMATO_SERVICE_URL + "/docente/" + docenteId;
        String response = httpClient.get(url);
        return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * Obtiene todos los Formatos A pendientes de evaluación
     */
    public List<Map<String, Object>> obtenerFormatosPendientes() throws IOException {
        String url = AppConfig.FORMATO_SERVICE_URL + "/pendientes";
        String response = httpClient.get(url);
        return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * Evalúa un Formato A
     */
    public Map<String, Object> evaluarFormato(Map<String, Object> evaluacionData) throws IOException {
        String url = AppConfig.FORMATO_SERVICE_URL + "/evaluaciones";
        String response = httpClient.post(url, evaluacionData);
        return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Crea una nueva versión de un Formato A
     */
    public Map<String, Object> crearNuevaVersion(Long id, Map<String, Object> formatoData) throws IOException {
        String url = AppConfig.FORMATO_SERVICE_URL + "/" + id + "/nueva-version";
        String response = httpClient.post(url, formatoData);
        return objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Obtiene todos los Formatos A (para estudiantes ver estado de proyecto)
     */
    public List<Map<String, Object>> obtenerTodosLosFormatos() throws IOException {
        String url = AppConfig.FORMATO_SERVICE_URL;
        String response = httpClient.get(url);
        return objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
    }
}

