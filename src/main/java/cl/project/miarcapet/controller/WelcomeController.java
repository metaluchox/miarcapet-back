package cl.project.miarcapet.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/")
@Tag(name = "Bienvenida", description = "Endpoint de bienvenida y verificación de la API")
public class WelcomeController {

    public ResponseEntity<Map<String, Object>> welcome() {
        // Crear respuesta con información de bienvenida
        Map<String, Object> response = new HashMap<>();

        // Mensaje principal
        response.put("message", "Bienvenido a miarcapet");

        // Estado de la API
        response.put("status", "online");

        // Versión de la API
        response.put("version", "1.0.0");

        // Timestamp actual
        response.put("timestamp", LocalDateTime.now());

        // Endpoints útiles
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("swagger", "/swagger-ui/index.html");
        endpoints.put("api-docs", "/v3/api-docs");
        endpoints.put("auth", "/auth");
        response.put("endpoints", endpoints);

        // Retornar respuesta con código 200 OK
        return ResponseEntity.ok(response);
    }

    
}
