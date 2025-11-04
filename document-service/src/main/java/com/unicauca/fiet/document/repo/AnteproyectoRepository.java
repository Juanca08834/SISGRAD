package com.unicauca.fiet.document.repo;

import com.unicauca.fiet.document.domain.Anteproyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnteproyectoRepository extends JpaRepository<Anteproyecto, UUID> {
    List<Anteproyecto> findAllByOrderByFechaEnvioDesc();
}
