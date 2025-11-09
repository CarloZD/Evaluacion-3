package com.sabor.gourmet.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    // Relación con Cliente (opcional - solo cuando está ocupada)
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente clienteAsignado;

    @Column(name = "fecha_ocupacion")
    private LocalDateTime fechaOcupacion;

    @Column(name = "numero_personas")
    private Integer numeroPersonas;

    // Constructor personalizado sin id para facilitar la creación
    public Mesa(String numero, Integer capacidad, String estado) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    // Metodo helper para ocupar mesa
    public void ocupar(Cliente cliente, Integer personas) {
        this.estado = "ocupada";
        this.clienteAsignado = cliente;
        this.numeroPersonas = personas;
        this.fechaOcupacion = LocalDateTime.now();
    }

    // Metodo helper para liberar mesa
    public void liberar() {
        this.estado = "disponible";
        this.clienteAsignado = null;
        this.numeroPersonas = null;
        this.fechaOcupacion = null;
    }
}