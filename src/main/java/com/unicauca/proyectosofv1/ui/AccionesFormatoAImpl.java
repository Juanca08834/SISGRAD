package com.unicauca.proyectosofv1.ui;

import com.unicauca.proyectosofv1.modelo.Modalidad;
import com.unicauca.proyectosofv1.servicios.ServicioFormatoA;

import java.time.LocalDate;
import java.util.List;

public class AccionesFormatoAImpl implements AccionesFormatoA {

    private final ServicioFormatoA servicio;

    public AccionesFormatoAImpl(ServicioFormatoA servicio) {
        this.servicio = servicio;
    }

    @Override
    public void alSubirFormatoA(long proyectoId, String titulo, Modalidad modalidad, LocalDate fecha,
                                String director, String codirector, String objetivoGeneral,
                                List<String> objetivosEspecificos, String pdfPath, String cartaAceptacionPath) {
        // delega al caso de uso
        servicio.subir(proyectoId, titulo, modalidad, fecha, director, codirector,
                objetivoGeneral, objetivosEspecificos, pdfPath, cartaAceptacionPath);
    }
}
