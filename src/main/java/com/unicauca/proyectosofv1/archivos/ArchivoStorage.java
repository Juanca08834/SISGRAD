package com.unicauca.proyectosofv1.infraestructura.archivos;

import java.io.IOException;
import java.nio.file.*;

public final class ArchivoStorage {
    private final Path base;

    public ArchivoStorage(Path base) {
        this.base = base;
    }

    /** Carpeta por defecto: ./data/uploads  (relativa al directorio de ejecución) */
    public static ArchivoStorage porDefecto() {
        return new ArchivoStorage(Paths.get("data", "uploads"));
    }

    public String guardarPdf(String subcarpeta, Path origen, String nombreDestinoSinExt) throws IOException {
        if (origen == null || !Files.exists(origen) || !Files.isRegularFile(origen)) {
            throw new IOException("Archivo origen no existe: " + origen);
        }
        String nombre = sanitizar(nombreDestinoSinExt) + ".pdf";
        Path carpeta = base.resolve(subcarpeta == null ? "" : subcarpeta);
        Files.createDirectories(carpeta);

        Path destino = carpeta.resolve(nombre);
        // Si ya existe, sobreescribe (útil en reintentos)
        Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
        return destino.toAbsolutePath().toString();
    }

    private static String sanitizar(String s) {
        String t = (s == null ? "" : s).trim().toLowerCase();
        t = java.text.Normalizer.normalize(t, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        t = t.replaceAll("[^a-z0-9._-]+", "_");
        if (t.isBlank()) t = "archivo";
        return t;
    }
}
