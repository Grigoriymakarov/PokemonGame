package dto;

import models.exceptions.BugemonNotFoundException;
import models.shared.bugemon.BugemonSpecie;
import models.shared.bugemon.TrainerBugemon;

import java.util.List;

/**
 * Data Transfer Object interface for exposing team information without allowing modifications.
 *
 * <p>This interface provides a read-only view of a team of Bugemon. It allows access to team members
 * through various methods and provides information about the team's composition.</p>
 *
 * @see models.team.Team
 */
public interface TeamDTO {
    /**
     * Gets all members of this team.
     *
     * @return a list of all team members
     */
    public List<? extends TrainerBugemonDTO> getMembers();

    /**
     * Returns the team member of a specific Bugemon specie.
     *
     * @param specie the specie of the Bugemon to find
     * @return the TrainerBugemon of the specified specie
     * @throws BugemonNotFoundException if no Bugemon of the specified specie is found in the team
     */
    TrainerBugemonDTO getMemberOfSpecie(final BugemonSpecie specie) throws BugemonNotFoundException;

    /**
     * Checks if a Bugemon with the given specie is in the team.
     *
     * @param specie the specie to check
     * @return {@code true} if a Bugemon with this specie is in the team
     */
    boolean containsSpecie(final BugemonSpecie specie);

    /**
     * Gets all alive members of the team.
     *
     * @return a list of alive Bugemon in this team
     */
    List<TrainerBugemon> getAliveMembers();
}
