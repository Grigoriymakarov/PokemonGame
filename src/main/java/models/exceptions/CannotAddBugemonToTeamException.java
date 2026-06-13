package models.exceptions;

/**
 * Base exception thrown when a Bugemon cannot be added to a team.
 *
 * <p>This exception serves as the parent class for specific team composition errors
 * such as {@link TeamFullException} and {@link TeamAlreadyHasSpecieException}.</p>
 *
 * @see TeamFullException
 * @see TeamAlreadyHasSpecieException
 * @see models.team.Team
 */
public class CannotAddBugemonToTeamException extends Exception {
    public CannotAddBugemonToTeamException(String message) {
        super(message);
    }
    public CannotAddBugemonToTeamException(String message, Throwable cause) {
        super(message, cause);
    }
}
