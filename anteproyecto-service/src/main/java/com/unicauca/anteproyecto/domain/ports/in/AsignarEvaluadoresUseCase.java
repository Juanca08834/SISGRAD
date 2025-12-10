package com.unicauca.anteproyecto.domain.ports.in;

import com.unicauca.anteproyecto.domain.model.EvaluadorAsignado;
import java.util.List;

/**
 * Puerto de entrada para asignar evaluadores (REQUISITO 8)
 * UBICACIÓN: domain/ports/in/AsignarEvaluadoresUseCase.java
 */
public interface AsignarEvaluadoresUseCase {
    /**
     * Asigna 2 evaluadores a un anteproyecto
     * @param anteproyectoId ID del anteproyecto
     * @param evaluadores Lista con 2 evaluadores
     * @return Lista de evaluadores asignados
     */
    List<EvaluadorAsignado> asignarEvaluadores(Long anteproyectoId, 
                                                List<EvaluadorAsignado> evaluadores);
}