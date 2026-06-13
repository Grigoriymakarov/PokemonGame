package models.team;

import models.shared.Identifiable;
import models.shared.bugemon.TrainerBugemon;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a saved team for persistence.
 *
 * <p>This class is a data transfer object (DTO) used to serialize and deserialize
 * team data to/from JSON files via {@link repositories.TeamRepository}.</p>
 *
 * <p>Unlike {@link Team} which represents an active team with full
 * {@link TrainerBugemon} objects, {@code SavedTeam} stores only the team name
 * and a list of Bugemon IDs (strings) for lightweight JSON persistence.</p>
 *
 * <p>The team can be reconstructed from a {@code SavedTeam} into a full {@link Team}
 * by {@link services.TeamService#createTeamFromSavedTeam(SavedTeam)}.</p>
 *
 * @param name    The name of the saved team.
 * @param members The list of Bugemon IDs that compose this team.
 * @see Team
 * @see services.TeamService
 * @see repositories.TeamRepository
 */
public record SavedTeam(String name, List<String> members) implements Identifiable {
    /**
     * Creates a new saved team with the given name and members.
     *
     * @param name    the name of the team (must not be {@code null})
     * @param members the list of Bugemon IDs in this team (must not be {@code null})
     */
    public SavedTeam(final String name, final List<String> members) {
        this.name = name;
        this.members = new ArrayList<>(members);
    }

    /**
     * Gets the name of this saved team.
     *
     * @return the team name
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Gets the list of Bugemon IDs in this team.
     *
     * @return an unmodifiable view of the Bugemon IDs
     */
    @Override
    public List<String> members() {
        return Collections.unmodifiableList(this.members);
    }

    @Override
    public String id() {
        return this.name();
    }
}


