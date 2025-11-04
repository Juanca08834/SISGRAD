package com.unicauca.fiet.project.repo;

import com.unicauca.fiet.project.domain.FormatAVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormatAVersionRepository extends JpaRepository<FormatAVersion, UUID> {
    List<FormatAVersion> findByProjectIdOrderByVersionNumberDesc(UUID projectId);
    Optional<FormatAVersion> findFirstByProjectIdOrderByVersionNumberDesc(UUID projectId);
}
