package models.exceptions;

/**
 * Exception thrown when a Bugemon with a specific ID cannot be found in the repository.
 *
 * <p>This exception is raised when {@link repositories.BugemonRepository} or
 * {@link services.BugemonService} attempt to load a Bugemon that does not exist.</p>
 *
 * @see repositories.BugemonRepository
 * @see services.BugemonService
 */
public class BugemonNotFoundException extends ElementNotFoundException {

    public BugemonNotFoundException(final String bugemonId) {
        super("Bugemon with ID '" + bugemonId + "' not found");
    }
    public BugemonNotFoundException(final String bugemonId, final Throwable cause) {
        super("Bugemon with ID '" + bugemonId + "' not found", cause);
    }
}
