package models.exceptions;

/**
 * Exception thrown when a team is given an invalid name.
 *
 * <p>Invalid names may include empty strings, names with invalid characters,
 * or names that violate naming constraints defined by the application.</p>
 *
 * @see models.team.Team
 * @see services.TeamService
 */
public class InvalidTeamNameException extends Exception {
    public InvalidTeamNameException(String message) {
        super(message);
    }
}
