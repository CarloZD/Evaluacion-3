package com.sabor.gourmet.controller;

import com.sabor.gourmet.model.Usuario;
import com.sabor.gourmet.repository.UsuarioRepository;
import com.sabor.gourmet.services.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditoriaService auditoriaService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Ha cerrado sesión exitosamente");
        }
        model.addAttribute("title", "Iniciar Sesión");
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("title", "Registro de Usuario");
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario,
                            @RequestParam String confirmarPassword,
                            RedirectAttributes redirectAttributes) {
        try {
            // Validar que el username no exista
            if (usuarioRepository.existsByUsername(usuario.getUsername())) {
                redirectAttributes.addFlashAttribute("error",
                        "El nombre de usuario ya está en uso");
                return "redirect:/registro";
            }

            // Validar que el email no exista
            if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()
                    && usuarioRepository.existsByEmail(usuario.getEmail())) {
                redirectAttributes.addFlashAttribute("error",
                        "El correo electrónico ya está registrado");
                return "redirect:/registro";
            }

            // Validar que las contraseñas coincidan
            if (!usuario.getPassword().equals(confirmarPassword)) {
                redirectAttributes.addFlashAttribute("error",
                        "Las contraseñas no coinciden");
                return "redirect:/registro";
            }

            // Encriptar contraseña
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            // Asignar rol USER por defecto
            Set<String> roles = new HashSet<>();
            roles.add("USER");
            usuario.setRoles(roles);
            usuario.setActivo(true);

            // Guardar usuario
            Usuario usuarioGuardado = usuarioRepository.save(usuario);

            // Registrar en auditoría
            auditoriaService.registrar("USUARIO", "REGISTRO",
                    "Nuevo usuario registrado: " + usuario.getUsername(),
                    usuarioGuardado.getId(), usuario.getUsername());

            redirectAttributes.addFlashAttribute("mensaje",
                    "Registro exitoso. Por favor inicie sesión.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al registrar usuario: " + e.getMessage());
            return "redirect:/registro";
        }
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado(Model model) {
        model.addAttribute("title", "Acceso Denegado");
        return "auth/acceso-denegado";
    }
}