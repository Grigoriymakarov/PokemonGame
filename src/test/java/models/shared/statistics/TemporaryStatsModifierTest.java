package models.shared.statistics;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link TemporaryStatsModifier} class.
 *
 * <p>This test class verifies remaining time validation and activation state
 * changes for temporary statistic modifiers.</p>
 */
public class TemporaryStatsModifierTest {

    /**
     * Verifies that a positive remaining time is stored and activates the modifier.
     */
    @Test
    public void positiveRemainingTimeShouldBeAccepted() {
        TemporaryStatsModifier modifier = new TemporaryStatsModifier(1);

        assertEquals(1, modifier.getRemainingTime());
        assertTrue(modifier.isActive());
    }

    /**
     * Verifies that a non-positive remaining time is rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void nonPositiveRemainingTimeShouldBeRejected() {
        new TemporaryStatsModifier(0);
    }

    /**
     * Verifies that shortening the remaining time to zero deactivates the modifier.
     */
    @Test
    public void shortenRemainingTimeToZeroShouldDeactivateModifier() {
        TemporaryStatsModifier modifier = new TemporaryStatsModifier(1);

        modifier.shortenRemainingTime(1);

        assertEquals(0, modifier.getRemainingTime());
        assertFalse(modifier.isActive());
    }
}
