package com.unicauca.anteproyecto.domain.ports.in;

import com.unicauca.anteproyecto.domain.model.Anteproyecto;
import com.unicauca.anteproyecto.domain.model.EvaluadorAsignado;
import java.util.List;

/**
 * Puerto de entrada para consultar anteproyectos
 * UBICACIÓN: domain/ports/in/ConsultarAnteproyectoUseCase.java
 */
public interface ConsultarAnteproyectoUseCase {
    /**
     * Obtiene un anteproyecto por ID
     */
    Anteproyecto obtenerPorId(Long id);
    
    /**
     * Obtiene todos los anteproyectos
     */
    List<Anteproyecto> obtenerTodos();
    
    /**
     * Obtiene evaluadores asignados a un anteproyecto
     */
    List<EvaluadorAsignado> obtenerEvaluadores(Long anteproyectoId);
}