package cl.project.miarcapet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de Transferencia de Datos para solicitudes de registro de usuario.
 * Contiene toda la información necesaria para crear una nueva cuenta de usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Dirección de email del usuario - Se usará como nombre de usuario.
     * Debe ser único y tener formato de email válido.
     */
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;

    /**
     * Nombre completo del usuario.
     */
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    /**
     * Contraseña del usuario.
     * Mínimo 6 caracteres para seguridad básica.
     */
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    /**
     * Rol opcional para el usuario.
     * Si no se proporciona, será "USER" por defecto.
     * Puede ser "USER" o "ADMIN".
     */
    private String role;
}
