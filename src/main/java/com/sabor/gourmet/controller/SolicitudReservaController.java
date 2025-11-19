package com.sabor.gourmet.controller;

import com.sabor.gourmet.model.*;
import com.sabor.gourmet.repository.*;
import com.sabor.gourmet.services.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudReservaController {

    @Autowired
    private SolicitudReservaRepository solicitudRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    // ========== VISTAS ==========

    /**
     * Lista de solicitudes para ADMIN
     */
    @GetMapping("/admin")
    public String listarSolicitudesAdmin(
            @RequestParam(required = false) String estado,
            Model model) {

        List<SolicitudReserva> solicitudes;

        if (estado != null && !estado.isEmpty()) {
            solicitudes = solicitudRepository.findByEstadoOrderByFechaSolicitudDesc(estado);
        } else {
            solicitudes = solicitudRepository.findAllByOrderByFechaSolicitudDesc();
        }

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("estadoFiltro", estado);

        // Estadísticas
        long pendientes = solicitudRepository.countByEstado("PENDIENTE");
        long aprobadas = solicitudRepository.countByEstado("APROBADA");
        long rechazadas = solicitudRepository.countByEstado("RECHAZADA");

        model.addAttribute("pendientes", pendientes);
        model.addAttribute("aprobadas", aprobadas);
        model.addAttribute("rechazadas", rechazadas);
        model.addAttribute("title", "Gestión de Solicitudes");

        return "solicitudes/admin-lista";
    }

    /**
     * Mis solicitudes para USUARIO
     */
    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(Authentication authentication, Model model) {
        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<SolicitudReserva> solicitudes =
                solicitudRepository.findByUsuarioOrderByFechaSolicitudDesc(usuario);

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("title", "Mis Solicitudes");

        return "solicitudes/mis-solicitudes";
    }

    /**
     * Formulario para crear solicitud
     */
    @GetMapping("/nueva/{mesaId}")
    public String nuevaSolicitudForm(
            @PathVariable Long mesaId,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Mesa mesa = mesaRepository.findById(mesaId)
                    .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

            if (!"disponible".equals(mesa.getEstado())) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "La mesa no está disponible para solicitudes");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
                return "redirect:/mesas";
            }

            String username = authentication.getName();
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            model.addAttribute("mesa", mesa);
            model.addAttribute("clientes", clienteRepository.findByEstado("activo"));
            model.addAttribute("usuario", usuario);
            model.addAttribute("title", "Nueva Solicitud de Reserva");

            return "solicitudes/form";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/mesas";
        }
    }

    // ========== ACCIONES ==========

    /**
     * Crear nueva solicitud
     */
    @PostMapping("/crear")
    public String crearSolicitud(
            @RequestParam Long mesaId,
            @RequestParam Long clienteId,
            @RequestParam Integer numeroPersonas,
            @RequestParam(required = false) String motivo,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Mesa mesa = mesaRepository.findById(mesaId)
                    .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            String username = authentication.getName();
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar capacidad
            if (numeroPersonas > mesa.getCapacidad()) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "El número de personas excede la capacidad de la mesa");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
                return "redirect:/solicitudes/nueva/" + mesaId;
            }

            // Crear solicitud
            SolicitudReserva solicitud = new SolicitudReserva(
                    mesa, cliente, usuario, numeroPersonas, motivo);

            solicitudRepository.save(solicitud);

            // Auditoría
            auditoriaService.registrar("SOLICITUD", "CREAR",
                    "Usuario " + username + " solicitó mesa " + mesa.getNumero() +
                            " para cliente " + cliente.getNombres() + " " + cliente.getApellidos(),
                    solicitud.getId(), "Solicitud #" + solicitud.getId());

            redirectAttributes.addFlashAttribute("mensaje",
                    "Solicitud enviada exitosamente. El administrador la revisará pronto.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

            return "redirect:/solicitudes/mis-solicitudes";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al crear solicitud: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/mesas";
        }
    }

    /**
     * Aprobar solicitud (ADMIN)
     */
    @PostMapping("/aprobar/{id}")
    public String aprobarSolicitud(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            SolicitudReserva solicitud = solicitudRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            if (!solicitud.isPendiente()) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "Esta solicitud ya fue procesada");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
                return "redirect:/solicitudes/admin";
            }

            String username = authentication.getName();
            Usuario admin = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Aprobar solicitud
            solicitud.aprobar(admin, observaciones);
            solicitudRepository.save(solicitud);

            // Asignar mesa al cliente
            Mesa mesa = solicitud.getMesa();
            mesa.ocupar(solicitud.getCliente(), solicitud.getNumeroPersonas());
            mesaRepository.save(mesa);

            // Auditoría
            auditoriaService.registrar("SOLICITUD", "APROBAR",
                    "Admin " + username + " aprobó solicitud #" + id +
                            " - Mesa " + mesa.getNumero(),
                    id, "Solicitud #" + id);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Solicitud aprobada y mesa asignada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al aprobar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/solicitudes/admin";
    }

    /**
     * Rechazar solicitud (ADMIN)
     */
    @PostMapping("/rechazar/{id}")
    public String rechazarSolicitud(
            @PathVariable Long id,
            @RequestParam String observaciones,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            SolicitudReserva solicitud = solicitudRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            if (!solicitud.isPendiente()) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "Esta solicitud ya fue procesada");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
                return "redirect:/solicitudes/admin";
            }

            String username = authentication.getName();
            Usuario admin = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Rechazar solicitud
            solicitud.rechazar(admin, observaciones);
            solicitudRepository.save(solicitud);

            // Auditoría
            auditoriaService.registrar("SOLICITUD", "RECHAZAR",
                    "Admin " + username + " rechazó solicitud #" + id,
                    id, "Solicitud #" + id);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Solicitud rechazada");
            redirectAttributes.addFlashAttribute("tipoMensaje", "info");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al rechazar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/solicitudes/admin";
    }

    /**
     * Cancelar solicitud (USUARIO)
     */
    @GetMapping("/cancelar/{id}")
    public String cancelarSolicitud(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            SolicitudReserva solicitud = solicitudRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            String username = authentication.getName();
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar que sea el dueño de la solicitud
            if (!solicitud.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "No tienes permiso para cancelar esta solicitud");
                redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
                return "redirect:/solicitudes/mis-solicitudes";
            }

            if (!solicitud.isPendiente()) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "Solo puedes cancelar solicitudes pendientes");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
                return "redirect:/solicitudes/mis-solicitudes";
            }

            solicitudRepository.delete(solicitud);

            // Auditoría
            auditoriaService.registrar("SOLICITUD", "CANCELAR",
                    "Usuario " + username + " canceló solicitud #" + id,
                    id, "Solicitud #" + id);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Solicitud cancelada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "Error al cancelar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/solicitudes/mis-solicitudes";
    }
}