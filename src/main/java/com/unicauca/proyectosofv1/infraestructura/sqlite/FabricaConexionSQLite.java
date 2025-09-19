package com.unicauca.proyectosofv1.infraestructura.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Fabrica de conexiones SQLite.
 * - Usa un archivo local "proyectoSofV1.db".
 * - Activa FOREIGN KEYS.
 * - Inicializa el esquema (idempotente).
 * - Expone obtenerConexion() y un alias crearConexion() para compatibilidad.
 */
public final class FabricaConexionSQLite {
    private static final String URL = "jdbc:sqlite:proyectoSofV1.db";

    private FabricaConexionSQLite() {}

    /** Conexion nueva a la base de datos (con FK activas e inicializacion de tablas). */
    public static Connection obtenerConexion() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            inicializarEsquema(conn);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo abrir conexion SQLite", e);
        }
    }

    /** Alias por compatibilidad con codigo que llame crearConexion(). */
    public static Connection crearConexion() {
        return obtenerConexion();
    }

    /** Crea las tablas si no existen (idempotente). */
    private static void inicializarEsquema(Connection conn) throws SQLException {
        String[] ddls = new String[] {
            // usuarios
            """
            CREATE TABLE IF NOT EXISTS usuarios (
              email TEXT PRIMARY KEY,
              nombres TEXT NOT NULL,
              apellidos TEXT NOT NULL,
              celular TEXT NULL,
              programa TEXT NOT NULL,
              rol TEXT NOT NULL,
              password_hash TEXT NOT NULL,
              creado_en TEXT NOT NULL
            )
            """,
            // proyecto
            """
            CREATE TABLE IF NOT EXISTS proyecto(
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              docente_email TEXT NOT NULL,
              estado TEXT NOT NULL,
              intentos INTEGER NOT NULL
            )
            """,
            // formato_a
            """
            CREATE TABLE IF NOT EXISTS formato_a(
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              proyecto_id INTEGER NOT NULL,
              titulo TEXT NOT NULL,
              modalidad TEXT NOT NULL,
              fecha TEXT NOT NULL,
              director TEXT NOT NULL,
              codirector TEXT,
              objetivo_general TEXT,
              objetivos_especificos TEXT,
              pdf_path TEXT NOT NULL,
              carta_path TEXT,
              FOREIGN KEY(proyecto_id) REFERENCES proyecto(id)
            )
            """
        };

        try (Statement st = conn.createStatement()) {
            for (String ddl : ddls) {
                st.executeUpdate(ddl);
            }
        }
    }
}
