package com.sabor.gourmet.config;

import com.sabor.gourmet.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Rutas públicas
                        .requestMatchers("/login", "/registro", "/css/**", "/js/**", "/images/**").permitAll()

                        // Rutas solo para ADMIN
                        .requestMatchers("/auditoria/**").hasRole("ADMIN")
                        .requestMatchers("/solicitudes/admin/**").hasRole("ADMIN")
                        .requestMatchers("/solicitudes/aprobar/**").hasRole("ADMIN")
                        .requestMatchers("/solicitudes/rechazar/**").hasRole("ADMIN")

                        // Rutas para USER y ADMIN
                        .requestMatchers("/clientes/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/mesas/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/solicitudes/nueva/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/solicitudes/crear").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/solicitudes/mis-solicitudes").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/solicitudes/cancelar/**").hasAnyRole("ADMIN", "USER")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/acceso-denegado")
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }
}