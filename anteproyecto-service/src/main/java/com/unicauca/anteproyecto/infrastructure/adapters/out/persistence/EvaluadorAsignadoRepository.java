package com.unicauca.anteproyecto.infrastructure.adapters.out.persistence;

import com.unicauca.anteproyecto.infrastructure.adapters.out.persistence.entity.EvaluadorAsignadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para Evaluadores Asignados
 * UBICACIÓN: infrastructure/adapters/out/persistence/EvaluadorAsignadoRepository.java
 */
@Repository
public interface EvaluadorAsignadoRepository extends JpaRepository<EvaluadorAsignadoEntity, Long> {
    List<EvaluadorAsignadoEntity> findByAnteproyectoId(Long anteproyectoId);
}