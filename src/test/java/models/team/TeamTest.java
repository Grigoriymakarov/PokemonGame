package models.team;

import models.shared.bugemon.BugemonSpecie;
import models.shared.bugemon.TrainerBugemon;
import models.shared.statistics.Statistics;
import models.shared.Type;
import org.junit.Before;
import org.junit.Test;

import static constants.LogicConstants.TEAM_MAX_SIZE;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Team} class.
 *
 * <p>Tests team creation, member management, size limits, and validation.</p>
 */
public class TeamTest {

    private Team team;
    private TrainerBugemon b1;

    /**
     * Sets up the test fixtures before each test.
     */
    @Before
    public void setUp() {
        team = new Team();
        BugemonSpecie specie = new BugemonSpecie.Builder()
                .id("florachu")
                .name("Florachu")
                .type(Type.FLORA)
                .baseStats(new Statistics(100, 50, 40, 30))
                .attacks(null) // or new ArrayList<>() depending on what your builder accepts
                .sprite("florachu.png")
                .starter(false)
                .build();
        b1 = new TrainerBugemon.Builder()
                .withSpecie(specie)
                .build();
    }


    /**
     * Verifies that a newly created team is empty.
     */
    @Test
    public void newTeamShouldBeEmpty() {
        assertTrue(team.isEmpty());
        assertEquals(0, team.size());
    }

    /**
     * Verifies that adding a Bugemon increases the team size.
     */
    @Test
    public void addShouldIncreaseSize() {
        team.add(b1);
        assertEquals(1, team.size());
        assertTrue(team.containsSpecie(b1.getSpecie()));
    }

    /**
     * Verifies that duplicate Bugemon cannot be added to a team.
     */
    @Test
    public void cannotAddDuplicate() {

        team.add(b1);

        assertFalse(team.canAdd(b1));
        try {
            team.add(b1);
            fail("Adding a duplicate Bugemon should throw an exception");
        } catch (IllegalArgumentException expected) {
            // expected
        }
        assertEquals(1, team.size());
    }

    /**
     * Verifies that a team cannot contain more than six Bugemon.
     */
    @Test
    public void cannotAddMoreThanSix() {

        for (int i = 0; i < TEAM_MAX_SIZE; i++) {
            BugemonSpecie specie = new BugemonSpecie.Builder()
                    .id(String.valueOf(i))
                    .name("Bug" + i)
                    .type(Type.FLORA)
                    .baseStats(new Statistics(100, 50, 40, 30))
                    .attacks(null)
                    .sprite("florachu.png")
                    .starter(false)
                    .build();

            TrainerBugemon bugemon = new TrainerBugemon.Builder()
                    .withSpecie(specie)
                    .build();

            team.add(bugemon);
        }

        BugemonSpecie extraSpecie = new BugemonSpecie.Builder()
                .id("99")
                .name("Extra")
                .type(Type.FLORA)
                .baseStats(new Statistics(100, 50, 40, 30))
                .attacks(null)
                .sprite("florachu.png")
                .starter(false)
                .build();

        TrainerBugemon extraBugemon = new TrainerBugemon.Builder()
                .withSpecie(extraSpecie)
                .build();

        assertFalse(team.canAdd(extraBugemon));
        try {
            team.add(extraBugemon);
            fail("Adding a Bugemon to a full team should throw an exception");
        } catch (IllegalStateException expected) {
            // expected
        }
        assertEquals(TEAM_MAX_SIZE, team.size());
    }

    /**
     * Verifies that removing an existing Bugemon decreases the team size.
     */
    @Test
    public void removeExistingBugemonShouldDecreaseSize() {
        team.add(b1);

        assertTrue(team.canRemove(b1.getSpecie()));
        team.removeBySpecie(b1.getSpecie());
        assertEquals(0, team.size());
        assertFalse(team.containsSpecie(b1.getSpecie()));
    }
    /**
     * Verifies that removing a non-existing Bugemon returns false.
     */
    @Test
    public void removeNonExistingBugemonShouldReturnFalse() {
        assertFalse(team.canRemove(b1.getSpecie()));
        team.removeBySpecie(b1.getSpecie());
        assertEquals(0, team.size());
    }
    /**
     * Verifies that a team is valid only when it contains at least one Bugemon.
     */
    @Test
    public void teamIsValid() {
        assertFalse(team.isValid());

        team.add(b1);

        assertTrue(team.isValid());
    }
}