package com.sabor.gourmet.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mesas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false, length = 20)
    private String estado = "disponible"; // disponible, ocupada, reservada, mantenimiento

    // Constructor personalizado sin id para facilitar la creación
    public Mesa(String numero, Integer capacidad, String estado) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
    }
}