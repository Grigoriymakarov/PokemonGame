package services;

import models.exceptions.*;
import models.shared.bugemon.BugemonSpecie;
import models.team.SavedTeam;
import models.team.Team;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repositories.AttackRepository;
import repositories.BugemonRepository;
import repositories.TeamRepository;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link TeamService} class.
 *
 * <p>This test class verifies team member management, validation,
 * persistence operations, and saved team restoration.</p>
 */
public class TeamServiceTest {

    private TeamService teamService;
    private static final String ID   = "florachu";
    private static final String ID2 = "pyricore";
    private static final String TEAM_NAME = "equipe";

    /**
     * Builds repository data, creates the team service, and removes test data before each test.
     *
     * @throws IOException if repository data cannot be loaded or test data cannot be deleted
     */
    @Before
    public void setUp() throws IOException {
        BugemonRepository.getInstance().buildDataBase();
        BugemonService bugemonService = new BugemonService();
        teamService = new TeamService(bugemonService);
        this.tearDown();
    }

    /**
     * Removes the saved test team after each test.
     *
     * @throws IOException if test data cannot be deleted
     */
    @After
    public void tearDown() throws IOException {
        try {
            TeamRepository.getInstance().findByName(TEAM_NAME);
            TeamRepository.getInstance().deleteTeam(TEAM_NAME);
        } catch (ElementNotFoundException e) {
            // If the team doesn't exist, there's nothing to delete, so we can ignore this exception.
        }
    }

    /**
     * Verifies that adding a Bugemon increases the team size.
     */
    @Test
    public void addBugemon_teamSizeIncreases() throws CannotAddBugemonToTeamException, BugemonNotFoundException {
        teamService.addBugemon(ID);
        assertEquals("The team size should be 1", 1, teamService.getTeamSize());
    }

    /**
     * Verifies that adding the same Bugemon twice throws an exception.
     */
    @Test
    public void addBugemon_duplicateBugemon() throws CannotAddBugemonToTeamException, BugemonNotFoundException {
        teamService.addBugemon(ID);
        assertThrows(TeamAlreadyHasSpecieException.class, () -> {
            teamService.addBugemon(ID);
        });
    }

    /**
     * Verifies that removing an existing Bugemon decreases the team size.
     */
    @Test
    public void removeBugemon_teamSizeDecreases() throws BugemonNotFoundException, CannotAddBugemonToTeamException {
        teamService.addBugemon(ID);
        teamService.removeBugemon(ID);
        assertEquals("The team size should be 0 after removal", 0, teamService.getTeamSize());
    }

    /**
     * Verifies that removing an absent Bugemon returns false.
     */
    @Test
    public void removeBugemon_nonExistingId() {
        assertThrows(BugemonNotFoundException.class, () -> {
            teamService.removeBugemon("id_inexistant");
        });
    }

    /**
     * Verifies that an empty team is invalid.
     */
    @Test
    public void emptyTeamShouldBeInvalid() {
        assertFalse("An empty team should be invalid", teamService.isTeamValid());
    }

    /**
     * Verifies that a team with one member is valid.
     */
    @Test
    public void teamWithOneMemberShouldBeValid() throws CannotAddBugemonToTeamException, BugemonNotFoundException {
        teamService.addBugemon(ID);
        assertTrue("A team with one member should be valid", teamService.isTeamValid());
    }

    /**
     * Verifies that resetting the team removes all members.
     */
    @Test
    public void resetTeam_clearsAllMembers() throws CannotAddBugemonToTeamException, BugemonNotFoundException {
        teamService.addBugemon(ID);
        teamService.resetTeam();
        assertEquals("After reset, the team should be empty", 0, teamService.getTeamSize());
    }

    /**
     * Verifies that a Bugemon is reported as present after being added.
     */
    @Test
    public void bugemonShouldBePresentAfterAdd() throws BugemonNotFoundException, CannotAddBugemonToTeamException {
        teamService.addBugemon(ID);
        assertTrue("containsSpecie should return true after adding the Bugemon",
                teamService.containsSpecie(ID));
    }

    /**
     * Verifies that a Bugemon is reported as absent when it has not been added.
     */
    @Test
    public void bugemonShouldBeAbsentWhenNotAdded() throws BugemonNotFoundException {
        assertFalse("containsSpecie should return false when the Bugemon was not added",
                teamService.containsSpecie(ID));
    }

    /**
     * Verifies that loading a saved team restores its members.
     *
     * @throws IOException if the team cannot be saved or loaded
     */
    @Test
    public void loadSavedTeam_restoresTeam() throws IOException, BugemonNotFoundException, TeamNotFoundException, InvalidTeamNameException, InvalidTeamCompositionException, CannotAddBugemonToTeamException {
        teamService.addBugemon(ID);
        teamService.saveCurrentTeam(TEAM_NAME);

        teamService.resetTeam();
        assertEquals("The team should be empty before loading", 0, teamService.getTeamSize());

        teamService.loadSavedTeam(TEAM_NAME);
        assertEquals("The loaded team should have 1 member", 1, teamService.getTeamSize());
        assertTrue("The loaded team should contain the expected Bugemon",
                teamService.containsSpecie(ID));
    }

    /**
     * Verifies that saving an empty team throws an exception.
     *
     * @throws IOException if the save operation fails
     */
    @Test(expected = InvalidTeamCompositionException.class)
    public void saveCurrentTeam_emptyTeam() throws IOException, InvalidTeamNameException, InvalidTeamCompositionException {
        teamService.saveCurrentTeam(TEAM_NAME);
    }

    /**
     * Verifies that saving a team with an existing name throws an exception.
     *
     * @throws IOException if the save operation fails
     */
    @Test(expected = InvalidTeamNameException.class)
    public void saveCurrentTeam_duplicateName() throws IOException, InvalidTeamNameException, InvalidTeamCompositionException, CannotAddBugemonToTeamException, BugemonNotFoundException {
        teamService.addBugemon(ID);
        teamService.saveCurrentTeam(TEAM_NAME);
        teamService.saveCurrentTeam(TEAM_NAME);
    }

    /**
     * Verifies that loading an unknown saved team name throws an exception.
     *
     * @throws IOException if the load operation fails
     */
    @Test(expected = TeamNotFoundException.class)
    public void loadTeamWithNonExistingName() throws IOException, TeamNotFoundException, BugemonNotFoundException {
        teamService.loadSavedTeam("equipe_qui_nexiste_pas");
    }

    /**
     * Verifies that deleting a saved team makes it unavailable for loading.
     *
     * @throws IOException if the team cannot be saved or deleted
     */
    @Test
    public void deleteSavedTeam_NoLongerExists() throws IOException, TeamNotFoundException, InvalidTeamNameException, InvalidTeamCompositionException, CannotAddBugemonToTeamException, BugemonNotFoundException {
        teamService.addBugemon(ID);
        teamService.saveCurrentTeam(TEAM_NAME);
        teamService.deleteSavedTeam(TEAM_NAME);

        assertThrows(TeamNotFoundException.class,
                () -> teamService.loadSavedTeam(TEAM_NAME));
    }

    /**
     * Verifies that creating a team from saved data restores the expected Bugemon.
     */
    @Test
    public void createTeamFromSavedTeam_restoresCorrectBugemons() throws BugemonNotFoundException, CannotAddBugemonToTeamException {
        teamService.addBugemon(ID);
        SavedTeam saved = teamService.createSavedTeam("test");

        Team restored = teamService.createTeamFromSavedTeam(saved);
        assertEquals("The restored team should have 1 member", 1, restored.size());

        final BugemonSpecie specie = new BugemonService().getSpecieFromId(ID);
        assertTrue("The restored team should contain the expected Bugemon",
                restored.containsSpecie(specie));
    }
}
