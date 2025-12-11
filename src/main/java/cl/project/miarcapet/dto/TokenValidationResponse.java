package cl.project.miarcapet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de Transferencia de Datos para respuestas de validación de token.
 * Retornado por el endpoint /auth/validate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponse {

    /**
     * Indica si el token es válido.
     */
    private boolean valid;

    /**
     * Nombre de usuario (email) extraído del token si es válido.
     */
    private String username;

    /**
     * Rol extraído del token si es válido.
     */
    private String role;

    /**
     * Mensaje describiendo el resultado de la validación.
     */
    private String message;

    /**
     * Marca de tiempo cuando el token expira (en milisegundos desde epoch).
     * Solo se completa si el token es válido.
     */
    private Long expiresAt;
}
