package com.unicauca.anteproyecto.domain.ports.out;

import com.unicauca.anteproyecto.domain.model.Anteproyecto;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia
 * UBICACIÓN: domain/ports/out/AnteproyectoPersistencePort.java
 */
public interface AnteproyectoPersistencePort {
    Anteproyecto guardar(Anteproyecto anteproyecto);
    Optional<Anteproyecto> buscarPorId(Long id);
    List<Anteproyecto> buscarTodos();
    Anteproyecto actualizar(Anteproyecto anteproyecto);
}