
// MesaController.java
package com.sabor.gourmet.controller;

import com.sabor.gourmet.model.Mesa;
import com.sabor.gourmet.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mesas")
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @GetMapping
    public String listarMesas(Model model) {
        model.addAttribute("mesas", mesaRepository.findAll());
        model.addAttribute("title", "Gestión de Mesas");
        return "mesas/lista";
    }

    @GetMapping("/nueva")
    public String nuevaMesaForm(Model model) {
        model.addAttribute("mesa", new Mesa());
        model.addAttribute("title", "Nueva Mesa");
        return "mesas/form";
    }

    @PostMapping("/guardar")
    public String guardarMesa(@ModelAttribute Mesa mesa) {
        mesaRepository.save(mesa);
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
    public String eliminarMesa(@PathVariable Long id) {
        mesaRepository.deleteById(id);
        return "redirect:/mesas";
    }

    @GetMapping("/cambiar-estado/{id}/{estado}")
    public String cambiarEstado(@PathVariable Long id, @PathVariable String estado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada: " + id));

        mesa.setEstado(estado);
        mesaRepository.save(mesa);
        return "redirect:/mesas";
    }

    @GetMapping("/ocupar/{id}")
    public String ocuparMesa(@PathVariable Long id) {
        return cambiarEstado(id, "ocupada");
    }

    @GetMapping("/liberar/{id}")
    public String liberarMesa(@PathVariable Long id) {
        return cambiarEstado(id, "disponible");
    }
}

// ============================================

