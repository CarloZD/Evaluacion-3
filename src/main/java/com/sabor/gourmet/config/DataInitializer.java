package com.sabor.gourmet.config;

import com.sabor.gourmet.model.Usuario;
import com.sabor.gourmet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario ADMIN si no existe
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombres("Administrador");
            admin.setApellidos("Sistema");
            admin.setEmail("admin@saborgourmet.com");
            admin.setTelefono("999999999");
            admin.setActivo(true);

            Set<String> rolesAdmin = new HashSet<>();
            rolesAdmin.add("ADMIN");
            admin.setRoles(rolesAdmin);

            usuarioRepository.save(admin);
            System.out.println("✅ Usuario ADMIN creado: admin / admin123");
        }

        // Crear usuario USER de prueba si no existe
        if (!usuarioRepository.existsByUsername("user")) {
            Usuario user = new Usuario();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setNombres("Usuario");
            user.setApellidos("Prueba");
            user.setEmail("user@saborgourmet.com");
            user.setTelefono("988888888");
            user.setActivo(true);

            Set<String> rolesUser = new HashSet<>();
            rolesUser.add("USER");
            user.setRoles(rolesUser);

            usuarioRepository.save(user);
            System.out.println("✅ Usuario USER creado: user / user123");
        }
    }
}