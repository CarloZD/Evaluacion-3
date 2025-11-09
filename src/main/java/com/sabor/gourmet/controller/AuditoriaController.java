package com.sabor.gourmet.controller;

import com.sabor.gourmet.model.Auditoria;
import com.sabor.gourmet.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @GetMapping
    public String listarAuditoria(
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        List<Auditoria> registros;

        // Aplicar filtros
        if (fechaInicio != null && fechaFin != null) {
            LocalDateTime inicio = fechaInicio.atStartOfDay();
            LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
            registros = auditoriaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(inicio, fin);
        } else if (modulo != null && !modulo.isEmpty()) {
            registros = auditoriaRepository.findByModuloOrderByFechaHoraDesc(modulo);
        } else if (accion != null && !accion.isEmpty()) {
            registros = auditoriaRepository.findByAccionOrderByFechaHoraDesc(accion);
        } else if (usuario != null && !usuario.isEmpty()) {
            registros = auditoriaRepository.findByUsuarioOrderByFechaHoraDesc(usuario);
        } else {
            // Mostrar últimas 50 acciones por defecto
            registros = auditoriaRepository.findTop50ByOrderByFechaHoraDesc();
        }

        model.addAttribute("registros", registros);
        model.addAttribute("moduloFiltro", modulo);
        model.addAttribute("accionFiltro", accion);
        model.addAttribute("usuarioFiltro", usuario);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("title", "Auditoría del Sistema");

        // Estadísticas
        long totalRegistros = auditoriaRepository.count();
        long registrosClientes = auditoriaRepository.countByModulo("CLIENTE");
        long registrosMesas = auditoriaRepository.countByModulo("MESA");

        model.addAttribute("totalRegistros", totalRegistros);
        model.addAttribute("registrosClientes", registrosClientes);
        model.addAttribute("registrosMesas", registrosMesas);

        return "auditoria/lista";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Auditoria auditoria = auditoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de auditoría no encontrado"));

        model.addAttribute("auditoria", auditoria);
        model.addAttribute("title", "Detalle de Auditoría");
        return "auditoria/detalle";
    }

    @GetMapping("/historial/{modulo}/{registroId}")
    public String verHistorial(
            @PathVariable String modulo,
            @PathVariable Long registroId,
            Model model) {

        List<Auditoria> historial = auditoriaRepository
                .findByModuloAndRegistroIdOrderByFechaHoraDesc(modulo, registroId);

        model.addAttribute("registros", historial);
        model.addAttribute("modulo", modulo);
        model.addAttribute("registroId", registroId);
        model.addAttribute("title", "Historial del Registro");
        return "auditoria/historial";
    }
}