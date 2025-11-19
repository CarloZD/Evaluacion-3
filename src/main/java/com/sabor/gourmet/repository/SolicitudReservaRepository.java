package com.sabor.gourmet.repository;

import com.sabor.gourmet.model.SolicitudReserva;
import com.sabor.gourmet.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudReservaRepository extends JpaRepository<SolicitudReserva, Long> {

    // Buscar por estado
    List<SolicitudReserva> findByEstadoOrderByFechaSolicitudDesc(String estado);

    // Buscar todas ordenadas por fecha
    List<SolicitudReserva> findAllByOrderByFechaSolicitudDesc();

    // Buscar por usuario (para que vea sus propias solicitudes)
    List<SolicitudReserva> findByUsuarioOrderByFechaSolicitudDesc(Usuario usuario);

    // Buscar pendientes
    List<SolicitudReserva> findByEstadoOrderByFechaSolicitudAsc(String estado);

    // Contar pendientes
    long countByEstado(String estado);

    // Buscar por mesa
    List<SolicitudReserva> findByMesaIdOrderByFechaSolicitudDesc(Long mesaId);

    // Buscar por cliente
    List<SolicitudReserva> findByClienteIdOrderByFechaSolicitudDesc(Long clienteId);
}