package com.unicauca.proyectosofv1.servicios.impl;

import com.unicauca.proyectosofv1.modelo.FormatoA;
import com.unicauca.proyectosofv1.repositorio.RepositorioFormatoA;
import com.unicauca.proyectosofv1.servicios.ServicioHistorialFormatoA;

import java.util.List;

public class ServicioHistorialFormatoAImpl implements ServicioHistorialFormatoA {

    private final RepositorioFormatoA repo;

    public ServicioHistorialFormatoAImpl(RepositorioFormatoA repo) {
        this.repo = repo;
    }

    @Override
    public List<FormatoA> listar(long proyectoId) {
        return repo.listarPorProyecto(proyectoId);
    }
}

