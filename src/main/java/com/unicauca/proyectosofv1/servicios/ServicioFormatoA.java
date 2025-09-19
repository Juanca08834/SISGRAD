// ServicioFormatoA.java
package com.unicauca.proyectosofv1.servicios;

import com.unicauca.proyectosofv1.modelo.*;

import java.time.LocalDate;
import java.util.List;

public interface ServicioFormatoA {
    FormatoA subir(long proyectoId, String titulo, Modalidad modalidad, LocalDate fecha,
                   String director, String codirector, String objetivoGeneral,
                   List<String> objetivosEspecificos, String pdfPath, String cartaAceptacionPath);
}
