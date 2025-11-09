package com.sabor.gourmet.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(length = 15)
    private String telefono;

    @Column(unique = true, length = 100)
    private String correo;

    @Column(nullable = false, length = 20)
    private String estado = "activo"; // activo, inactivo

    // Constructor personalizado sin id para facilitar la creación
    public Cliente(String dni, String nombres, String apellidos, String telefono, String correo, String estado) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
        this.estado = estado;
    }
}