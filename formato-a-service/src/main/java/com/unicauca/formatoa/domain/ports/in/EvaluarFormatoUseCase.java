package com.unicauca.formatoa.domain.ports.in;

import com.unicauca.formatoa.domain.model.Evaluacion;

/**
 * Puerto de entrada (Use Case) para evaluar un Formato A
 */
public interface EvaluarFormatoUseCase {
    /**
     * Evalúa un Formato A
     * @param evaluacion Datos de la evaluación
     * @return Evaluación creada
     */
    Evaluacion evaluar(Evaluacion evaluacion);
}