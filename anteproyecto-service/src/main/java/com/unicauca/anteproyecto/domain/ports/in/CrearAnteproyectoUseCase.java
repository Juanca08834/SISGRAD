package com.unicauca.anteproyecto.domain.ports.in;

import com.unicauca.anteproyecto.domain.model.Anteproyecto;

/**
 * Puerto de entrada para crear anteproyectos
 * UBICACIÓN: domain/ports/in/CrearAnteproyectoUseCase.java
 */
public interface CrearAnteproyectoUseCase {
    /**
     * Crea un nuevo anteproyecto
     */
    Anteproyecto crear(Anteproyecto anteproyecto);
}