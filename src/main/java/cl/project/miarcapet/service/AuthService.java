package cl.project.miarcapet.service;

import cl.project.miarcapet.dto.*;

/**
 * Interfaz de servicio para operaciones de autenticación.
 * Define métodos para registro de usuario, login y validación de tokens.
 */
public interface AuthService {

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Detalles del registro (email, nombre, contraseña, rol)
     * @return AuthResponse con token JWT e información del usuario
     * @throws cl.project.miarcapet.exception.UserAlreadyExistsException si el email ya existe
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Autentica un usuario y genera un token JWT.
     *
     * @param request Credenciales de login (email, contraseña)
     * @return AuthResponse con token JWT e información del usuario
     * @throws org.springframework.security.authentication.BadCredentialsException si las credenciales son inválidas
     */
    AuthResponse login(LoginRequest request);

    /**
     * Valida un token JWT y extrae información.
     *
     * @param request Solicitud de validación de token que contiene el token JWT
     * @return TokenValidationResponse con resultado de validación y detalles del token
     */
    TokenValidationResponse validateToken(TokenValidationRequest request);
}
