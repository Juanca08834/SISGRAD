package com.unicauca.proyectosofv1.plugins.core;

import java.time.LocalDate;

public class TrabajoGradoDTO {
    public final String id;
    public final String titulo;
    public final LocalDate fechaFormatoA;
    public final String estudiante1;
    public final String estudiante2; // opcional
    public final String director;
    public final String modalidad;   // Investigación / Práctica
    public final String programa;

    public TrabajoGradoDTO(String id, String titulo, LocalDate fechaFormatoA,
                           String estudiante1, String estudiante2,
                           String director, String modalidad, String programa) {
        this.id = id; this.titulo = titulo; this.fechaFormatoA = fechaFormatoA;
        this.estudiante1 = estudiante1; this.estudiante2 = estudiante2;
        this.director = director; this.modalidad = modalidad; this.programa = programa;
    }
}