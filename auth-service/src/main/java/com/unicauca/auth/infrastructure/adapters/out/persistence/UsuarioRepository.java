package com.unicauca.auth.infrastructure.adapters.out.persistence;

import com.unicauca.auth.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones de base de datos
 * Spring Data JPA generará automáticamente la implementación
 */
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    
    /**
     * Busca un usuario por su email
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioEntity> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el email dado
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);
}