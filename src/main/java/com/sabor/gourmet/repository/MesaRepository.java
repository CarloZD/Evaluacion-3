package com.sabor.gourmet.repository;

import com.sabor.gourmet.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    // Contar mesas por estado
    long countByEstado(String estado);

    // Buscar mesas por estado
    List<Mesa> findByEstado(String estado);

    // Buscar mesas disponibles con capacidad mínima
    List<Mesa> findByEstadoAndCapacidadGreaterThanEqual(String estado, Integer capacidad);
}