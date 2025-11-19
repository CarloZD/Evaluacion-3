package com.sabor.gourmet.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Usuario que hizo la solicitud

    @Column(name = "numero_personas", nullable = false)
    private Integer numeroPersonas;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(length = 20, nullable = false)
    private String estado = "PENDIENTE"; // PENDIENTE, APROBADA, RECHAZADA

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Usuario adminResponsable; // Admin que aprobó/rechazó

    @Column(columnDefinition = "TEXT")
    private String observaciones; // Notas del admin

    @Column(columnDefinition = "TEXT")
    private String motivo; // Motivo de la solicitud del cliente

    // Constructor para facilitar creación
    public SolicitudReserva(Mesa mesa, Cliente cliente, Usuario usuario,
                            Integer numeroPersonas, String motivo) {
        this.mesa = mesa;
        this.cliente = cliente;
        this.usuario = usuario;
        this.numeroPersonas = numeroPersonas;
        this.motivo = motivo;
        this.fechaSolicitud = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    // Métodos helper
    public void aprobar(Usuario admin, String observaciones) {
        this.estado = "APROBADA";
        this.fechaRespuesta = LocalDateTime.now();
        this.adminResponsable = admin;
        this.observaciones = observaciones;
    }

    public void rechazar(Usuario admin, String observaciones) {
        this.estado = "RECHAZADA";
        this.fechaRespuesta = LocalDateTime.now();
        this.adminResponsable = admin;
        this.observaciones = observaciones;
    }

    public boolean isPendiente() {
        return "PENDIENTE".equals(this.estado);
    }

    public boolean isAprobada() {
        return "APROBADA".equals(this.estado);
    }

    public boolean isRechazada() {
        return "RECHAZADA".equals(this.estado);
    }
}