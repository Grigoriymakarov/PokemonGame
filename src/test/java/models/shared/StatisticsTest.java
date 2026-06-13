package models.shared;

import models.shared.statistics.Statistics;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link Statistics} class.
 *
 * <p>This test class verifies individual statistic increases and the addition
 * of complete statistic sets.</p>
 */
public class StatisticsTest {

    private Statistics stats;

    /**
     * Sets up a baseline set of statistics before each test.
     */
    @Before
    public void setUp() {
        stats = new Statistics(100, 50, 30, 20);
    }

    /**
     * Verifies that increasing the HP statistic adds to the current HP value.
     */
    @Test
    public void increaseStatShouldAddToHp() {
        stats.increaseStat(Statistics.Stat.HP, 10);
        assertEquals(110, stats.getHp());
    }

    /**
     * Verifies that increasing the attack statistic adds to the current attack value.
     */
    @Test
    public void increaseStatShouldAddToAttack() {
        stats.increaseStat(Statistics.Stat.ATTACK, 5);
        assertEquals(55, stats.getAttack());
    }

    /**
     * Verifies that increasing the defense statistic adds to the current defense value.
     */
    @Test
    public void increaseStatShouldAddToDefense() {
        stats.increaseStat(Statistics.Stat.DEFENSE, 5);
        assertEquals(35, stats.getDefense());
    }

    /**
     * Verifies that increasing the initiative statistic adds to the current initiative value.
     */
    @Test
    public void increaseStatShouldAddToInitiative() {
        stats.increaseStat(Statistics.Stat.INITIATIVE, 5);
        assertEquals(25, stats.getInitiative());
    }

    /**
     * Verifies that increasing a statistic with a negative value decreases it.
     */
    @Test
    public void increaseStatShouldWorkWithNegativeValue() {
        stats.increaseStat(Statistics.Stat.ATTACK, -10);
        assertEquals(40, stats.getAttack());
    }

    /**
     * Verifies that adding another statistics object sums every statistic.
     */
    @Test
    public void addShouldSumAllStats() {
        Statistics bonus = new Statistics(10, 5, 3, 2);
        stats.add(bonus);

        assertEquals(110, stats.getHp());
        assertEquals(55,  stats.getAttack());
        assertEquals(33,  stats.getDefense());
        assertEquals(22,  stats.getInitiative());
    }

    /**
     * Verifies that adding zero-valued statistics leaves all values unchanged.
     */
    @Test
    public void addWithZeroStatsShouldNotChange() {
        Statistics zero = new Statistics(0, 0, 0, 0);
        stats.add(zero);

        assertEquals(100, stats.getHp());
        assertEquals(50,  stats.getAttack());
        assertEquals(30,  stats.getDefense());
        assertEquals(20,  stats.getInitiative());
    }

    /**
     * Verifies that adding negative statistics decreases all values.
     */
    @Test
    public void addWithNegativeStatsShouldDecrease() {
        Statistics malus = new Statistics(-10, -5, -3, -2);
        stats.add(malus);

        assertEquals(90, stats.getHp());
        assertEquals(45, stats.getAttack());
        assertEquals(27, stats.getDefense());
        assertEquals(18, stats.getInitiative());
    }
}
