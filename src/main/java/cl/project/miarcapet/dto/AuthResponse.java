package cl.project.miarcapet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de Transferencia de Datos para respuestas de autenticación.
 * Se retorna después de un login o registro exitoso.
 * Contiene el token JWT y la información del usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * Token de acceso JWT.
     * El cliente debe incluir esto en el header Authorization para solicitudes posteriores.
     * Formato: "Bearer {token}"
     */
    private String token;

    /**
     * Tipo de token (siempre "Bearer" para JWT).
     */
    private String type = "Bearer";

    /**
     * Dirección de email del usuario.
     */
    private String email;

    /**
     * Nombre completo del usuario.
     */
    private String name;

    /**
     * Rol del usuario en el sistema.
     */
    private String role;

    /**
     * Mensaje de éxito.
     */
    private String message;
}
