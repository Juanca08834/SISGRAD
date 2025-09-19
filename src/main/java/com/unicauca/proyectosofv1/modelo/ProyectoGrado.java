package com.unicauca.proyectosofv1.modelo;

import java.util.ArrayList;
import java.util.List;

public class ProyectoGrado {
    private final long id;
    private final String docenteEmail;  // usamos Usuario existente y su rol
    private final List<String> estudiantesEmail = new ArrayList<>();
    private EstadoProyecto estado = EstadoProyecto.PRIMERA_EVAL_A;
    private int intentosFormatoA = 1;
    private FormatoA formatoAActual;

    public ProyectoGrado(long id, String docenteEmail) {
        this.id = id; this.docenteEmail = docenteEmail;
    }
    public long getId() { return id; }
    public String getDocenteEmail() { return docenteEmail; }
    public List<String> getEstudiantesEmail() { return estudiantesEmail; }
    public EstadoProyecto getEstado() { return estado; }
    public int getIntentosFormatoA() { return intentosFormatoA; }
    public FormatoA getFormatoAActual() { return formatoAActual; }

    public void setFormatoAActual(FormatoA f) { this.formatoAActual = f; }

    public void aprobar() { this.estado = EstadoProyecto.ACEPTADO_A; }

    public void avanzarPorRechazo() {
        if (intentosFormatoA == 1) estado = EstadoProyecto.SEGUNDA_EVAL_A;
        else if (intentosFormatoA == 2) estado = EstadoProyecto.TERCERA_EVAL_A;
        else estado = EstadoProyecto.RECHAZADO_A;
        if (estado != EstadoProyecto.RECHAZADO_A) intentosFormatoA++;
    }
}
