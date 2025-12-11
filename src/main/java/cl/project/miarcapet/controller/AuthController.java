package cl.project.miarcapet.controller;

import cl.project.miarcapet.dto.*;
import cl.project.miarcapet.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para endpoints de autenticación.
 * Proporciona endpoints para registro de usuario, login y validación de tokens.
 *
 * Ruta base: /auth
 * Todos los endpoints son públicamente accesibles (configurado en SecurityConfig).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticación de usuarios con JWT")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /auth/register
     * Registra un nuevo usuario en el sistema.
     *
     * Validación del cuerpo de solicitud:
     * - email: Requerido, debe ser formato de email válido
     * - name: Requerido, 2-100 caracteres
     * - password: Requerido, mínimo 6 caracteres
     * - role: Opcional, por defecto "USER"
     *
     * @param request DTO RegisterRequest con detalles del usuario
     * @return ResponseEntity con AuthResponse que contiene token JWT e info del usuario
     * @throws UserAlreadyExistsException si el email ya existe (manejado por GlobalExceptionHandler)
     */
    @PostMapping("/register")
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario en el sistema. Retorna un token JWT válido para autenticación inmediata."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiJ9...",
                                              "type": "Bearer",
                                              "email": "usuario@ejemplo.com",
                                              "name": "Juan Pérez",
                                              "role": "USER",
                                              "message": "Usuario registrado exitosamente"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos (email inválido, contraseña muy corta, etc.)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-12-09T16:30:00",
                                              "status": 400,
                                              "errors": {
                                                "email": "El email debe ser válido",
                                                "password": "La contraseña debe tener al menos 6 caracteres"
                                              },
                                              "message": "Fallo en la validación"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El email ya está registrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-12-09T16:30:00",
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "Ya existe un usuario con el email usuario@ejemplo.com"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario a registrar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "usuario@ejemplo.com",
                                              "name": "Juan Pérez",
                                              "password": "password123",
                                              "role": "USER"
                                            }
                                            """
                            )
                    )
            )
            RegisterRequest request) {
        // @Valid activa las anotaciones de validación en RegisterRequest
        AuthResponse response = authService.register(request);

        // Retornar estado 201 Created con la respuesta
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /auth/login
     * Autentica un usuario y retorna un token JWT.
     *
     * Validación del cuerpo de solicitud:
     * - email: Requerido, debe ser formato de email válido
     * - password: Requerido, no puede estar en blanco
     *
     * Proceso de autenticación:
     * 1. Valida credenciales usando Spring Security
     * 2. Genera token JWT
     * 3. Retorna token con información del usuario
     *
     * @param request DTO LoginRequest con credenciales
     * @return ResponseEntity con AuthResponse que contiene token JWT
     * @throws BadCredentialsException si las credenciales son inválidas (manejado por GlobalExceptionHandler)
     */
    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario existente con email y contraseña. Retorna un token JWT para autorizar solicitudes posteriores."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login exitoso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiJ9...",
                                              "type": "Bearer",
                                              "email": "usuario@ejemplo.com",
                                              "name": "Juan Pérez",
                                              "role": "USER",
                                              "message": "Login exitoso"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-12-09T16:30:00",
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "Email o contraseña inválidos"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-12-09T16:30:00",
                                              "status": 400,
                                              "errors": {
                                                "email": "El email debe ser válido"
                                              },
                                              "message": "Fallo en la validación"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales de inicio de sesión",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "usuario@ejemplo.com",
                                              "password": "password123"
                                            }
                                            """
                            )
                    )
            )
            LoginRequest request) {
        // @Valid activa las anotaciones de validación en LoginRequest
        AuthResponse response = authService.login(request);

        // Retornar estado 200 OK con la respuesta
        return ResponseEntity.ok(response);
    }

    /**
     * POST /auth/validate
     * Valida un token JWT y retorna información del token.
     *
     * Este endpoint es útil para:
     * - Aplicaciones frontend para verificar si el token almacenado sigue válido
     * - Microservicios para validar tokens antes de procesar solicitudes
     * - Lógica de renovación de tokens para determinar si se necesita un nuevo token
     *
     * Cuerpo de solicitud:
     * - token: Requerido, token JWT (con o sin prefijo "Bearer ")
     *
     * La respuesta incluye:
     * - valid: Boolean indicando si el token es válido
     * - username: Email extraído del token (si es válido)
     * - role: Rol del usuario extraído del token (si es válido)
     * - expiresAt: Marca de tiempo de expiración del token (si es válido)
     * - message: Mensaje describiendo el resultado de la validación
     *
     * @param request DTO TokenValidationRequest con el token
     * @return ResponseEntity con TokenValidationResponse
     */
    @PostMapping("/validate")
    @Operation(
            summary = "Validar token JWT",
            description = "Verifica si un token JWT es válido y no ha expirado. Retorna información del usuario si el token es válido."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token válido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenValidationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "valid": true,
                                              "username": "usuario@ejemplo.com",
                                              "role": "ROLE_USER",
                                              "message": "El token es válido",
                                              "expiresAt": 1702466400000
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o expirado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenValidationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "valid": false,
                                              "username": null,
                                              "role": null,
                                              "message": "El token ha expirado",
                                              "expiresAt": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Token no proporcionado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-12-09T16:30:00",
                                              "status": 400,
                                              "errors": {
                                                "token": "El token es requerido"
                                              },
                                              "message": "Fallo en la validación"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<TokenValidationResponse> validateToken(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Token JWT a validar (con o sin prefijo 'Bearer ')",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TokenValidationRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "token": "Bearer eyJhbGciOiJIUzI1NiJ9..."
                                            }
                                            """
                            )
                    )
            )
            TokenValidationRequest request) {

        TokenValidationResponse response = authService.validateToken(request);

        // Retornar estado apropiado basado en el resultado de validación
        if (response.isValid()) {
            return ResponseEntity.ok(response);
        } else {
            // Retornar 401 Unauthorized si el token es inválido
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
