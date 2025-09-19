// ServicioEvaluacionFormatoAImpl.java
package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.modelo.*;
import com.unicauca.proyectosofv1.repositorio.RepositorioProyecto;
import com.unicauca.proyectosofv1.servicios.ServicioEvaluacionFormatoA;

public class ServicioEvaluacionFormatoAImpl implements ServicioEvaluacionFormatoA {
    private final RepositorioProyecto repoProyecto;

    public ServicioEvaluacionFormatoAImpl(RepositorioProyecto repoProyecto) {
        this.repoProyecto = repoProyecto;
    }

    @Override
    public void evaluar(long proyectoId, Decision decision, String observaciones) {
        ProyectoGrado p = repoProyecto.buscarPorId(proyectoId);
        if (p == null) throw new IllegalArgumentException("Proyecto no existe");

        if (decision == Decision.APROBADO) {
            p.aprobar();
            System.out.println("[MAIL] Proyecto " + proyectoId + " aprobado. Obs: " + observaciones);
        } else {
            p.avanzarPorRechazo();
            System.out.println("[MAIL] Proyecto " + proyectoId + " rechazado. Obs: " + observaciones +
                    " | Intentos: " + p.getIntentosFormatoA());
        }
        repoProyecto.actualizarProyecto(p);
    }
}
