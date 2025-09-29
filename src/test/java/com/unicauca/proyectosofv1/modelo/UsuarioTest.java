package com.unicauca.proyectosofv1.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class UsuarioTest {

    @Test
    void testCrearUsuarioYObtenerDatos() {
        LocalDateTime fecha = LocalDateTime.now();

        Usuario usuario = new Usuario(
                "correo@test.com",
                "Juan",
                "Pérez",
                "123456789",
                "Ingeniería",
                "ESTUDIANTE",
                "hash123",
                fecha
        );

        assertEquals("correo@test.com", usuario.getEmail());
        assertEquals("Juan", usuario.getNombres());
        assertEquals("Pérez", usuario.getApellidos());
        assertEquals("123456789", usuario.getCelular());
        assertEquals("Ingeniería", usuario.getPrograma());
        assertEquals("ESTUDIANTE", usuario.getRol());
        assertEquals("hash123", usuario.getPasswordHash());
        assertEquals(fecha, usuario.getCreadoEn());
    }

    @Test
    void testInmutabilidad() {
        LocalDateTime fecha = LocalDateTime.now();
        Usuario usuario = new Usuario(
                "correo@test.com",
                "Ana",
                "López",
                "987654321",
                "Derecho",
                "ADMIN",
                "hash456",
                fecha
        );

        // Verificamos que los valores se mantienen y no hay setters
        assertEquals("Ana", usuario.getNombres());
        assertEquals("López", usuario.getApellidos());
        assertNull(getSetterMethodIfAny(usuario)); // comprobamos indirectamente que no hay setters
    }

    // Método auxiliar para verificar ausencia de setters
    private Object getSetterMethodIfAny(Usuario usuario) {
        try {
            usuario.getClass().getDeclaredMethod("setNombres", String.class);
            return "Setter encontrado";
        } catch (NoSuchMethodException e) {
            return null; // no hay setters → está bien
        }
    }
}
