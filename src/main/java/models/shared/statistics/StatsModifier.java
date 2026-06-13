package models.shared.statistics;

/**
 * Represents a modifier applied to combat statistics.
 *
 * <p>This class extends {@link Statistics} to represent stat changes that can be applied
 * or removed during combat. Modifiers can be positive (buffs) or negative (debuffs),
 * affecting stats such as HP, attack, defense, and initiative.</p>
 *
 * <p>Stats modifiers are used to track temporary combat effects that modify a Bugemon's
 * performance during battle. The modifier values are typically added to or subtracted from
 * the base statistics to calculate final combat values.</p>
 *
 * @see Statistics
 * @see TemporaryStatsModifier
 */
public class StatsModifier extends Statistics {
    /**
     * Constructs a StatsModifier with specified stat modifier values.
     *
     * @param hp the HP modifier value
     * @param attack the attack modifier value
     * @param def the defense modifier value
     * @param init the initiative modifier value
     */
    public StatsModifier(final int hp, final int attack, final int def, final int init) {
        super(hp, attack, def, init);
    }

    /**
     * Constructs a StatsModifier with all values initialized to 0.
     */
    public StatsModifier() {
        super(0, 0, 0, 0);
    }

    /**
     * Resets all negative stat modifiers to 0, removing all debuffs.
     */
    public void resetMaluses() {
        if (0 > this.hp) this.hp = 0;
        if (0 > this.attack) this.attack = 0;
        if (0 > this.defense) this.defense = 0;
        if (0 > this.initiative) this.initiative = 0;
    }
}
