package models.exceptions;

/**
 * Exception thrown when a saved team with a specific name cannot be found in the repository.
 *
 * <p>This exception is raised when {@link repositories.TeamRepository} or {@link services.TeamService}
 * attempt to load or access a saved team that does not exist.</p>
 *
 * @see repositories.TeamRepository
 * @see services.TeamService
 */
public class TeamNotFoundException extends ElementNotFoundException {

    public TeamNotFoundException(final String message) {
        super(message);
    }
    public TeamNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
