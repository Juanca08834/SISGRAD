package com.unicauca.proyectosofv1.repositorio;

import com.unicauca.proyectosofv1.modelo.*;
import java.util.List;

public interface RepositorioProyecto {
    long crearProyecto(String docenteEmail);              // retorna id
    ProyectoGrado buscarPorId(long id);
    void actualizarProyecto(ProyectoGrado p);
    List<ProyectoGrado> listarTodos();
    List<ProyectoGrado> listarPorEstado(EstadoProyecto e);
}
