package cl.project.miarcapet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de Transferencia de Datos para solicitudes de validación de token.
 * Usado por el endpoint /auth/validate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationRequest {

    /**
     * Token JWT a validar.
     * Puede proporcionarse con o sin el prefijo "Bearer ".
     */
    @NotBlank(message = "El token es requerido")
    private String token;
}
