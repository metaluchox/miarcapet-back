package cl.project.miarcapet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de Transferencia de Datos para solicitudes de inicio de sesión.
 * Contiene las credenciales necesarias para la autenticación del usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Dirección de email del usuario (se usa como nombre de usuario).
     * Debe tener un formato de email válido y no puede estar en blanco.
     */
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;

    /**
     * Contraseña del usuario.
     * No puede estar en blanco pero no hay restricción de longitud aquí
     * (será validada por la lógica de negocio).
     */
    @NotBlank(message = "La contraseña es requerida")
    private String password;
}
