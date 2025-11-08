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

    @Column(nullable = false)
    private int numero;

    @Column(nullable = false)
    private int capacidad;

    private boolean disponible = true;
}
