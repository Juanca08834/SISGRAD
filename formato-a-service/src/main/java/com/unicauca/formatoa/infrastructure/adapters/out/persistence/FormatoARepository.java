package com.unicauca.formatoa.infrastructure.adapters.out.persistence;

import com.unicauca.formatoa.domain.model.EstadoFormato;
import com.unicauca.formatoa.infrastructure.adapters.out.persistence.entity.FormatoAEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para FormatoA
 */
@Repository
public interface FormatoARepository extends JpaRepository<FormatoAEntity, Long> {
    
    /**
     * Busca formatos por docente
     */
    List<FormatoAEntity> findByDocenteId(Long docenteId);
    
    /**
     * Busca formatos por estado
     */
    List<FormatoAEntity> findByEstado(EstadoFormato estado);
    
    /**
     * Busca formatos ordenados por fecha de creación descendente
     */
    List<FormatoAEntity> findAllByOrderByFechaCreacionDesc();
}