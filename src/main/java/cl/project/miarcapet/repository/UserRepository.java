package cl.project.miarcapet.repository;

import cl.project.miarcapet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interfaz de repositorio para la entidad User.
 * Extiende JpaRepository para proporcionar operaciones CRUD y métodos de consulta personalizados.
 * Spring Data JPA implementará automáticamente esta interfaz en tiempo de ejecución.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su dirección de email.
     * Se usa durante la autenticación para cargar los detalles del usuario.
     *
     * @param email La dirección de email a buscar
     * @return Optional que contiene el User si se encuentra, Optional vacío en caso contrario
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si ya existe un usuario con el email dado.
     * Se usa durante el registro para prevenir cuentas duplicadas.
     *
     * @param email La dirección de email a verificar
     * @return true si existe un usuario con este email, false en caso contrario
     */
    boolean existsByEmail(String email);
}
