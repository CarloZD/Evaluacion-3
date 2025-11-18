package com.sabor.gourmet.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 15)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "rol")
    private Set<String> roles = new HashSet<>();

    // Relación opcional con Cliente (para usuarios que también son clientes)
    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Constructor personalizado
    public Usuario(String username, String password, String nombres,
                   String apellidos, String email, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.roles = roles;
        this.fechaRegistro = LocalDateTime.now();
        this.activo = true;
    }

    // Metodo helper para verificar si tiene un rol específico
    public boolean hasRole(String rol) {
        return roles.contains(rol);
    }

    // Metodo para agregar un rol
    public void addRole(String rol) {
        this.roles.add(rol);
    }
}