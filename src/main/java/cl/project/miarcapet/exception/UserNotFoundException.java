package cl.project.miarcapet.exception;

/**
 * Excepción lanzada cuando un usuario no puede ser encontrado por email o ID.
 * Típicamente usado durante autenticación u operaciones de búsqueda de usuarios.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje personalizado.
     * @param message Descripción del error
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
