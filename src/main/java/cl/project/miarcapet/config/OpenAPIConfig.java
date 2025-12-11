package cl.project.miarcapet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API.
 * Proporciona una interfaz web interactiva para probar los endpoints.
 *
 * Acceso a Swagger UI: http://localhost:8080/swagger-ui.html
 * Documentación JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Configura la documentación OpenAPI/Swagger.
     * Define información de la API, seguridad JWT y esquemas.
     *
     * @return Configuración OpenAPI completa
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Nombre del esquema de seguridad JWT
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                // Información general de la API
                .info(new Info()
                        .title("Mi Arcapet API")
                        .description("""
                                API REST para el sistema Mi Arcapet con autenticación JWT.

                                ## Autenticación

                                La mayoría de los endpoints requieren autenticación mediante JWT token.

                                ### Cómo usar:
                                1. Registra un usuario con POST /auth/register
                                2. Inicia sesión con POST /auth/login para obtener tu token
                                3. Copia el token de la respuesta
                                4. Haz clic en el botón "Authorize" (candado) arriba
                                5. Ingresa: Bearer TU_TOKEN_AQUI
                                6. Ahora puedes probar los endpoints protegidos

                                ## Endpoints Públicos
                                - POST /auth/register - Registrar nuevo usuario
                                - POST /auth/login - Iniciar sesión
                                - POST /auth/validate - Validar token JWT

                                ## Base de Datos
                                - H2 Console: http://localhost:8080/h2-console
                                - JDBC URL: jdbc:h2:mem:miarcapet
                                - Usuario: sa
                                - Contraseña: (vacío)
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mi Arcapet Team")
                                .email("contacto@miarcapet.cl")
                                .url("https://miarcapet.cl"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))

                // Configuración de seguridad JWT
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa tu JWT token. Ejemplo: Bearer eyJhbGc...")));
    }
}
