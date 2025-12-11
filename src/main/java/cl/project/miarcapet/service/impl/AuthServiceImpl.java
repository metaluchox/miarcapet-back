package cl.project.miarcapet.service.impl;

import cl.project.miarcapet.dto.*;
import cl.project.miarcapet.entity.User;
import cl.project.miarcapet.exception.InvalidTokenException;
import cl.project.miarcapet.exception.UserAlreadyExistsException;
import cl.project.miarcapet.repository.UserRepository;
import cl.project.miarcapet.service.AuthService;
import cl.project.miarcapet.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de autenticación.
 * Maneja registro de usuarios, autenticación y validación de tokens.
 *
 * Usa:
 * - UserRepository: Para operaciones de base de datos
 * - PasswordEncoder: Para encriptar contraseñas
 * - AuthenticationManager: Para autenticar credenciales
 * - JwtService: Para generar y validar tokens JWT
 */
@Service
@RequiredArgsConstructor // Lombok genera constructor para campos final (inyección de dependencias)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo usuario.
     *
     * Proceso:
     * 1. Verificar si el email ya existe
     * 2. Crear nueva entidad User
     * 3. Encriptar contraseña
     * 4. Guardar en base de datos
     * 5. Generar token JWT
     * 6. Retornar respuesta con token e info del usuario
     *
     * @param request Detalles del registro
     * @return AuthResponse con token JWT
     * @throws UserAlreadyExistsException si el email ya está registrado
     */
    @Override
    @Transactional // Asegura que la transacción de base de datos se maneje apropiadamente
    public AuthResponse register(RegisterRequest request) {
        // Paso 1: Verificar si el usuario ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Ya existe un usuario con el email " + request.getEmail()
            );
        }

        // Paso 2: Crear nueva entidad de usuario
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword())) // Encriptar contraseña
                .role(request.getRole() != null ? request.getRole() : "USER") // Por defecto rol USER
                .enabled(true)
                .build();

        // Paso 3: Guardar usuario en base de datos
        User savedUser = userRepository.save(user);

        // Paso 4: Generar token JWT
        String token = jwtService.generateToken(savedUser);

        // Paso 5: Construir y retornar respuesta
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole())
                .message("Usuario registrado exitosamente")
                .build();
    }

    /**
     * Autentica un usuario y genera token JWT.
     *
     * Proceso:
     * 1. Autenticar credenciales usando AuthenticationManager de Spring Security
     * 2. Si la autenticación tiene éxito, cargar usuario desde base de datos
     * 3. Generar token JWT
     * 4. Retornar respuesta con token e info del usuario
     *
     * @param request Credenciales de login
     * @return AuthResponse con token JWT
     * @throws BadCredentialsException si las credenciales son inválidas (manejado por AuthenticationManager)
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        // Paso 1: Autenticar usando Spring Security
        // Esto lanzará BadCredentialsException si las credenciales son incorrectas
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Paso 2: Obtener usuario autenticado (la autenticación tuvo éxito)
        User user = (User) authentication.getPrincipal();

        // Paso 3: Generar token JWT
        String token = jwtService.generateToken(user);

        // Paso 4: Construir y retornar respuesta
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .message("Login exitoso")
                .build();
    }

    /**
     * Valida un token JWT y extrae información.
     *
     * Proceso:
     * 1. Limpiar token (remover prefijo "Bearer " si existe)
     * 2. Intentar extraer username del token
     * 3. Si tiene éxito y no está expirado, cargar usuario desde base de datos
     * 4. Validar token contra usuario
     * 5. Retornar resultado de validación con info del usuario
     *
     * @param request Solicitud de validación de token
     * @return TokenValidationResponse con resultado de validación
     */
    @Override
    public TokenValidationResponse validateToken(TokenValidationRequest request) {
        try {
            // Paso 1: Limpiar token (remover prefijo "Bearer " si existe)
            String token = request.getToken();
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Paso 2: Extraer username del token
            String username = jwtService.extractUsername(token);

            // Paso 3: Verificar si el token está expirado
            if (jwtService.isTokenExpired(token)) {
                return TokenValidationResponse.builder()
                        .valid(false)
                        .message("El token ha expirado")
                        .build();
            }

            // Paso 4: Cargar usuario desde base de datos
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new InvalidTokenException("Usuario no encontrado para el token"));

            // Paso 5: Validar token contra usuario
            if (jwtService.isTokenValid(token, user)) {
                // Extraer información adicional
                String role = jwtService.extractRole(token);
                Long expiresAt = jwtService.extractExpiration(token);

                return TokenValidationResponse.builder()
                        .valid(true)
                        .username(username)
                        .role(role)
                        .expiresAt(expiresAt)
                        .message("El token es válido")
                        .build();
            } else {
                return TokenValidationResponse.builder()
                        .valid(false)
                        .message("El token es inválido")
                        .build();
            }

        } catch (InvalidTokenException e) {
            // Fallo en el análisis del token (formato inválido, firma, etc.)
            return TokenValidationResponse.builder()
                    .valid(false)
                    .message("Token inválido: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            // Error inesperado
            return TokenValidationResponse.builder()
                    .valid(false)
                    .message("Fallo en la validación del token: " + e.getMessage())
                    .build();
        }
    }
}
