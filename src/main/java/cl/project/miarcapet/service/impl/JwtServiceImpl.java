package cl.project.miarcapet.service.impl;

import cl.project.miarcapet.exception.InvalidTokenException;
import cl.project.miarcapet.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementación del servicio JWT para manejar operaciones con tokens JWT.
 * Usa la biblioteca JJWT (io.jsonwebtoken) para creación y análisis de tokens.
 *
 * Este servicio es responsable de:
 * - Generar tokens JWT con información del usuario y expiración
 * - Validar tokens (firma, expiración, coincidencia de usuario)
 * - Extraer claims (username, rol, expiración) de los tokens
 */
@Service
public class JwtServiceImpl implements JwtService {

    /**
     * Clave secreta para firmar tokens JWT.
     * Debe inyectarse desde application.properties.
     * IMPORTANTE: En producción, usar una clave fuerte generada aleatoriamente (al menos 256 bits).
     */
    @Value("${jwt.secret-key}")
    private String secretKey;

    /**
     * Tiempo de expiración del token en milisegundos.
     * Se inyecta desde application.properties.
     * Por defecto son 24 horas (86400000 ms).
     */
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Genera un token JWT para el usuario autenticado.
     *
     * Estructura del token:
     * - Header: Algoritmo y tipo de token
     * - Payload: Claims (username, rol, fecha emisión, expiración)
     * - Signature: Firma HMAC SHA-256
     *
     * @param userDetails Detalles del usuario desde Spring Security
     * @return Token JWT firmado como String
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        // Crear mapa de claims con datos personalizados
        Map<String, Object> claims = new HashMap<>();

        // Agregar rol a los claims
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("role", role);

        // Construir y retornar el token
        return Jwts.builder()
                .claims(claims) // Agregar claims personalizados
                .subject(userDetails.getUsername()) // Establecer subject (username/email)
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de creación del token
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Fecha de expiración
                .signWith(getSigningKey()) // Firmar con clave secreta
                .compact(); // Construir el token
    }

    /**
     * Extrae el username (subject) del token JWT.
     *
     * @param token Token JWT
     * @return Username (email)
     * @throws InvalidTokenException si el token es inválido o expirado
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae el rol de los claims del token JWT.
     *
     * @param token Token JWT
     * @return Rol del usuario
     * @throws InvalidTokenException si el token es inválido o expirado
     */
    @Override
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Valida si el token es válido para el usuario dado.
     * Verificaciones:
     * 1. El username en el token coincide con el userDetails proporcionado
     * 2. El token no está expirado
     *
     * @param token Token JWT a validar
     * @param userDetails Detalles del usuario para validar contra
     * @return true si válido, false en caso contrario
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el token está expirado.
     *
     * @param token Token JWT
     * @return true si expirado, false en caso contrario
     */
    @Override
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token) < System.currentTimeMillis();
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Extrae la marca de tiempo de expiración del token.
     *
     * @param token Token JWT
     * @return Tiempo de expiración en milisegundos desde epoch
     */
    @Override
    public Long extractExpiration(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.getTime();
    }

    /**
     * Método genérico para extraer un claim específico del token.
     * Usa una función para extraer el claim deseado del objeto Claims.
     *
     * @param token Token JWT
     * @param claimsResolver Función para extraer claim específico
     * @param <T> Tipo del claim a extraer
     * @return Valor del claim extraído
     * @throws InvalidTokenException si el token es inválido
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token.
     * Verifica la firma y analiza el payload del token.
     *
     * @param token Token JWT
     * @return Objeto Claims que contiene todos los datos del token
     * @throws InvalidTokenException si la firma es inválida o el token está mal formado
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey()) // Verificar firma con clave secreta
                    .build()
                    .parseSignedClaims(token) // Analizar y validar token
                    .getPayload(); // Extraer payload de claims
        } catch (Exception e) {
            throw new InvalidTokenException("Token inválido o expirado", e);
        }
    }

    /**
     * Obtiene la clave de firma para operaciones JWT.
     * Decodifica la clave secreta codificada en base64 y crea un objeto SecretKey.
     *
     * @return SecretKey para firma HMAC SHA-256
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
