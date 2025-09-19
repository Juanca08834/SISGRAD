package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.modelo.Modalidad;
import java.time.LocalDate;
import java.util.List;

public record DatosFormatoA(
        String titulo,
        Modalidad modalidad,
        LocalDate fecha,
        String director,
        String codirector,
        String objetivoGeneral,
        List<String> objetivosEspecificos,
        String pdfPath,
        String cartaAceptacionPath
) {}
