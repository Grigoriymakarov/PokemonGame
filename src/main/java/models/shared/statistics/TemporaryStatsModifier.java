package models.shared.statistics;

/**
 * Represents a statistical modifier that expires after a set number of combat turns.
 * <p>
 * This class extends {@link StatsModifier} by adding a duration logic.
 * It is typically used for temporary buffs or debuffs (e.g., an attack boost
 * that lasts for 3 turns).
 * </p>
 */
public class TemporaryStatsModifier extends StatsModifier {

    /** The number of turns remaining before the modifier expires. */
    private int remainingTime;

    /** Flag indicating whether the modifier should still be applied. */
    private boolean active;

    /**
     * Constructs a TemporaryStatsModifier with specified stat values and remaining time.
     *
     * @param hp the HP modifier value
     * @param attack the attack modifier value
     * @param def the defense modifier value
     * @param init the initiative modifier value
     * @param remainingTime the number of turns this modifier remains active
     * @throws IllegalArgumentException if remainingTime is not positive
     */
    public TemporaryStatsModifier(final int hp, final int attack, final int def, final int init, final int remainingTime) {
        super(hp, attack, def, init);
        if (remainingTime <= 0) {throw new IllegalArgumentException("Remaining time must be positive");}
        this.remainingTime = remainingTime;
        this.active = true;
    }

    /**
     * Constructs a TemporaryStatsModifier with zero stat values and specified remaining time.
     *
     * @param remainingTime the number of turns this modifier remains active
     * @throws IllegalArgumentException if remainingTime is not positive
     */
    public TemporaryStatsModifier(final int remainingTime) {
        this(0, 0, 0, 0, remainingTime);
    }

    /**
     * Gets the remaining time for this temporary modifier.
     *
     * @return the remaining number of turns
     */
    public int getRemainingTime() {return this.remainingTime;}

    /**
     * Checks if this temporary modifier is currently active.
     *
     * @return true if the modifier is active
     */
    public boolean isActive() {return this.active;}

    /**
     * Reduces the remaining time for this modifier and deactivates it if time reaches 0.
     *
     * @param value the number of turns to reduce
     */
    public void shortenRemainingTime(final int value) {
        this.remainingTime -= value;
        if (this.remainingTime <= 0) {
            this.active = false;
            this.remainingTime = 0;
        }
    }
}
