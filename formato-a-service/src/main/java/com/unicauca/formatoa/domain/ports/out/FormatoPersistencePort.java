package com.unicauca.formatoa.domain.ports.out;

import com.unicauca.formatoa.domain.model.EstadoFormato;
import com.unicauca.formatoa.domain.model.FormatoA;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de Formatos A
 */
public interface FormatoPersistencePort {
    /**
     * Guarda un Formato A
     * @param formatoA Formato a guardar
     * @return Formato guardado con ID
     */
    FormatoA guardar(FormatoA formatoA);
    
    /**
     * Busca un Formato A por ID
     * @param id ID del formato
     * @return Optional con el formato si existe
     */
    Optional<FormatoA> buscarPorId(Long id);
    
    /**
     * Busca todos los Formatos A de un docente
     * @param docenteId ID del docente
     * @return Lista de formatos
     */
    List<FormatoA> buscarPorDocente(Long docenteId);
    
    /**
     * Busca formatos por estado
     * @param estado Estado del formato
     * @return Lista de formatos con ese estado
     */
    List<FormatoA> buscarPorEstado(EstadoFormato estado);
    
    /**
     * Busca todos los formatos
     * @return Lista de todos los formatos
     */
    List<FormatoA> buscarTodos();
    
    /**
     * Actualiza un formato
     * @param formatoA Formato a actualizar
     * @return Formato actualizado
     */
    FormatoA actualizar(FormatoA formatoA);
}