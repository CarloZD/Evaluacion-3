package com.sabor.gourmet.repository;

import com.sabor.gourmet.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por username
    Optional<Usuario> findByUsername(String username);

    // Buscar usuario por email
    Optional<Usuario> findByEmail(String email);

    // Verificar si existe un username
    boolean existsByUsername(String username);

    // Verificar si existe un email
    boolean existsByEmail(String email);

    // Buscar usuarios activos
    List<Usuario> findByActivoTrue();

    // Buscar usuarios inactivos
    List<Usuario> findByActivoFalse();

    // Buscar usuarios por rol (requiere query personalizada)
    @org.springframework.data.jpa.repository.Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = :rol")
    List<Usuario> findByRol(String rol);
}