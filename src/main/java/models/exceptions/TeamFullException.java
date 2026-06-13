package models.exceptions;

/**
 * Exception thrown when attempting to add a Bugemon to a team that is already at maximum capacity.
 *
 * <p>This exception is raised by {@link models.team.Team} when the team has reached the
 * maximum allowed size (defined by {@link constants.LogicConstants#TEAM_MAX_SIZE}).</p>
 *
 * @see models.team.Team
 * @see CannotAddBugemonToTeamException
 */
public class TeamFullException extends CannotAddBugemonToTeamException {
    public TeamFullException() {
        super("Cannot add more Bugemons to the team: the team is already full.");
    }
}
