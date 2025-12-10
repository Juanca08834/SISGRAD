package com.unicauca.anteproyecto.infrastructure.adapters.out.persistence;

import com.unicauca.anteproyecto.infrastructure.adapters.out.persistence.entity.AnteproyectoEntity;
import com.unicauca.anteproyecto.infrastructure.adapters.out.persistence.entity.AnteproyectoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para Anteproyecto
 * UBICACIÓN: infrastructure/adapters/out/persistence/AnteproyectoRepository.java
 */
@Repository
public interface AnteproyectoRepository extends JpaRepository<AnteproyectoEntity, Long> {
    List<AnteproyectoEntity> findAllByOrderByFechaSubidaDesc();
}