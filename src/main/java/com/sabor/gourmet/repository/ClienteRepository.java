package com.sabor.gourmet.repository;

import com.sabor.gourmet.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar clientes por estado
    List<Cliente> findByEstado(String estado);

    // Contar clientes por estado
    long countByEstado(String estado);

    // Buscar cliente por DNI
    Optional<Cliente> findByDni(String dni);

    // Buscar clientes por nombre o apellido (búsqueda parcial)
    List<Cliente> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(
            String nombres, String apellidos);

    // Buscar clientes por correo
    Optional<Cliente> findByCorreo(String correo);

    // Buscar clientes por teléfono
    List<Cliente> findByTelefonoContaining(String telefono);

    // Buscar todos los clientes ordenados por apellido
    List<Cliente> findAllByOrderByApellidosAsc();

    // Buscar clientes activos ordenados por nombre
    List<Cliente> findByEstadoOrderByNombresAsc(String estado);
}