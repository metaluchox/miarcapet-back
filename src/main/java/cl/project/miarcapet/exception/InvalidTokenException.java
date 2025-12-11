package cl.project.miarcapet.exception;

/**
 * Excepción lanzada cuando un token JWT es inválido, expirado o mal formado.
 * Se usa en procesos de validación de tokens y autenticación.
 */
public class InvalidTokenException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje personalizado.
     * @param message Descripción de por qué el token es inválido
     */
    public InvalidTokenException(String message) {
        super(message);
    }

    /**
     * Construye la excepción con un mensaje y la causa subyacente.
     * @param message Descripción del error
     * @param cause La excepción subyacente que causó este error
     */
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
