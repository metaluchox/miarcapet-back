package cl.project.miarcapet.exception;

/**
 * Excepción lanzada cuando se intenta registrar un usuario con un email que ya existe.
 * Esta es una excepción verificada para manejo explícito durante el registro.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje personalizado.
     * @param message Descripción del error
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
