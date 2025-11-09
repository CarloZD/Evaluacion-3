package com.sabor.gourmet.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String modulo; // CLIENTE, MESA

    @Column(nullable = false, length = 50)
    private String accion; // CREAR, EDITAR, ELIMINAR, LISTAR, ASIGNAR, LIBERAR, CAMBIAR_ESTADO

    @Column(length = 50)
    private String usuario; // Usuario que realizó la acción (por ahora "Sistema")

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(columnDefinition = "TEXT")
    private String descripcion; // Descripción detallada de la acción

    @Column(name = "registro_id")
    private Long registroId; // ID del registro afectado (cliente o mesa)

    @Column(length = 100)
    private String registroNombre; // Nombre del registro para referencia

    @Column(name = "ip_address", length = 45)
    private String ipAddress; // Dirección IP del usuario

    // Constructor para facilitar creación
    public Auditoria(String modulo, String accion, String usuario, String descripcion,
                     Long registroId, String registroNombre) {
        this.modulo = modulo;
        this.accion = accion;
        this.usuario = usuario;
        this.fechaHora = LocalDateTime.now();
        this.descripcion = descripcion;
        this.registroId = registroId;
        this.registroNombre = registroNombre;
    }
}