package com.unicauca.proyectosofv1.plugins.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReportPluginManager {
    private final Map<String,String> registry = new HashMap<>();

    public ReportPluginManager() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("plugin.properties")) {
            if (in == null) throw new IllegalStateException("No se encontró plugin.properties");
            Properties p = new Properties(); p.load(in);
            for (String k : p.stringPropertyNames()) {
                registry.put(k.trim(), p.getProperty(k).trim());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando plugin.properties", e);
        }
    }

    public IReportPlugin get(String key) {
        try {
            String fqcn = registry.get(key);
            if (fqcn == null) throw new IllegalArgumentException("No hay plugin con clave: " + key);
            return (IReportPlugin) Class.forName(fqcn).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo instanciar el plugin: " + key, e);
        }
    }
}