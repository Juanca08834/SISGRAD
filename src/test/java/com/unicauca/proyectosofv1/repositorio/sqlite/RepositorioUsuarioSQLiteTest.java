package com.unicauca.proyectosofv1.repositorio.sqlite;

import com.unicauca.proyectosofv1.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;

import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioUsuarioSQLiteTest {

    private Connection connection;
    private RepositorioUsuarioSQLite repositorio;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        repositorio = new RepositorioUsuarioSQLite(connection);

        crearTablaUsuarios();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    @DisplayName("Guardar debe insertar usuario correctamente")
    void guardar_DebeInsertarUsuarioCorrectamente() throws SQLException {
        Usuario usuario = new Usuario(
                "test@unicauca.edu.co",
                "Juan",
                "Pérez",
                "1234567890",
                "Ingeniería de Sistemas",
                "ESTUDIANTE",
                "hash123",
                LocalDateTime.now()
        );

        repositorio.guardar(usuario);

        Usuario usuarioGuardado = repositorio.buscarPorEmail("test@unicauca.edu.co");
        assertNotNull(usuarioGuardado);
        assertEquals("test@unicauca.edu.co", usuarioGuardado.getEmail());
        assertEquals("Juan", usuarioGuardado.getNombres());
        assertEquals("Pérez", usuarioGuardado.getApellidos());
        assertEquals("1234567890", usuarioGuardado.getCelular());
        assertEquals("Ingeniería de Sistemas", usuarioGuardado.getPrograma());
        assertEquals("ESTUDIANTE", usuarioGuardado.getRol());
        assertEquals("hash123", usuarioGuardado.getPasswordHash());
    }

    @Test
    @DisplayName("Buscar por email retorna null cuando usuario no existe")
    void buscarPorEmail_DebeRetornarNull_CuandoUsuarioNoExiste() {
        Usuario usuario = repositorio.buscarPorEmail("noexiste@unicauca.edu.co");

        assertNull(usuario);
    }
    @Test
    @DisplayName("Buscar por email debe retornar usuario cuando existe")
    void buscarPorEmail_DebeRetornarUsuario_CuandoUsuarioExiste() throws SQLException {
        Usuario usuarioAGuardar = new Usuario(
                "existe@unicauca.edu.co",
                "Carlos",
                "Rodríguez",
                "1111111111",
                "Ingeniería Electrónica",
                "ESTUDIANTE",
                "hashExiste",
                LocalDateTime.now()
        );
        repositorio.guardar(usuarioAGuardar);

        Usuario usuarioEncontrado = repositorio.buscarPorEmail("existe@unicauca.edu.co");

        assertNotNull(usuarioEncontrado);
        assertEquals("existe@unicauca.edu.co", usuarioEncontrado.getEmail());
        assertEquals("Carlos", usuarioEncontrado.getNombres());
        assertEquals("Rodríguez", usuarioEncontrado.getApellidos());
    }

    @Test
    @DisplayName("Actualizar debe modificar usuario existente")
    void actualizar_DebeModificarUsuarioExistente() throws SQLException {
        // Arrange - Primero insertar un usuario
        Usuario usuarioOriginal = new Usuario(
                "actualizar@unicauca.edu.co",
                "María",
                "García",
                "9876543210",
                "Ingeniería Civil",
                "ESTUDIANTE",
                "hashOriginal",
                LocalDateTime.now()
        );
        repositorio.guardar(usuarioOriginal);

        Usuario usuarioActualizado = new Usuario(
                "actualizar@unicauca.edu.co", // mismo email
                "María Elena", // nombre actualizado
                "García López", // apellido actualizado
                "5555555555", // celular actualizado
                "Ingeniería Industrial", // programa actualizado
                "DOCENTE", // rol actualizado
                "nuevoHash123", // nueva contraseña
                usuarioOriginal.getCreadoEn() // misma fecha de creación
        );

        repositorio.actualizar(usuarioActualizado);

        Usuario usuarioBuscado = repositorio.buscarPorEmail("actualizar@unicauca.edu.co");
        assertNotNull(usuarioBuscado);
        assertEquals("María Elena", usuarioBuscado.getNombres());
        assertEquals("García López", usuarioBuscado.getApellidos());
        assertEquals("5555555555", usuarioBuscado.getCelular());
        assertEquals("Ingeniería Industrial", usuarioBuscado.getPrograma());
        assertEquals("DOCENTE", usuarioBuscado.getRol());
        assertEquals("nuevoHash123", usuarioBuscado.getPasswordHash());
    }

    private void crearTablaUsuarios() throws SQLException {
        String sql = """
            CREATE TABLE usuarios (
                email TEXT PRIMARY KEY,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                celular TEXT,
                programa TEXT NOT NULL,
                rol TEXT NOT NULL,
                password_hash TEXT NOT NULL,
                creado_en TEXT NOT NULL
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}