package com.unicauca.formatoa.domain.ports.out;

import com.unicauca.formatoa.domain.model.Evaluacion;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de Evaluaciones
 */
public interface EvaluacionPersistencePort {
    /**
     * Guarda una evaluación
     * @param evaluacion Evaluación a guardar
     * @return Evaluación guardada con ID
     */
    Evaluacion guardar(Evaluacion evaluacion);
    
    /**
     * Busca evaluaciones por Formato A
     * @param formatoAId ID del Formato A
     * @return Lista de evaluaciones
     */
    List<Evaluacion> buscarPorFormatoA(Long formatoAId);
    
    /**
     * Busca una evaluación por ID
     * @param id ID de la evaluación
     * @return Optional con la evaluación si existe
     */
    Optional<Evaluacion> buscarPorId(Long id);
}