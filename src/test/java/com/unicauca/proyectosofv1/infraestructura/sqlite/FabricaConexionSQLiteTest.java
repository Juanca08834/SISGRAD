package com.unicauca.proyectosofv1.infraestructura.sqlite;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para FabricaConexionSQLite")
class FabricaConexionSQLiteTest {

    private static final String TABLA_USUARIOS = "usuarios";
    private Connection conexion;

    @TempDir
    Path directorioTemporal;

    @BeforeEach
    void setUp() {
        String directorioOriginal = System.getProperty("user.dir");
        System.setProperty("user.dir", directorioTemporal.toString());
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }

        File dbFile = new File("proyectoSofV1.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    @DisplayName("Debe crear conexión SQLite exitosamente")
    void debeCrearConexionSQLiteExitosamente() throws SQLException {
        conexion = assertDoesNotThrow(() -> FabricaConexionSQLite.obtenerConexion());

        assertNotNull(conexion);
        assertFalse(conexion.isClosed());
        assertTrue(conexion.isValid(5));
    }

    @Test
    @DisplayName("Debe crear archivo de base de datos")
    void debeCrearArchiveDeBaseDeDatos() {
        conexion = FabricaConexionSQLite.obtenerConexion();

        File dbFile = new File("proyectoSofV1.db");
        assertTrue(dbFile.exists());
        assertTrue(dbFile.isFile());
        assertTrue(dbFile.length() > 0);
    }

    @Test
    @DisplayName("Debe inicializar esquema con tabla usuarios")
    void debeInicializarEsquemaConTablaUsuarios() throws SQLException {
        conexion = FabricaConexionSQLite.obtenerConexion();

        DatabaseMetaData metaData = conexion.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, TABLA_USUARIOS, null)) {
            assertTrue(tables.next(), "La tabla 'usuarios' debería existir");
            assertEquals(TABLA_USUARIOS, tables.getString("TABLE_NAME"));
        }
    }

    @Test
    @DisplayName("Debe crear tabla usuarios con todas las columnas requeridas")
    void debeCrearTablaUsuariosConTodasLasColumnasRequeridas() throws SQLException {
        conexion = FabricaConexionSQLite.obtenerConexion();

        DatabaseMetaData metaData = conexion.getMetaData();
        try (ResultSet columns = metaData.getColumns(null, null, TABLA_USUARIOS, null)) {

            String[] columnasEsperadas = {"email", "nombres", "apellidos", "celular",
                    "programa", "rol", "password_hash", "creado_en"};

            boolean[] columnasEncontradas = new boolean[columnasEsperadas.length];

            while (columns.next()) {
                String nombreColumna = columns.getString("COLUMN_NAME");
                for (int i = 0; i < columnasEsperadas.length; i++) {
                    if (columnasEsperadas[i].equals(nombreColumna)) {
                        columnasEncontradas[i] = true;
                        break;
                    }
                }
            }

            for (int i = 0; i < columnasEsperadas.length; i++) {
                assertTrue(columnasEncontradas[i],
                        "La columna '" + columnasEsperadas[i] + "' debería existir");
            }
        }
    }

    @Test
    @DisplayName("Debe configurar email como clave primaria")
    void debeConfigurarEmailComoClavePrimaria() throws SQLException {
        conexion = FabricaConexionSQLite.obtenerConexion();

        DatabaseMetaData metaData = conexion.getMetaData();
        try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, TABLA_USUARIOS)) {
            assertTrue(primaryKeys.next(), "Debe existir una clave primaria");
            assertEquals("email", primaryKeys.getString("COLUMN_NAME"));
            assertFalse(primaryKeys.next(), "Solo debe haber una columna como clave primaria");
        }
    }

    @Test
    @DisplayName("Debe permitir valores null en columna celular")
    void debePermitirValoresNullEnColumnaCelular() throws SQLException {
        conexion = FabricaConexionSQLite.obtenerConexion();

        String sql = """
            INSERT INTO usuarios (email, nombres, apellidos, celular, programa, rol, password_hash, creado_en)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, "test.null@unicauca.edu.co");
            stmt.setString(2, "María");
            stmt.setString(3, "García");
            stmt.setNull(4, Types.VARCHAR); // Celular NULL
            stmt.setString(5, "Ingeniería Electrónica y Telecomunicaciones");
            stmt.setString(6, "Docente");
            stmt.setString(7, "hashedPassword2");
            stmt.setString(8, "2024-01-02T10:00:00");

            int filasAfectadas = assertDoesNotThrow(() -> stmt.executeUpdate());
            assertEquals(1, filasAfectadas);
        }
    }

    @Test
    @DisplayName("Debe rechazar emails duplicados por clave primaria")
    void debeRechazarEmailsDuplicadosPorClavePrimaria() throws SQLException {
        conexion = FabricaConexionSQLite.obtenerConexion();

        String sql = """
            INSERT INTO usuarios (email, nombres, apellidos, celular, programa, rol, password_hash, creado_en)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, "duplicado@unicauca.edu.co");
            stmt.setString(2, "Usuario1");
            stmt.setString(3, "Apellido1");
            stmt.setString(4, "3001111111");
            stmt.setString(5, "Ingeniería de Sistemas");
            stmt.setString(6, "Estudiante");
            stmt.setString(7, "hash1");
            stmt.setString(8, "2024-01-01T10:00:00");

            stmt.executeUpdate();
        }

        SQLException excepcion = assertThrows(SQLException.class, () -> {
            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, "duplicado@unicauca.edu.co"); // Email duplicado
                stmt.setString(2, "Usuario2");
                stmt.setString(3, "Apellido2");
                stmt.setString(4, "3002222222");
                stmt.setString(5, "Automática industrial");
                stmt.setString(6, "Docente");
                stmt.setString(7, "hash2");
                stmt.setString(8, "2024-01-02T10:00:00");

                stmt.executeUpdate();
            }
        });

        assertTrue(excepcion.getMessage().contains("UNIQUE constraint failed") ||
                excepcion.getMessage().contains("PRIMARY KEY constraint failed"));
    }

    @Test
    @DisplayName("Debe crear múltiples conexiones independientes")
    void debeCrearMultiplesConexionesIndependientes() throws SQLException {
        Connection conexion1 = FabricaConexionSQLite.obtenerConexion();
        Connection conexion2 = FabricaConexionSQLite.obtenerConexion();

        assertNotNull(conexion1);
        assertNotNull(conexion2);
        assertNotSame(conexion1, conexion2, "Deben ser instancias diferentes");

        assertTrue(conexion1.isValid(5));
        assertTrue(conexion2.isValid(5));

        conexion1.close();
        conexion2.close();
    }

    @Test
    @DisplayName("Debe manejar múltiples llamadas de inicialización sin error")
    void debeManejarMultiplesLlamadasDeInicializacionSinError() {
        Connection conn1 = assertDoesNotThrow(() -> FabricaConexionSQLite.obtenerConexion());
        Connection conn2 = assertDoesNotThrow(() -> FabricaConexionSQLite.obtenerConexion());
        Connection conn3 = assertDoesNotThrow(() -> FabricaConexionSQLite.obtenerConexion());

        assertAll(
                () -> assertNotNull(conn1),
                () -> assertNotNull(conn2),
                () -> assertNotNull(conn3),
                () -> assertTrue(conn1.isValid(5)),
                () -> assertTrue(conn2.isValid(5)),
                () -> assertTrue(conn3.isValid(5))
        );

        assertDoesNotThrow(() -> {
            conn1.close();
            conn2.close();
            conn3.close();
        });
    }
}