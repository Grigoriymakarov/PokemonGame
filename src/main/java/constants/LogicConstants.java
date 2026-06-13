package constants;

import models.shared.statistics.Statistics;

import java.util.Collection;

/** Constants used in the business layer
 */
public final class LogicConstants {
    private LogicConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    // Tower logic
    /** Highest floor number in the tower. */
    public static final int TOWER_MAX_FLOOR = 9;
    /** Lowest floor number in the tower. */
    public static final int TOWER_MIN_FLOOR = 2;

    // Team creation
    /** Maximum number of Bugemons in a team. */
    public static final int TEAM_MAX_SIZE = 6;
    /** Minimum number of Bugemons for a team to be valid. */
    public static final int TEAM_MIN_SIZE = 1;

    // Level-up bonus generation
    public static final int BONUS_NUMBER_CHOICES = 3;
    public static final int BONUS_MAX_POINTS = 10;
    public static final Collection<Statistics> FIXED_BONUSES = java.util.List.of(
            new Statistics(20, 0, 0, 0),
            new Statistics(0, 10,0,0),
            new Statistics(0, 0,10,0),
            new Statistics(0, 0,0,20)
    );

    // Combat multipliers
    public static final float SUPER_EFFECTIVE_MULTIPLIER = 1.5f;
    public static final float NOT_VERY_EFFECTIVE_MULTIPLIER = 0.75f;
    public static final float NEUTRAL_MULTIPLIER = 1.0f;
    public static final float CRITICAL_HIT_MULTIPLIER = 1.5f;
    public static final double CRITICAL_HIT_THRESHOLD = 0.9;

    // XP constants
    public static final int BASE_XP_REWARD = 30;
}
