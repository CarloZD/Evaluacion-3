package com.sabor.gourmet.controller;

import com.sabor.gourmet.model.Cliente;
import com.sabor.gourmet.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public String listarClientes(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado,
            Model model) {

        List<Cliente> clientes;

        // Aplicar filtros
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            clientes = clienteRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(
                    busqueda, busqueda);
        } else if (estado != null && !estado.isEmpty()) {
            clientes = clienteRepository.findByEstado(estado);
        } else {
            clientes = clienteRepository.findAll();
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estadoFiltro", estado);
        model.addAttribute("title", "Gestión de Clientes");

        // Estadísticas
        long totalClientes = clienteRepository.count();
        long clientesActivos = clienteRepository.countByEstado("activo");
        long clientesInactivos = clienteRepository.countByEstado("inactivo");

        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("clientesActivos", clientesActivos);
        model.addAttribute("clientesInactivos", clientesInactivos);

        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoClienteForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("title", "Nuevo Cliente");
        return "clientes/form";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        try {
            // Validar DNI único (excepto al editar el mismo cliente)
            if (cliente.getId() == null) {
                if (clienteRepository.findByDni(cliente.getDni()).isPresent()) {
                    redirectAttributes.addFlashAttribute("mensaje", "Ya existe un cliente con ese DNI");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
                    return "redirect:/clientes/nuevo";
                }
            }

            // Asegurarse de que el estado tenga un valor por defecto
            if (cliente.getEstado() == null || cliente.getEstado().isEmpty()) {
                cliente.setEstado("activo");
            }

            clienteRepository.save(cliente);

            redirectAttributes.addFlashAttribute("mensaje",
                    cliente.getId() == null ? "Cliente registrado exitosamente" : "Cliente actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el cliente: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editarClienteForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
            model.addAttribute("cliente", cliente);
            model.addAttribute("title", "Editar Cliente");
            return "clientes/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/clientes";
        }
    }

    @GetMapping("/ver/{id}")
    public String verCliente(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));
            model.addAttribute("cliente", cliente);
            model.addAttribute("title", "Detalle del Cliente");
            return "clientes/detalle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/clientes";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));

            clienteRepository.deleteById(id);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Cliente '" + cliente.getNombres() + " " + cliente.getApellidos() + "' eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/clientes";
    }

    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + id));

            String nuevoEstado = cliente.getEstado().equals("activo") ? "inactivo" : "activo";
            cliente.setEstado(nuevoEstado);
            clienteRepository.save(cliente);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Cliente " + (nuevoEstado.equals("activo") ? "activado" : "desactivado") + " exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

        return "redirect:/clientes";
    }

    @GetMapping("/buscar-dni")
    @ResponseBody
    public Cliente buscarPorDni(@RequestParam String dni) {
        return clienteRepository.findByDni(dni).orElse(null);
    }
}