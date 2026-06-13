package models.shared.statistics;

import com.google.gson.annotations.SerializedName;
import dto.StatisticsDTO;
import models.shared.bugemon.TrainerBugemon;

/**
 * Represents the combat statistics of a Bugemon.
 *
 * <p>The statistics define a Bugemon's combat capabilities:</p>
 *
 * <p>This class is immutable after construction. To modify stats during battle,
 * use the modifiers in {@link TrainerBugemon}.</p>
 *
 * @see TrainerBugemon
 */
public class Statistics implements StatisticsDTO {

    /**
     * Enumeration of all statistics.
     */
    public enum Stat {
        @SerializedName("pv") HP,
        @SerializedName("attaque") ATTACK,
        @SerializedName("defense") DEFENSE,
        @SerializedName("initiative") INITIATIVE
    }

    /** Maximum health points of the Bugemon. */
    @SerializedName("pv")
    protected int hp;

    /** Attack statistic, increases the damage dealt. */
    @SerializedName("attaque")
    protected int attack;

    /** Defense statistic, reduces the damage received. */
    protected int defense;

    /** Initiative, determines the attack order in a turn. */
    protected int initiative;

    /**
     * Creates a set of statistics with the given values.
     *
     * @param hp     health points (must be > 0)
     * @param attack attack value (>= 0)
     * @param def    defense value (>= 0)
     * @param init   initiative value (>= 0)
     */
    public Statistics(final int hp, final int attack, final int def, final int init) {
        this.hp = hp;
        this.attack = attack;
        this.defense = def;
        this.initiative = init;
    }

    /**
     * Copy constructor. Creates a new instance with the same values.
     *
     * <p>Used by {@link services.BugemonService#createTrainerBugemon(String)}
     * to ensure that each battle instance has its own statistics
     * independent of the original template.</p>
     *
     * @param other the statistics to copy (must not be {@code null})
     */
    public Statistics(final Statistics other) {
        this.hp = other.hp;
        this.attack = other.attack;
        this.defense = other.defense;
        this.initiative = other.initiative;
    }

    /**
     * Returns the maximum health points.
     *
     * @return the maximum HP
     */
    public int getHp() { return this.hp; }

    /**
     * Returns the attack statistic.
     *
     * @return the attack value
     */
    public int getAttack() { return this.attack; }

    /**
     * Returns the defense statistic.
     *
     * @return the defense value
     */
    public int getDefense() { return this.defense; }

    /**
     * Returns the initiative statistic.
     *
     * @return the initiative value
     */
    public int getInitiative() { return this.initiative; }

    /**
     * Returns the value of the specified statistic.
     *
     * @param stat the statistic to read
     * @return the value of the requested statistic
     */
    public int get(final Stat stat) {
        return switch (stat) {
            case HP -> this.hp;
            case ATTACK -> this.attack;
            case DEFENSE -> this.defense;
            case INITIATIVE -> this.initiative;
        };
    }


    /**
     * Increases the value of the specified statistic
     * @param stat  the statistic to modify
     * @param value the amount to increase (must be > 0)
     */
    public void increaseStat(final Stat stat, final int value) {
        switch (stat) {
            case HP -> this.hp += value;
            case ATTACK -> this.attack += value;
            case DEFENSE -> this.defense += value;
            case INITIATIVE -> this.initiative += value;
        }
    }

    /**
     * Adds the stats of another Statistics object to itself
     * @param stats the Statistics to be added
     */
    public void add(final Statistics stats) {
        this.hp +=  stats.hp;
        this.attack += stats.attack;
        this.defense += stats.defense;
        this.initiative += stats.initiative;
    }
}
