package cl.project.miarcapet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entidad que representa un Usuario en el sistema.
 * Implementa la interfaz UserDetails para integrarse con Spring Security.
 * Esta entidad se mapea a la tabla 'users' en la base de datos.
 */
@Entity
@Table(name = "users")
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@NoArgsConstructor // Genera constructor sin argumentos (requerido por JPA)
@AllArgsConstructor // Genera constructor con todos los campos
@Builder // Implementa el patrón builder para crear objetos
public class User implements UserDetails {

    /**
     * Clave primaria - ID autogenerado para cada usuario
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email del usuario - Se usa como nombre de usuario para autenticación
     * Debe ser único en todo el sistema
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Nombre completo del usuario
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Contraseña encriptada - ¡Nunca almacenar contraseñas en texto plano!
     * Se encriptará usando BCrypt password encoder
     */
    @Column(nullable = false)
    private String password;

    /**
     * Rol del usuario en el sistema (ej: USER, ADMIN)
     * Valor por defecto es USER
     */
    @Column(nullable = false, length = 20)
    private String role = "USER";

    /**
     * Indica si la cuenta está habilitada
     * Puede usarse para desactivar cuentas sin eliminarlas
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Marca de tiempo cuando se creó el usuario
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Marca de tiempo de la última actualización del usuario
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Callback del ciclo de vida JPA - Establece createdAt antes de persistir
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Callback del ciclo de vida JPA - Actualiza updatedAt antes de actualizar
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Implementación de la interfaz UserDetails para Spring Security

    /**
     * Retorna las autoridades (roles/permisos) otorgadas al usuario.
     * Usamos un sistema simple basado en roles.
     * El rol se prefija con "ROLE_" según la convención de Spring Security.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    /**
     * Retorna el nombre de usuario usado para autenticación.
     * En nuestro caso, usamos el email como nombre de usuario.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indica si la cuenta del usuario ha expirado.
     * No implementamos expiración de cuentas, siempre retorna true.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está bloqueado o desbloqueado.
     * No implementamos bloqueo de cuentas, siempre retorna true.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales del usuario (contraseña) han expirado.
     * No implementamos expiración de credenciales, siempre retorna true.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario está habilitado o deshabilitado.
     * Usa el campo 'enabled' para controlar el acceso.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
