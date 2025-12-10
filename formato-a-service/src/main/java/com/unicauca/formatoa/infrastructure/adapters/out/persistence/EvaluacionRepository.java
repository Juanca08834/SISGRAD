package com.unicauca.formatoa.infrastructure.adapters.out.persistence;

import com.unicauca.formatoa.infrastructure.adapters.out.persistence.entity.EvaluacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para Evaluacion
 */
@Repository
public interface EvaluacionRepository extends JpaRepository<EvaluacionEntity, Long> {
    
    /**
     * Busca evaluaciones por Formato A
     */
    List<EvaluacionEntity> findByFormatoAId(Long formatoAId);
    
    /**
     * Busca evaluaciones ordenadas por fecha descendente
     */
    List<EvaluacionEntity> findAllByOrderByFechaEvaluacionDesc();
}