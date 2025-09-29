package com.unicauca.proyectosofv1.infraestructura.sqlite;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class FabricaConexionSQLiteTest {

    private Path tempDb;

    @AfterEach
    void cleanUp() throws Exception {
        // eliminar property si fue puesta
        System.clearProperty("sisgrad.db.path");
        if (tempDb != null) {
            // intenta borrar el archivo si existe
            try {
                Files.deleteIfExists(tempDb);
            } catch (Exception e) {
                // en Windows puede fallar si aún hay un handle abierto;
                // dejamos que no rompa el resto de la suite, pero re-lanzamos
                // si realmente queremos forzar la limpieza en entornos CI:
                // throw e;
            }
            tempDb = null;
        }
    }

    @Test
    void obtenerConexion_conRutaPorDefecto_funciona() throws Exception {
        // Usar ruta por defecto (producirá un archivo 'proyectoSofV1.db' en cwd)
        try (Connection conn = FabricaConexionSQLite.obtenerConexion()) {
            assertNotNull(conn);
            assertDoesNotThrow(() -> conn.createStatement().execute("SELECT 1"));
        }
    }

    @Test
    void obtenerConexion_usandoSystemProperty_funciona() throws Exception {
        // Crear un archivo temporal y usar su ruta como la DB (sqlite creará/abrirá)
        tempDb = Files.createTempFile("sisgrad-test", ".db");
        // borrar el archivo temporal para que la fábrica cree uno nuevo en la misma ruta
        Files.deleteIfExists(tempDb);

        System.setProperty("sisgrad.db.path", tempDb.toString());

        try (Connection conn = FabricaConexionSQLite.obtenerConexion()) {
            assertNotNull(conn);
            // la fábrica debería haber creado el archivo
            assertTrue(Files.exists(tempDb), "El archivo DB debería existir en la ruta temporal");
            assertDoesNotThrow(() -> conn.createStatement().execute("SELECT 1"));
        }
    }

    @Test
    void obtenerConexion_rutaInvalida_lanzaRuntime() throws Exception {
        // Queremos forzar que Files.createDirectories(...) falle.
        // Estrategia: crear un archivo normal "parentFile" y luego
        // pasar como dbPath: parentFile + "/illegal.db".
        // Entonces abs.getParent() será el path del archivo (no un directorio)
        // y createDirectories(parent) lanzará FileAlreadyExistsException,
        // que la fábrica captura y convierte en RuntimeException.

        Path parentFile = Files.createTempFile("sisgrad-parent-file", ".tmp");
        try {
            String badPath = parentFile.toString() + "/illegal.db";
            System.setProperty("sisgrad.db.path", badPath);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    FabricaConexionSQLite::obtenerConexion);
            assertTrue(ex.getMessage().contains("No se pudo abrir conexión SQLite"));
        } finally {
            // borrar el archivo que usamos como "parent" (si existe)
            Files.deleteIfExists(parentFile);
        }
    }
}
