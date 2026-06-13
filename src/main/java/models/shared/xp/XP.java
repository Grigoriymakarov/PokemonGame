package models.shared.xp;

/**
 * Represents the experience points and level of a Bugemon.
 *
 * <p>This class tracks a Bugemon's progression through levels, starting at level 1 with 0 XP.
 * It provides methods to gain XP, check for level-ups, and calculate experience required
 * for the next level.</p>
 *
 * <p>The class is mutable and designed to be used within a {@link models.shared.bugemon.TrainerBugemon}
 * instance to track progression during the game.</p>
 *
 * @see models.shared.bugemon.TrainerBugemon
 * @see LevelUpEvent
 */
public class XP {

    private int level;
    private int currentXP;

    /**
     * Creates a new XP tracker initialized to level 1 with 0 experience points.
     */
    public XP() {
        this.level = 1;
        this.currentXP = 0;
    }

    /**
     * Creates a new XP tracker with the specified level and current experience.
     *
     * @param level the initial level
     * @param currentXP the initial current experience points
     */
    public XP(final int level, final int currentXP) {
        this.level = level;
        this.currentXP = currentXP;
    }

    /**
     * Creates a copy of the given XP tracker.
     *
     * <p>This copy constructor produces an independent instance with the same
     * level and experience values, suitable for creating independent Bugemon instances.</p>
     *
     * @param other the XP tracker to copy
     */
    public XP(final XP other) {
        this.level = other.level;
        this.currentXP = other.currentXP;
    }

    /**
     * Returns the current level.
     *
     * @return the current level
     */
    public int getLevel()       { return this.level; }

    /**
     * Returns the current experience points.
     *
     * @return the current experience points within the current level
     */
    public int getCurrentXP()   { return this.currentXP; }

    /**
     * Calculates the experience required to reach the next level from the current level.
     *
     * @return the amount of XP needed to level up
     */
    public int getXPToNextLevel() {
        return 50 + 50 * (this.level - 1);
    }

    /**
     * Adds experience points to this tracker and handles automatic level-ups.
     *
     * <p>If the added experience causes the total to exceed the requirement for the next level,
     * the level increases and excess experience is carried over. Multiple level-ups can occur
     * if the amount is large enough.</p>
     *
     * @param amount the experience points to add (must be non-negative)
     * @throws IllegalArgumentException if amount is negative
     */
    public void addXP(final int amount) {
        if (amount < 0) throw new IllegalArgumentException("XP amount must be >= 0");

        this.currentXP += amount;
        while (this.currentXP >= this.getXPToNextLevel()) { // If XP gained allows for multiple levelup
            this.currentXP -= this.getXPToNextLevel();
            this.level++;
        }
    }
}