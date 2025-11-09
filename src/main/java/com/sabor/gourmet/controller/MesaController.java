package com.sabor.gourmet.controller;

import com.sabor.gourmet.model.Mesa;
import com.sabor.gourmet.model.Cliente;
import com.sabor.gourmet.repository.MesaRepository;
import com.sabor.gourmet.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public String listarMesas(Model model) {
        model.addAttribute("mesas", mesaRepository.findAll());
        model.addAttribute("title", "Gestión de Mesas");

        // Estadísticas
        long totalMesas = mesaRepository.count();
        long mesasDisponibles = mesaRepository.countByEstado("disponible");
        long mesasOcupadas = mesaRepository.countByEstado("ocupada");
        long mesasReservadas = mesaRepository.countByEstado("reservada");

        model.addAttribute("totalMesas", totalMesas);
        model.addAttribute("mesasDisponibles", mesasDisponibles);
        model.addAttribute("mesasOcupadas", mesasOcupadas);
        model.addAttribute("mesasReservadas", mesasReservadas);

        return "mesas/lista";
    }

    @GetMapping("/nueva")
    public String nuevaMesaForm(Model model) {
        model.addAttribute("mesa", new Mesa());
        model.addAttribute("title", "Nueva Mesa");
        return "mesas/form";
    }

    @PostMapping("/guardar")
    public String guardarMesa(@ModelAttribute Mesa mesa, RedirectAttributes redirectAttributes) {
        try {
            mesaRepository.save(mesa);
            redirectAttributes.addFlashAttribute("mensaje", "Mesa guardada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar la mesa: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/mesas";
    }

    @GetMapping("/editar/{id}")
    public String editarMesaForm(@PathVariable Long id, Model model) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + id));
        model.addAttribute("mesa", mesa);
        model.addAttribute("title", "Editar Mesa");
        return "mesas/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarMesa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Mesa mesa = mesaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + id));

            if ("ocupada".equals(mesa.getEstado())) {
                redirectAttributes.addFlashAttribute("mensaje", "No se puede eliminar una mesa ocupada");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            } else {
                mesaRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("mensaje", "Mesa eliminada exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/mesas";
    }

    @GetMapping("/asignar/{id}")
    public String asignarClienteForm(@PathVariable Long id, Model model) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + id));

        if (!"disponible".equals(mesa.getEstado())) {
            return "redirect:/mesas";
        }

        model.addAttribute("mesa", mesa);
        model.addAttribute("clientes", clienteRepository.findByEstado("activo"));
        model.addAttribute("title", "Asignar Cliente a Mesa");
        return "mesas/asignar";
    }

    @PostMapping("/asignar/{id}")
    public String asignarCliente(
            @PathVariable Long id,
            @RequestParam Long clienteId,
            @RequestParam Integer numeroPersonas,
            RedirectAttributes redirectAttributes) {

        try {
            Mesa mesa = mesaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada"));

            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

            if (numeroPersonas > mesa.getCapacidad()) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "El número de personas excede la capacidad de la mesa");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
                return "redirect:/mesas/asignar/" + id;
            }

            mesa.ocupar(cliente, numeroPersonas);
            mesaRepository.save(mesa);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Mesa asignada exitosamente a " + cliente.getNombres() + " " + cliente.getApellidos());
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al asignar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/mesas";
    }

    @GetMapping("/liberar/{id}")
    public String liberarMesa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Mesa mesa = mesaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + id));

            mesa.liberar();
            mesaRepository.save(mesa);

            redirectAttributes.addFlashAttribute("mensaje", "Mesa liberada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/mesas";
    }

    @GetMapping("/cambiar-estado/{id}/{estado}")
    public String cambiarEstado(
            @PathVariable Long id,
            @PathVariable String estado,
            RedirectAttributes redirectAttributes) {

        try {
            Mesa mesa = mesaRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + id));

            mesa.setEstado(estado);
            mesaRepository.save(mesa);

            redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/mesas";
    }
}