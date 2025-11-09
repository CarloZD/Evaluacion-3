package com.sabor.gourmet.repository;

import com.sabor.gourmet.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    // Buscar por módulo
    List<Auditoria> findByModuloOrderByFechaHoraDesc(String modulo);

    // Buscar por acción
    List<Auditoria> findByAccionOrderByFechaHoraDesc(String accion);

    // Buscar por usuario
    List<Auditoria> findByUsuarioOrderByFechaHoraDesc(String usuario);

    // Buscar por rango de fechas
    List<Auditoria> findByFechaHoraBetweenOrderByFechaHoraDesc(
            LocalDateTime inicio, LocalDateTime fin);

    // Buscar por módulo y registro específico
    List<Auditoria> findByModuloAndRegistroIdOrderByFechaHoraDesc(
            String modulo, Long registroId);

    // Obtener últimas N acciones
    List<Auditoria> findTop50ByOrderByFechaHoraDesc();

    // Contar acciones por módulo
    long countByModulo(String modulo);

    // Contar acciones por usuario
    long countByUsuario(String usuario);
}