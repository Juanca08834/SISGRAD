package com.unicauca.proyectosofv1.modelo;

import java.time.LocalDate;
import java.util.List;

public class FormatoA {
    public final long id;                // 0 si aun no se ha guardado
    public final long proyectoId;        // FK
    public final String titulo;
    public final Modalidad modalidad;
    public final LocalDate fecha;
    public final String director;
    public final String codirector;
    public final String objetivoGeneral;
    public final List<String> objetivosEspecificos;
    public final String pdfPath;
    public final String cartaAceptacionPath; // null si no aplica

    public FormatoA(long id, long proyectoId, String titulo, Modalidad modalidad, LocalDate fecha,
                    String director, String codirector, String objetivoGeneral, List<String> objetivosEspecificos,
                    String pdfPath, String cartaAceptacionPath) {
        this.id = id; this.proyectoId = proyectoId; this.titulo = titulo; this.modalidad = modalidad;
        this.fecha = fecha; this.director = director; this.codirector = codirector;
        this.objetivoGeneral = objetivoGeneral; this.objetivosEspecificos = objetivosEspecificos;
        this.pdfPath = pdfPath; this.cartaAceptacionPath = cartaAceptacionPath;
    }
}
