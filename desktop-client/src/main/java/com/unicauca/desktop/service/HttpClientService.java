package com.unicauca.desktop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unicauca.desktop.config.AppConfig;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Cliente HTTP para comunicación con los microservicios
 */
public class HttpClientService {

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpClientService() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Realiza una petición GET
     */
    public String get(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        addAuthHeader(request);
        request.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode >= 200 && statusCode < 300) {
                return responseBody;
            } else {
                throw new IOException("HTTP Error " + statusCode + ": " + responseBody);
            }
        } catch (ParseException e) {
            throw new IOException("Error parsing response", e);
        }
    }

    /**
     * Realiza una petición POST
     */
    public String post(String url, Object body) throws IOException {
        HttpPost request = new HttpPost(url);
        addAuthHeader(request);
        request.setHeader("Content-Type", "application/json");

        if (body != null) {
            String jsonBody = objectMapper.writeValueAsString(body);
            request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        }

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode >= 200 && statusCode < 300) {
                return responseBody;
            } else {
                throw new IOException("HTTP Error " + statusCode + ": " + responseBody);
            }
        } catch (ParseException e) {
            throw new IOException("Error parsing response", e);
        }
    }

    /**
     * Realiza una petición PUT
     */
    public String put(String url, Object body) throws IOException {
        HttpPut request = new HttpPut(url);
        addAuthHeader(request);
        request.setHeader("Content-Type", "application/json");

        if (body != null) {
            String jsonBody = objectMapper.writeValueAsString(body);
            request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        }

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode >= 200 && statusCode < 300) {
                return responseBody;
            } else {
                throw new IOException("HTTP Error " + statusCode + ": " + responseBody);
            }
        } catch (ParseException e) {
            throw new IOException("Error parsing response", e);
        }
    }

    /**
     * Realiza una petición DELETE
     */
    public String delete(String url) throws IOException {
        HttpDelete request = new HttpDelete(url);
        addAuthHeader(request);
        request.setHeader("Content-Type", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode >= 200 && statusCode < 300) {
                return responseBody;
            } else {
                throw new IOException("HTTP Error " + statusCode + ": " + responseBody);
            }
        } catch (ParseException e) {
            throw new IOException("Error parsing response", e);
        }
    }

    /**
     * Agrega el header de autenticación si existe token
     */
    private void addAuthHeader(HttpUriRequestBase request) {
        String token = AppConfig.getAuthToken();
        if (token != null && !token.isEmpty()) {
            request.setHeader("Authorization", "Bearer " + token);
        }
    }

    /**
     * Convierte JSON a objeto
     */
    public <T> T parseJson(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * Cierra el cliente HTTP
     */
    public void close() throws IOException {
        httpClient.close();
    }
}

