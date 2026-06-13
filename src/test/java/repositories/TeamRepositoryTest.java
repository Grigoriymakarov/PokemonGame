package repositories;

import models.exceptions.ElementNotFoundException;
import models.exceptions.TeamNotFoundException;
import models.team.SavedTeam;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;


/**
 * Unit tests for the {@link TeamRepository} class.
 *
 <p>This test class verifies the behavior of the repository responsible
 for storing and retrieving {@link SavedTeam} objects.</p>
 *
 * <p>The tests define the expected behavior for story H2 (team persistence),
 * including saving teams, loading teams, preventing duplicates, and handling
 * invalid teams.</p>
 */
public class TeamRepositoryTest {

    private TeamRepository repository;
    private SavedTeam savedTeam;

    /**
     * Initializes the test environment before each test.
     *
     * <p>This method creates a new repository instance and initializes
     * a sample team with Bugemon instances used in the tests.</p>
     */
    @Before
    public void setUp() {
        repository = TeamRepository.getInstance();

        savedTeam = new SavedTeam(
                "Equipe",
                Arrays.asList("florachu", "moussil")
        );
    }

    /**
     * Verifies that saving a team stores it correctly in the repository.
     *
     * <p>After saving the team, its name should appear in the list of
     * saved teams returned by the repository.</p>
     */
    @Test
    public void addTeamShouldStoreTeam() throws IOException {
        repository.addTeam(savedTeam);

        assertTrue(repository.getSavedTeams().stream()
                .anyMatch(t -> t.name().equals("Equipe")));
    }

    /**
     * Verifies that loading an existing team returns a valid team object.
     * <p>The returned team should not be null and should have the expected name.</p>
     */
    @Test
    public void getSavedTeamByNameShouldWork() throws IOException, ElementNotFoundException {
        repository.addTeam(savedTeam);

        SavedTeam loaded = repository.findByName("Equipe");

        assertNotNull(loaded);
        assertEquals("Equipe", loaded.name());
    }

    /**
     * Verifies that the loaded team contains the same Bugemon members
     * as the team that was saved.
     *
     * <p>This ensures that the persistence mechanism correctly preserves
     * the team composition.</p>
     */
    @Test
    public void loadedTeamShouldContainCorrectMembers() throws IOException, ElementNotFoundException {
        repository.addTeam(savedTeam);

        SavedTeam loaded = repository.findByName("Equipe");

        assertEquals(2, loaded.members().size());
        assertEquals(savedTeam.members(), loaded.members());
    }

    /**
     * Verifies that the repository correctly detects an existing team name.
     *
     * <p>After adding a team to the repository, calling
     * {@code checkNameOfTeam} with the same name should return {@code true},
     * indicating that the name is already used.</p>
     *
     * @throws IOException if an error occurs while accessing the repository
     */
    @Test
    public void checkNameShouldDetectDuplicate() throws IOException, ElementNotFoundException {
        repository.addTeam(savedTeam);

        assertNotNull(repository.findByName("Equipe"));
    }


    /**
     * Verifies that deleting a saved team removes it from the repository.
     *
     * <p>After deletion, the team's name should no longer appear
     * in the list of saved teams.</p>
     */
    @Test
    public void deleteTeamShouldRemoveIt() throws IOException, ElementNotFoundException {
        repository.addTeam(savedTeam);
        repository.deleteTeam(savedTeam.name());

        assertThrows(ElementNotFoundException.class, () -> repository.findByName(savedTeam.name()));
    }
}