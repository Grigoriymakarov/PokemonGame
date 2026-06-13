package models.exceptions;

/**
 * Base exception thrown when a requested element cannot be found in a repository or collection.
 *
 * <p>This exception serves as the parent class for more specific "not found" exceptions
 * such as {@link BugemonNotFoundException}, {@link ItemNotFoundException}, and {@link TeamNotFoundException}.</p>
 *
 * @see BugemonNotFoundException
 * @see ItemNotFoundException
 * @see TeamNotFoundException
 */
public class ElementNotFoundException extends Exception {
    public ElementNotFoundException(String message) {
        super(message);
    }
    public ElementNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
