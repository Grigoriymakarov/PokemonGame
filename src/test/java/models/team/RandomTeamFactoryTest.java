package models.team;

import models.shared.bugemon.TrainerBugemon;
import org.junit.Before;
import org.junit.Test;
import repositories.AttackRepository;
import repositories.BugemonRepository;
import services.BugemonService;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link RandomTeamFactory} class.
 *
 * <p>This test class verifies random team creation, generated team size,
 * generated members, and basic random variation.</p>
 */
public class RandomTeamFactoryTest {

    private RandomTeamFactory factory;

    /**
     * Builds the repository data needed by the random team factory before each test.
     *
     * @throws IOException if repository data cannot be loaded
     */
    @Before
    public void setUp() throws IOException {
        BugemonRepository.getInstance().buildDataBase();
        factory = new RandomTeamFactory(new BugemonService());
    }

    /**
     * Verifies that creating a team returns a team with the requested size.
     */
    @Test
    public void createTeamShouldReturnTeamWithCorrectSize() {
        Team team = factory.createTeam(3);
        assertEquals(3, team.size());
    }

    /**
     * Verifies that a generated team does not contain null Bugemon members.
     */
    @Test
    public void createTeamShouldNotContainNullBugemons() {
        Team team = factory.createTeam(4);
        List<TrainerBugemon> members = team.getMembers();

        for (TrainerBugemon bugemon : members) {
            assertNotNull(bugemon);
        }
    }

    /**
     * Verifies that repeated team creation can produce different teams.
     */
    @Test
    public void createTeamCalledTwiceShouldNotAlwaysReturnSameTeam() {
        String firstName = factory.createTeam(3).getMembers().get(0).getSpecie().id();
        boolean foundDifference = false;

        for (int i = 0; i < 20; i++) {
            Team team = factory.createTeam(3);
            String id = team.getMembers().get(0).getSpecie().id();
            if (!id.equals(firstName)) {
                foundDifference = true;
                break;
            }
        }

        assertTrue("The generated team should vary randomly", foundDifference);
    }
}
