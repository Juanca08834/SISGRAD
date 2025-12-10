package com.unicauca.formatoa.domain.ports.in;

import com.unicauca.formatoa.domain.model.FormatoA;
import java.util.List;

/**
 * Puerto de entrada (Use Case) para consultar Formatos A
 */
public interface ConsultarFormatoUseCase {
    /**
     * Obtiene un Formato A por su ID
     * @param id ID del formato
     * @return Formato A encontrado
     */
    FormatoA obtenerPorId(Long id);
    
    /**
     * Obtiene todos los Formatos A de un docente
     * @param docenteId ID del docente
     * @return Lista de formatos del docente
     */
    List<FormatoA> obtenerPorDocente(Long docenteId);
    
    /**
     * Obtiene todos los Formatos A pendientes de evaluación
     * @return Lista de formatos pendientes
     */
    List<FormatoA> obtenerPendientes();
    
    /**
     * Obtiene todos los Formatos A
     * @return Lista de todos los formatos
     */
    List<FormatoA> obtenerTodos();
}