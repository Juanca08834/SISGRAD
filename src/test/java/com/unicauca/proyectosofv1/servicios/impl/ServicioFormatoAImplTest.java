package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.excepciones.SISGRADException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ServicioFormatoAImplTest {

    private ServicioFormatoAImpl servicio;

    @BeforeEach
    void setUp() {
        // Mock del Connection porque el constructor lo requiere.
        Connection mockConn = Mockito.mock(Connection.class);
        servicio = new ServicioFormatoAImpl(mockConn);
    }

    @Test
    void subirFormatoA_missingTitle_throws() {
        // titulo en blanco -> debe lanzar SISGRADException
        assertThrows(SISGRADException.class, () ->
                servicio.subirFormatoA(
                        1,                  // trabajoGradoId
                        "   ",              // titulo (blank)
                        "Investigación",    // modalidad
                        10,                 // directorId
                        null,               // codirectorId
                        20,                 // estudiante1Id
                        null,               // estudiante2Id
                        1,                  // programaId
                        "Objetivo general", // objetivoGeneral
                        "Objetivos específicos", // objetivosEspecificos
                        "/tmp/fake.pdf",    // pdfPath
                        null                // cartaAceptacionPath
                )
        );
    }

    @Test
    void subirFormatoA_practicaProfesional_missingCarta_throws() {
        // modalidad Práctica Profesional requiere cartaAceptacionPath -> si es null debe lanzar
        assertThrows(SISGRADException.class, () ->
                servicio.subirFormatoA(
                        1,
                        "Título válido",
                        "Práctica Profesional",
                        10,
                        null,
                        20,
                        null,
                        1,
                        "Objetivo general",
                        "Objetivos específicos",
                        "/tmp/fake.pdf",
                        null // falta cartaAceptacionPath
                )
        );
    }

    @Test
    void subirFormatoA_sameStudents_throws() {
        // estudiante1Id == estudiante2Id -> debe lanzar
        assertThrows(SISGRADException.class, () ->
                servicio.subirFormatoA(
                        1,
                        "Título válido",
                        "Investigación",
                        10,
                        null,
                        20,
                        20, // mismo id para estudiante1 y estudiante2
                        1,
                        "Objetivo general",
                        "Objetivos específicos",
                        "/tmp/fake.pdf",
                        null
                )
        );
    }
}
