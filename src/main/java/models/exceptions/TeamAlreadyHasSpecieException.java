package models.exceptions;

import models.shared.bugemon.BugemonSpecie;

/**
 * Exception thrown when attempting to add a Bugemon species to a team that already contains that species.
 *
 * <p>Teams cannot have duplicate species. This exception is raised by {@link models.team.Team}
 * when a user attempts to add a Bugemon of a species already present in their team.</p>
 *
 * @see models.team.Team
 * @see CannotAddBugemonToTeamException
 */
public class TeamAlreadyHasSpecieException extends CannotAddBugemonToTeamException {
    public TeamAlreadyHasSpecieException(BugemonSpecie specie) {
        super("Team already has a Bugemon of the same specie: " + specie.getName());
    }
}
