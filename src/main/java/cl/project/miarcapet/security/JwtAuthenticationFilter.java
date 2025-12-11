package cl.project.miarcapet.security;

import cl.project.miarcapet.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de Autenticación JWT.
 * Intercepta cada solicitud HTTP y valida el token JWT si está presente.
 *
 * Extiende OncePerRequestFilter para asegurar que el filtro se ejecute una vez por solicitud.
 * Este filtro se agrega a la cadena de filtros de Spring Security en SecurityConfig.
 *
 * Proceso del filtro:
 * 1. Extraer token JWT del header Authorization
 * 2. Validar formato del token y extraer username
 * 3. Cargar detalles del usuario desde base de datos
 * 4. Validar token contra detalles del usuario
 * 5. Establecer autenticación en SecurityContext si es válido
 * 6. Continuar cadena de filtros
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Método principal del filtro llamado para cada solicitud.
     *
     * @param request Solicitud HTTP
     * @param response Respuesta HTTP
     * @param filterChain Cadena de filtros para continuar el procesamiento
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Paso 1: Extraer header Authorization
        final String authHeader = request.getHeader("Authorization");

        // Paso 2: Verificar si el header Authorization está presente y empieza con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No hay token JWT presente, continuar cadena de filtros
            // Esto permite que los endpoints públicos sean accedidos sin autenticación
            filterChain.doFilter(request, response);
            return;
        }

        // Paso 3: Extraer token JWT (remover prefijo "Bearer ")
        final String jwt = authHeader.substring(7);

        try {
            // Paso 4: Extraer username del token
            final String userEmail = jwtService.extractUsername(jwt);

            // Paso 5: Verificar si el username está presente y el usuario no está ya autenticado
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Paso 6: Cargar detalles del usuario desde base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // Paso 7: Validar token contra detalles del usuario
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // Paso 8: Crear token de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, // Principal (usuario)
                            null, // Credenciales (no necesarias después de autenticación)
                            userDetails.getAuthorities() // Autoridades/roles del usuario
                    );

                    // Paso 9: Establecer detalles adicionales desde la solicitud
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Paso 10: Establecer autenticación en SecurityContext
                    // Esto le dice a Spring Security que el usuario está autenticado
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Fallo en la validación del token
            // Registrar error (en producción usar logging apropiado)
            System.err.println("Error de validación JWT: " + e.getMessage());
            // No establecer autenticación, continuar cadena de filtros
            // Spring Security manejará el acceso no autorizado
        }

        // Paso 11: Continuar cadena de filtros
        filterChain.doFilter(request, response);
    }
}
