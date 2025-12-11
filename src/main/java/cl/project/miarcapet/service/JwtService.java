package cl.project.miarcapet.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interfaz de servicio para operaciones con tokens JWT.
 * Define métodos para generación, validación y extracción de tokens.
 */
public interface JwtService {

    /**
     * Genera un token JWT para el usuario dado.
     *
     * @param userDetails Detalles del usuario que contiene username y autoridades
     * @return Token JWT como String
     */
    String generateToken(UserDetails userDetails);

    /**
     * Extrae el nombre de usuario (email) de un token JWT.
     *
     * @param token Token JWT
     * @return Nombre de usuario extraído del token
     */
    String extractUsername(String token);

    /**
     * Valida si un token es válido para el usuario dado.
     * Verifica si el token no está expirado y el nombre de usuario coincide.
     *
     * @param token Token JWT a validar
     * @param userDetails Detalles del usuario para validar contra
     * @return true si el token es válido, false en caso contrario
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Verifica si un token está expirado.
     *
     * @param token Token JWT
     * @return true si el token está expirado, false en caso contrario
     */
    boolean isTokenExpired(String token);

    /**
     * Extrae la marca de tiempo de expiración de un token.
     *
     * @param token Token JWT
     * @return Tiempo de expiración en milisegundos desde epoch
     */
    Long extractExpiration(String token);

    /**
     * Extrae el rol de un token.
     *
     * @param token Token JWT
     * @return Rol del usuario extraído del token
     */
    String extractRole(String token);
}
