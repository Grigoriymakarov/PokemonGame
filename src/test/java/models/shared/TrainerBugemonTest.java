package models.shared;

import models.shared.bugemon.BugemonSpecie;
import models.shared.bugemon.TrainerBugemon;
import models.shared.statistics.Statistics;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link TrainerBugemon} class.
 *
 * <p>Tests damage application, healing, HP management, and combat reset.</p>
 */
public class TrainerBugemonTest {

    private TrainerBugemon bugemon;
    private static final int BASE_HP = 100;

    /**
     * Sets up the test fixtures before each test.
     */
    @Before
    public void setUp() {
        BugemonSpecie specie = new BugemonSpecie.Builder()
                .id("florachu")
                .name("Florachu")
                .type(Type.FLORA)
                .baseStats(new Statistics(BASE_HP, 50, 30, 10))
                .attacks(new ArrayList<>())
                .sprite("florachu.png")
                .starter(true)
                .build();

        bugemon = new TrainerBugemon.Builder()
                .withSpecie(specie)
                .build();
    }

    /**
     * Verifies that applying damage reduces the Bugemon's current HP.
     */
    @Test
    public void applyDamageShouldReduceHP() {
        bugemon.applyDamage(30);
        assertEquals(70, bugemon.getCurrentHP());
    }


    /**
     * Verifies that applying damage equal to max HP marks the Bugemon as dead.
     */
    @Test
    public void applyDamageEqualToHPShouldMarkAsDead() {
        bugemon.applyDamage(BASE_HP);
        assertFalse(bugemon.isAlive());
    }

    /**
     * Verifies that applying damage less than max HP keeps the Bugemon alive.
     */
    @Test
    public void applyDamageLessThanHPShouldStayAlive() {
        bugemon.applyDamage(50);
        assertTrue(bugemon.isAlive());
    }

    /**
     * Verifies that healing increases the Bugemon's current HP.
     */
    @Test
    public void healShouldIncreaseHP() {
        bugemon.applyDamage(50);
        bugemon.heal(20);
        assertEquals(70, bugemon.getCurrentHP());
    }


    /**
     * Verifies that healing cannot exceed the maximum HP.
     */
    @Test
    public void healShouldNotExceedMaxHP() {
        bugemon.applyDamage(10);
        bugemon.heal(1000);
        assertEquals(BASE_HP, bugemon.getCurrentHP());
    }

    /**
     * Verifies that resetting for combat restores the Bugemon to maximum HP.
     */
    @Test
    public void resetForCombatShouldRestoreMaxHP() {
        bugemon.applyDamage(60);
        bugemon.resetForCombat();
        assertEquals(BASE_HP, bugemon.getCurrentHP());
    }
}