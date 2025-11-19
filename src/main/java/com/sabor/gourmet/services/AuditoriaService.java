package com.sabor.gourmet.services;

import com.sabor.gourmet.model.Auditoria;
import com.sabor.gourmet.repository.AuditoriaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    /**
     * Registra una acción en la auditoría
     */
    public void registrar(String modulo, String accion, String descripcion,
                          Long registroId, String registroNombre) {
        try {
            String usuario = obtenerUsuarioActual();
            String ipAddress = obtenerIpAddress();

            Auditoria auditoria = new Auditoria(
                    modulo,
                    accion,
                    usuario,
                    descripcion,
                    registroId,
                    registroNombre
            );
            auditoria.setIpAddress(ipAddress);

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            // Si falla la auditoría, no debe afectar la operación principal
            System.err.println("Error al registrar auditoría: " + e.getMessage());
        }
    }

    /**
     * Registra CREAR
     */
    public void registrarCrear(String modulo, Long id, String nombre) {
        registrar(modulo, "CREAR",
                "Se creó un nuevo registro: " + nombre,
                id, nombre);
    }

    /**
     * Registra EDITAR
     */
    public void registrarEditar(String modulo, Long id, String nombre) {
        registrar(modulo, "EDITAR",
                "Se editó el registro: " + nombre,
                id, nombre);
    }

    /**
     * Registra ELIMINAR
     */
    public void registrarEliminar(String modulo, Long id, String nombre) {
        registrar(modulo, "ELIMINAR",
                "Se eliminó el registro: " + nombre,
                id, nombre);
    }

    /**
     * Registra LISTAR
     */
    public void registrarListar(String modulo, int cantidad) {
        registrar(modulo, "LISTAR",
                "Se listaron " + cantidad + " registros",
                null, null);
    }

    /**
     * Registra BUSCAR
     */
    public void registrarBuscar(String modulo, String criterio, int resultados) {
        registrar(modulo, "BUSCAR",
                "Búsqueda: '" + criterio + "' - " + resultados + " resultados",
                null, null);
    }

    /**
     * Registra CAMBIAR ESTADO
     */
    public void registrarCambiarEstado(String modulo, Long id, String nombre,
                                       String estadoAnterior, String estadoNuevo) {
        registrar(modulo, "CAMBIAR_ESTADO",
                "Estado de '" + nombre + "' cambió de " + estadoAnterior + " a " + estadoNuevo,
                id, nombre);
    }

    /**
     * Registra ASIGNAR (específico para mesas)
     */
    public void registrarAsignar(Long mesaId, String mesaNumero,
                                 Long clienteId, String clienteNombre) {
        registrar("MESA", "ASIGNAR",
                "Mesa " + mesaNumero + " asignada a cliente: " + clienteNombre,
                mesaId, mesaNumero);
    }

    /**
     * Registra LIBERAR (específico para mesas)
     */
    public void registrarLiberar(Long mesaId, String mesaNumero) {
        registrar("MESA", "LIBERAR",
                "Mesa " + mesaNumero + " liberada",
                mesaId, mesaNumero);
    }

    /**
     * Obtiene el usuario actual desde Spring Security
     */
    private String obtenerUsuarioActual() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                // Si el username no es "anonymousUser", retornarlo
                if (!"anonymousUser".equals(username)) {
                    return username;
                }
            }

            return "Sistema";
        } catch (Exception e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
            return "Sistema";
        }
    }

    /**
     * Obtiene la dirección IP del cliente
     */
    private String obtenerIpAddress() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            System.err.println("Error al obtener IP: " + e.getMessage());
        }
        return "N/A";
    }
}