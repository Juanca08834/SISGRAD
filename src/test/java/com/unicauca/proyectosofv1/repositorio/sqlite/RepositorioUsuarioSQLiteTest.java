package com.unicauca.proyectosofv1.repositorio.sqlite;

import com.unicauca.proyectosofv1.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioUsuarioSQLiteTest {

    private RepositorioUsuarioSQLite repositorio;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE programa (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT UNIQUE)");
            stmt.executeUpdate("CREATE TABLE usuario (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombres TEXT, apellidos TEXT, email TEXT UNIQUE, " +
                    "password_hash TEXT, rol TEXT, celular TEXT, creado_en TEXT)");
            stmt.executeUpdate("CREATE TABLE estudiante (" +
                    "id INTEGER PRIMARY KEY, programa_id INTEGER, " +
                    "FOREIGN KEY (id) REFERENCES usuario(id), " +
                    "FOREIGN KEY (programa_id) REFERENCES programa(id))");
            stmt.executeUpdate("CREATE TABLE docente (" +
                    "id INTEGER PRIMARY KEY, programa_id INTEGER, " +
                    "FOREIGN KEY (id) REFERENCES usuario(id), " +
                    "FOREIGN KEY (programa_id) REFERENCES programa(id))");
        }

        repositorio = new RepositorioUsuarioSQLite(conn);
    }

    @Test
    void deberiaGuardarYRecuperarEstudiante() {
        Usuario usuario = new Usuario(
                "juan@correo.com", "Juan", "Pérez",
                "3001234567", "Ingeniería de Sistemas", "estudiante",
                "hash123", LocalDateTime.now()
        );

        repositorio.guardar(usuario);

        Usuario recuperado = repositorio.buscarPorEmail("juan@correo.com");

        assertNotNull(recuperado);
        assertEquals("Juan", recuperado.getNombres());
        assertEquals("estudiante", recuperado.getRol());
        assertEquals("Ingeniería de Sistemas", recuperado.getPrograma());
    }

    @Test
    void deberiaGuardarYRecuperarDocente() {
        Usuario usuario = new Usuario(
                "ana@correo.com", "Ana", "Gómez",
                "3119876543", "Matemáticas", "docente",
                "hash456", LocalDateTime.now()
        );

        repositorio.guardar(usuario);

        Usuario recuperado = repositorio.buscarPorEmail("ana@correo.com");

        assertNotNull(recuperado);
        assertEquals("Ana", recuperado.getNombres());
        assertEquals("docente", recuperado.getRol());
        assertEquals("Matemáticas", recuperado.getPrograma());
    }

    @Test
    void noDeberiaPermitirEmailsDuplicados() {
        Usuario u1 = new Usuario(
                "dup@correo.com", "Carlos", "López",
                "3000000000", "Derecho", "estudiante",
                "hash789", LocalDateTime.now()
        );

        Usuario u2 = new Usuario(
                "dup@correo.com", "Pedro", "Ramírez",
                "3221111111", "Derecho", "estudiante",
                "hash111", LocalDateTime.now()
        );

        repositorio.guardar(u1);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repositorio.guardar(u2));
        assertTrue(ex.getMessage().toLowerCase().contains("ya existe"));
    }

    @Test
    void deberiaRetornarNullSiNoExisteUsuario() {
        Usuario recuperado = repositorio.buscarPorEmail("noexiste@correo.com");
        assertNull(recuperado);
    }
}
