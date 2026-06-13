package models.shared.statistics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link StatsModifier} class.
 *
 * <p>This test class verifies how statistic modifiers handle malus reset behavior.</p>
 */
public class StatsModifierTest {

    /**
     * Verifies that resetting maluses clears negative statistic modifiers.
     */
    @Test
    public void resetMalusesShouldOnlyClearNegativeStats() {
        StatsModifier modifier = new StatsModifier(-2, -4, -3, -2);

        modifier.resetMaluses();

        assertEquals(0, modifier.getHp());
        assertEquals(0, modifier.getAttack());
        assertEquals(0, modifier.getDefense());
        assertEquals(0, modifier.getInitiative());
    }
}
