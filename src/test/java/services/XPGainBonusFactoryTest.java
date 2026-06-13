package services;

import models.shared.statistics.Statistics;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link XPGainBonusFactory} class.
 *
 * <p>This test class verifies that generated XP gain bonuses respect the
 * expected stat point distribution rules.</p>
 */
public class XPGainBonusFactoryTest {
    private XPGainBonusFactory xPGainBonusFactory;

    @Before
    public void setUp() {
        xPGainBonusFactory = new XPGainBonusFactory();
    }
    /**
     * Checks whether a generated bonus respects the allowed stat point budget.
     *
     * @param bonus the generated statistics bonus to validate
     * @return {@code true} if the bonus follows the expected distribution rules
     */
    private boolean isValidBonus(Statistics bonus) {
        int totalPoints = 0;
        for  (Statistics.Stat stat : Statistics.Stat.values()) {
            int statVal = bonus.get(stat);

            // 1 point can be invested in 2 HP, 2 INIT, 1 attack or 1 DEFENSE
            float coef = switch (stat) {
                case HP -> 0.5f;
                case ATTACK ->  1;
                case DEFENSE -> 1;
                case INITIATIVE -> 0.5f;
            };
            if (coef == 0.5f) {
                // The stat points should come by pair
                if (statVal%2 != 0) return false;
            }

            totalPoints += statVal * coef;
        }
        // Total points should be 10
        return totalPoints == 10;
    }

    /**
     * Verifies that generated bonuses are always valid across repeated generations.
     */
    @Test
    public void bonusShouldAlwaysBeValid(){
        // Test 100 times if the returned bonus list is valid
        for (int i=0; i<100; ++i) {
            for (Statistics bonus : xPGainBonusFactory.generateBonus()) {
                assertTrue(isValidBonus(bonus));
            }
        }
    }
}
