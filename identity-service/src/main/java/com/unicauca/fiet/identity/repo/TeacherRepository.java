package com.unicauca.fiet.identity.repo;

import com.unicauca.fiet.identity.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** Repositorio JPA para la entidad Teacher. */
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    Optional<Teacher> findByEmail(String email);
    boolean existsByEmail(String email);
}
