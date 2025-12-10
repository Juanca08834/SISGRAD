package com.unicauca.anteproyecto.domain.ports.out;

import com.unicauca.anteproyecto.domain.model.EvaluadorAsignado;
import java.util.List;

/**
 * Puerto de salida para persistencia de evaluadores
 * UBICACIÓN: domain/ports/out/EvaluadorPersistencePort.java
 */
public interface EvaluadorPersistencePort {
    EvaluadorAsignado guardar(EvaluadorAsignado evaluador);
    List<EvaluadorAsignado> guardarTodos(List<EvaluadorAsignado> evaluadores);
    List<EvaluadorAsignado> buscarPorAnteproyecto(Long anteproyectoId);
}