package com.unicauca.proyectosofv1.infraestructura.sqlite;

import java.sql.*;

public final class FabricaConexionSQLite {
    private FabricaConexionSQLite() {}

    public static Connection obtenerConexion() {
        try {
            String url = "jdbc:sqlite:proyectoSofV1.db"; // Archivo en la carpeta del proyecto
            Connection conn = DriverManager.getConnection(url);
            // IMPORTANT: El esquema se gestiona vía scripts en resources (DBInit), no aquí.
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo abrir conexión SQLite", e);
        }
    }
}