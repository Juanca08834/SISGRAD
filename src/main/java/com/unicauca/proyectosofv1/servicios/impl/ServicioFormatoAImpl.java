// ServicioFormatoAImpl.java
package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.modelo.*;
import com.unicauca.proyectosofv1.repositorio.*;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;

import java.time.LocalDate;
import java.util.List;

public class ServicioFormatoAImpl implements ServicioFormatoA {
    private final RepositorioProyecto repoProyecto;
    private final RepositorioFormatoA repoFormato;

    public ServicioFormatoAImpl(RepositorioProyecto rp, RepositorioFormatoA rf) {
        this.repoProyecto = rp; this.repoFormato = rf;
    }

    @Override
    public FormatoA subir(long proyectoId, String titulo, Modalidad modalidad, LocalDate fecha,
                          String director, String codirector, String objetivoGeneral,
                          List<String> objetivosEspecificos, String pdfPath, String cartaAceptacionPath) {
        ProyectoGrado p = repoProyecto.buscarPorId(proyectoId);
        if (p == null) throw new IllegalArgumentException("Proyecto no existe");

        if (modalidad == Modalidad.PRACTICA_PROFESIONAL &&
            (cartaAceptacionPath == null || cartaAceptacionPath.isBlank())) {
            throw new IllegalArgumentException("Carta de aceptacion requerida");
        }

        FormatoA f = new FormatoA(0, proyectoId, titulo, modalidad,
                (fecha != null ? fecha : LocalDate.now()), director, codirector,
                objetivoGeneral, objetivosEspecificos, pdfPath, cartaAceptacionPath);

        long id = repoFormato.guardar(f);
        FormatoA persistido = new FormatoA(id, proyectoId, titulo, modalidad,
                f.fecha, director, codirector, objetivoGeneral, objetivosEspecificos, pdfPath, cartaAceptacionPath);

        p.setFormatoAActual(persistido);
        repoProyecto.actualizarProyecto(p);
        return persistido;
    }
}
