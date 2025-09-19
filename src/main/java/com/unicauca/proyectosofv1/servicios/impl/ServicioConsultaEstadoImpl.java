// ServicioConsultaEstadoImpl.java
package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.modelo.*;
import com.unicauca.proyectosofv1.repositorio.RepositorioProyecto;
import com.unicauca.proyectosofv1.servicios.ServicioConsultaEstado;

public class ServicioConsultaEstadoImpl implements ServicioConsultaEstado {
    private final RepositorioProyecto repo;
    public ServicioConsultaEstadoImpl(RepositorioProyecto repo) { this.repo = repo; }
    @Override public EstadoProyecto consultar(long proyectoId) {
        ProyectoGrado p = repo.buscarPorId(proyectoId);
        if (p == null) throw new IllegalArgumentException("Proyecto no existe");
        return p.getEstado();
    }
}
