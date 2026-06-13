package services;

import models.exceptions.*;
import models.shared.bugemon.BugemonSpecie;
import models.shared.bugemon.TrainerBugemon;
import models.team.SavedTeam;
import models.team.Team;
import repositories.TeamRepository;

import java.io.IOException;
import java.util.List;

import static constants.MessageConstants.*;

/**
 * Service managing operations on the player's team during selection.
 *
 * <p>This service connects the {@link controllers.team.TeamSelectionController}
 * with the {@link Team} model. It delegates validation logic to {@link Team}
 * and uses {@link BugemonService} to resolve Bugémons from their IDs.</p>
 *
 * @see Team
 * @see controllers.team.TeamSelectionController
 */
public class TeamService {

    /** The current player's team model. */
    private Team team;

    /** Service used to resolve Bugémons from their IDs. */
    private final BugemonService bugemonService;

    /** Repository used to write in and out saved teams from its JSON file*/
    private final TeamRepository teamRepository;

    /**
     * Creates a {@code TeamService} with an empty team.
     *
     * @param bugemonService the Bugémon service to use for ID resolution
     */
    public TeamService(final BugemonService bugemonService) throws IOException {
        this.team = new Team();
        this.bugemonService = bugemonService;
        this.teamRepository = TeamRepository.getInstance();
        this.teamRepository.buildDataBase();
    }

    /**
     * Returns the current team.
     *
     * @return the current team
     */
    public Team getTeam() {
        return this.team;
    }

    /**
     * Adds a Bugémon to the team using its ID.
     *
     * @param bugemonId the ID of the Bugémon to add
     * @throws BugemonNotFoundException if no Bugémon exists with the given ID
     * @throws TeamFullException if the team already has the maximum number of members
     * @throws TeamAlreadyHasSpecieException if the team already contains a Bugémon of the same specie
     * @throws CannotAddBugemonToTeamException if the Bugémon cannot be added to the team for any other reason (e.g., invalid ID)
     */
    public void addBugemon(final String bugemonId) throws BugemonNotFoundException, TeamFullException, TeamAlreadyHasSpecieException, CannotAddBugemonToTeamException {
        final TrainerBugemon bugemon = this.bugemonService.createTrainerBugemon(bugemonId);
        if (this.team.isFull()) {
            throw new TeamFullException();
        } else if (this.team.containsSpecie(bugemon.getSpecie())) {
            throw new TeamAlreadyHasSpecieException(bugemon.getSpecie());
        }
        this.team.add(bugemon);
    }

    /**
     * Removes a Bugémon from the team using its ID.
     *
     * @param bugemonId the ID of the Bugémon to remove
     * @return {@code true} if the Bugémon was found and removed, {@code false} otherwise
     */
    public boolean removeBugemon(final String bugemonId) throws BugemonNotFoundException {
        final BugemonSpecie specie = this.bugemonService.getSpecieFromId(bugemonId);
        if (!this.team.canRemove(specie)) {
            return false;
        }
        this.team.removeBySpecie(specie);
        return true;
    }

    /**
     * Returns whether the team is valid for battle (at least 1 Bugémon).
     *
     * @return {@code true} if the team contains at least one member
     */
    public boolean isTeamValid() {
        return this.team.isValid();
    }

    /**
     * Returns the current number of Bugémons in the team.
     *
     * @return the number of members
     */
    public int getTeamSize() {
        return this.team.size();
    }

    /**
     * Resets the team by removing all its members.
     */
    public void resetTeam() {
        this.team.clear();
    }

    /**
     * Checks if a Bugémon specie with the given ID is already in the team.
     *
     * @param id the ID to check
     * @return {@code true} if this Bugémon is already in the team
     */
    public boolean containsSpecie(final String id) throws BugemonNotFoundException {
        final BugemonSpecie specie = this.bugemonService.getSpecieFromId(id);
        return this.containsSpecie(specie);
    }

    /**
     * Checks if a Bugémon specie is already in the team.
     *
     * @param specie the specie to check
        * @return {@code true} if this Bugémon is already in the team
     */
    public boolean containsSpecie(final BugemonSpecie specie) {
        return this.team.containsSpecie(specie);
    }


    /**
     * Saves the current team to persistent storage with the given name.
     *
     * <p>This method validates the team, ensures the name is unique, and persists
     * the team via {@link TeamRepository#addTeam(SavedTeam)}.</p>
     *
     * <p>Validation checks:</p>
     * <ul>
     *   <li>Team name must not be {@code null} or blank</li>
     *   <li>Team must contain at least one Bugemon</li>
     *   <li>Team name must not already exist in saved teams</li>
     * </ul>
     *
     * @param name the name for the saved team
     * @throws InvalidTeamNameException if the team name is null, blank, or already exists
     * @throws InvalidTeamCompositionException if the team is empty
     * @throws IOException if a persistence error occurs
     */
    public void saveCurrentTeam(final String name) throws IOException, InvalidTeamNameException, InvalidTeamCompositionException {
        if (name == null || name.isBlank()) throw new InvalidTeamNameException(ERROR_TEAM_ILLEGAL_NAME);
        if (!this.team.isValid()) throw new InvalidTeamCompositionException(ERROR_TEAM_EMPTY_PARENTHESIZED);
        if (this.teamRepository.checkNameOfTeam(name)){throw new InvalidTeamNameException(ERROR_TEAM_NAME_ALREADY_USED);}
        this.teamRepository.addTeam(this.createSavedTeam(name));
    }

    /**
     * Loads a previously saved team by name and sets it as the current team.
     *
     * <p>This method retrieves the saved team from persistent storage via
     * {@link TeamRepository#findByName(String)} and reconstructs it into
     * a full {@link Team} object with {@link #createTeamFromSavedTeam(SavedTeam)}.</p>
     *
     * @param name the name of the saved team to load
     * @throws TeamNotFoundException if no team with the given name exists
     * @throws BugemonNotFoundException if a Bugemon referenced in the saved team cannot be found
     * @throws IOException if a persistence error occurs
     */
    public void loadSavedTeam(final String name) throws IOException, TeamNotFoundException, BugemonNotFoundException {
        final SavedTeam savedTeam;
        try {
            savedTeam = this.teamRepository.findByName(name);
        } catch (ElementNotFoundException e) {
            throw new TeamNotFoundException("Cannot find team: " + name, e);
        }
        this.setTeam(this.createTeamFromSavedTeam(savedTeam));
    }

    /**
     * Gets the list of all saved team names.
     *
     * <p>Retrieves the names of all teams stored in persistent storage.</p>
     *
     * @return a list of team names
     * @throws IOException if a persistence error occurs
     */
    public List<String> getAllSavedTeamNames() throws IOException {
        return this.teamRepository.getAllTeamNames();
    }

    /**
     * Deletes a saved team from persistent storage.
     *
     * <p>Removes the team with the given name from the repository.
     * If the team does not exist, no action is taken.</p>
     *
     * @param name the name of the team to delete
     * @throws IOException if a persistence error occurs
     */
    public void deleteSavedTeam(final String name) throws IOException, TeamNotFoundException {
        this.teamRepository.deleteTeam(name);
    }

    /**
     * Sets the current team, replacing the existing one.
     *
     * <p>This method validates that the provided team is not {@code null}
     * and contains at least one Bugemon before replacing the current team.</p>
     *
     * @param team the new team to set (must not be {@code null} and must be valid)
     * @throws IllegalArgumentException if the team is {@code null} or invalid
     */
    public void setTeam(final Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team cannot be null");
        }
        if (!team.isValid()) {
            throw new IllegalArgumentException("Team must contain at least one member");
        }
        this.team = team;
    }

    /**
     * Reconstructs a full {@link Team} from a saved team.
     *
     * <p>This method takes a {@code SavedTeam} (which contains only names and IDs)
     * and creates a new {@link Team} with full {@link TrainerBugemon} objects
     * resolved via {@link BugemonService#createTrainerBugemon(String)}.</p>
     *
     * @param savedTeam the saved team to reconstruct (must not be {@code null})
     * @return a new Team containing the reconstructed Bugemon members
     */
    public Team createTeamFromSavedTeam(final SavedTeam savedTeam) throws BugemonNotFoundException {
        final Team team = new Team();

        for (final String memberId : savedTeam.members()) {
            team.add(this.bugemonService.createTrainerBugemon(memberId));
        }

        return team;
    }

    /**
     * Converts the current team to a {@link SavedTeam} for persistence.
     *
     * <p>This method extracts the IDs of all team members and packages them
     * with the given team name into a {@code SavedTeam} object suitable for
     * serialization to JSON.</p>
     *
     * @param teamName the name to give the saved team
     * @return a SavedTeam representation of the current team
     */
    public SavedTeam createSavedTeam(final String teamName) {
        // .getSpecie().getId() is explicitly used here because of the inherent coupling of TeamService with Specie ID
        // creating a method for Demeter rule would not be appropriate since the Specie ID should not be commonly used
        // in business logic
        final List<String> ids = this.team.getMembers().stream()
                .map(tb -> tb.getSpecie().id())
                .toList();

        return new SavedTeam(teamName, ids);
    }
    }



