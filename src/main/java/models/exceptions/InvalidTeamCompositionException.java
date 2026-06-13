package models.exceptions;

/**
 * Exception thrown when a team has an invalid composition.
 *
 * <p>Invalid compositions may include teams with insufficient members (below minimum team size)
 * or teams that violate other team composition rules defined by {@link constants.LogicConstants}.</p>
 *
 * @see models.team.Team
 * @see constants.LogicConstants
 */
public class InvalidTeamCompositionException extends Exception {
    public InvalidTeamCompositionException(String message) {
        super(message);
    }
}
