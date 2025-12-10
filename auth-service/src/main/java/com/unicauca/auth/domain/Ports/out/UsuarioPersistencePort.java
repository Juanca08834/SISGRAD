package com.unicauca.auth.domain.ports.out;

import com.unicauca.auth.domain.model.Usuario;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de usuarios
 * Define las operaciones de base de datos SIN especificar la tecnología
 * Puede ser implementado con JPA, MongoDB, o cualquier otra tecnología
 */
public interface UsuarioPersistencePort {
    /**
     * Guarda un nuevo usuario en la base de datos
     * @param usuario Usuario a guardar
     * @return Usuario guardado con ID asignado
     */
    Usuario guardar(Usuario usuario);
    
    /**
     * Busca un usuario por su email
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> buscarPorEmail(String email);
    
    /**
     * Busca un usuario por su ID
     * @param id ID del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> buscarPorId(Long id);
    
    /**
     * Verifica si existe un usuario con el email dado
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existePorEmail(String email);
    
    /**
     * Actualiza los datos de un usuario existente
     * @param usuario Usuario con datos actualizados
     */
    void actualizar(Usuario usuario);
}