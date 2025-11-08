package com.sabor.gourmet.controller;

import com.sabor.gourmet.model.Mesa;
import com.sabor.gourmet.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    // Página principal de mesas
    @GetMapping("/mesas")
    public String listarMesas(Model model) {
        model.addAttribute("mesas", mesaRepository.findAll());
        return "mesas";
    }

    // Formulario nueva mesa
    @GetMapping("/mesas/nueva")
    public String nuevaMesaForm(Model model) {
        model.addAttribute("mesa", new Mesa());
        return "mesa_form";
    }

    // Guardar mesa
    @PostMapping("/mesas/guardar")
    public String guardarMesa(@ModelAttribute Mesa mesa) {
        mesaRepository.save(mesa);
        return "redirect:/mesas";
    }
}
