package cl.project.miarcapet.config;

import cl.project.miarcapet.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security.
 * Configura autenticación, autorización y filtros de seguridad.
 *
 * Esta configuración:
 * 1. Habilita autenticación basada en JWT
 * 2. Configura endpoints públicos y protegidos
 * 3. Deshabilita manejo de sesiones (stateless)
 * 4. Deshabilita CSRF (no necesario para JWT stateless)
 * 5. Agrega filtro de autenticación JWT a la cadena de filtros
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configura la cadena de filtros de seguridad.
     * Define qué endpoints son públicos y cuáles requieren autenticación.
     *
     * @param http Objeto HttpSecurity para configuración
     * @return SecurityFilterChain configurado
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar protección CSRF (no necesaria para autenticación JWT stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos - No requieren autenticación
                        .requestMatchers("/").permitAll() // Página de bienvenida
                        .requestMatchers("/health").permitAll() // Health check
                        .requestMatchers("/auth/**").permitAll() // Todos los endpoints /auth/*
                        .requestMatchers("/h2-console/**").permitAll() // Consola base de datos H2
                        .requestMatchers("/error").permitAll() // Página de error

                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**").permitAll() // Swagger UI
                        .requestMatchers("/v3/api-docs/**").permitAll() // OpenAPI docs
                        .requestMatchers("/swagger-ui.html").permitAll() // Swagger UI HTML

                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated()
                )

                // Configurar manejo de sesiones
                .sessionManagement(session -> session
                        // Sesión stateless - Sin cookies de sesión
                        // Cada solicitud debe contener el token JWT
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Establecer proveedor de autenticación
                .authenticationProvider(authenticationProvider())

                // Agregar filtro JWT antes de UsernamePasswordAuthenticationFilter
                // Esto asegura que JWT se valide antes de la autenticación estándar
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Para Consola H2 (soporte de frames)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    /**
     * Crea el proveedor de autenticación.
     * Configura cómo Spring Security autentica usuarios.
     *
     * Usa DaoAuthenticationProvider que:
     * - Carga detalles del usuario desde UserDetailsService
     * - Valida contraseña usando PasswordEncoder
     *
     * @return AuthenticationProvider configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Establecer UserDetailsService para cargar usuario desde base de datos
        authProvider.setUserDetailsService(userDetailsService);

        // Establecer PasswordEncoder para validar contraseñas
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Expone AuthenticationManager como un bean.
     * Usado en AuthService para autenticar credenciales.
     *
     * @param config AuthenticationConfiguration de Spring Security
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean de codificador de contraseñas.
     * Usa el algoritmo de hashing BCrypt para encriptación de contraseñas.
     *
     * Características de BCrypt:
     * - Hashing adaptativo (más lento = más seguro)
     * - Generación de salt incorporada
     * - Estándar de la industria para almacenamiento de contraseñas
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Fuerza por defecto es 10 (buen balance entre seguridad y rendimiento)
        return new BCryptPasswordEncoder();
    }
}
