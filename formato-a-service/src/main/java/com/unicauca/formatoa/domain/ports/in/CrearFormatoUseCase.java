package com.unicauca.formatoa.domain.ports.in;

import com.unicauca.formatoa.domain.model.FormatoA;

/**
 * Puerto de entrada (Use Case) para crear un Formato A
 */
public interface CrearFormatoUseCase {
    /**
     * Crea un nuevo Formato A
     * @param formatoA Formato A a crear
     * @return Formato A creado con ID asignado
     */
    FormatoA crear(FormatoA formatoA);
    
    /**
     * Crea una nueva versión de un Formato A existente (reintentos)
     * @param formatoAId ID del formato original
     * @param formatoA Datos de la nueva versión
     * @return Nueva versión del Formato A
     */
    FormatoA crearNuevaVersion(Long formatoAId, FormatoA formatoA);
}