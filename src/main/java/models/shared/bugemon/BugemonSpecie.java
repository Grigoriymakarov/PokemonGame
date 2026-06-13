package models.shared.bugemon;

import com.google.gson.annotations.SerializedName;
import models.shared.Attack;
import models.shared.Identifiable;
import models.shared.Type;
import models.shared.statistics.Statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@code Bugemon} stores its name, type, base statistics, and the list
 * of attacks it can use. It acts as a template from which combat instances
 * are created with {@link TrainerBugemon}.
 *
 * <p>Bugemons are loaded from {@code bugemons.json} by
 * {@link repositories.BugemonRepository}.</p>
 *
 * @see TrainerBugemon
 * @see repositories.BugemonRepository
 * @see Statistics
 * @see Attack
 */
public final class BugemonSpecie implements Identifiable {

    /** Unique identifier of the Bugemon, used as a key. */
    private String id;

    /** Display name of the Bugemon. */
    @SerializedName("nom")
    private String name;

    /** Elemental type of the Bugemon ({@link Type}). */
    private Type type;

    /** Base statistics of the Bugemon (HP, attack, defense, initiative). */
    @SerializedName("stats")
    private Statistics baseStats;

    /** Path to the Bugemon sprite image. */
    private String sprite;

    /** Indicates whether this Bugemon is a starter Bugemon. */
    private boolean starter;

    /** Attacks available to this BugemonSpecie */
    @SerializedName("attaques")
    private List<Attack> attacks;

    private BugemonSpecie(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.baseStats = builder.baseStats;
        this.sprite = builder.sprite;
        this.starter = builder.starter;
        this.attacks = builder.attacks != null ? new ArrayList<>(builder.attacks) : new ArrayList<>();
    }
    /**
     * Required for Gson deserialization.
     */
    public BugemonSpecie() {}

    public static class Builder {
        private String id;
        private String name;
        private Type type;
        private Statistics baseStats;
        private String sprite;
        private boolean starter;
        private List<Attack> attacks;

        /**
         * Sets the unique identifier for the Bugemon.
         *
         * @param id the unique identifier
         * @return this Builder for method chaining
         */
        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the display name for the Bugemon.
         *
         * @param name the display name
         * @return this Builder for method chaining
         */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the elemental type for the Bugemon.
         *
         * @param type the elemental type
         * @return this Builder for method chaining
         */
        public Builder type(final Type type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the base statistics for the Bugemon.
         *
         * @param baseStats the statistics object
         * @return this Builder for method chaining
         */
        public Builder baseStats(final Statistics baseStats) {
            this.baseStats = baseStats;
            return this;
        }

        /**
         * Sets the sprite file name for the Bugemon.
         *
         * @param sprite the sprite resource path or file name
         * @return this Builder for method chaining
         */
        public Builder sprite(final String sprite) {
            this.sprite = sprite;
            return this;
        }

        /**
         * Sets whether this Bugemon is a starter.
         *
         * @param starter true if this Bugemon is available at game start
         * @return this Builder for method chaining
         */
        public Builder starter(final boolean starter) {
            this.starter = starter;
            return this;
        }

        /**
         * Sets the list of attacks available to this Bugemon.
         *
         * @param attacks the list of possible attacks
         * @return this Builder for method chaining
         */
        public Builder attacks(final List<Attack> attacks) {
            this.attacks = attacks != null ? new ArrayList<>(attacks) : new ArrayList<>();
            return this;
        }

        public BugemonSpecie build() {
            if (this.id == null)       throw new NullPointerException("id is required");
            if (this.name == null)     throw new NullPointerException("name is required");
            if (this.type == null)     throw new NullPointerException("type is required");
            if (this.baseStats == null) throw new NullPointerException("baseStats is required");
            return new BugemonSpecie(this);
        }
    }

    //GETTERS
    /**
     * Returns the unique identifier of this Bugemon.
     *
     * @return the identifier
     */
    @Override
    public String id() { return this.id; }

    /**
     * Returns the display name of the Bugemon.
     *
     * @return the Bugemon name
     */
    public String getName() { return this.name; }

    /**
     * Returns the elemental type of the Bugemon.
     *
     *  @return the Bugemon's {@link Type}
     */
    public Type getType() { return this.type; }

    /**
     * Returns the base statistics of the Bugemon.
     *
     * @return the statistics object (HP, attack, defense, initiative)
     */
    public Statistics getBaseStats() { return this.baseStats; }

    /**
     * Indicates whether this Bugemon is a starter.
     *
     * @return {@code true} if the Bugemon is available at the beginning of the game
     */
    public boolean getStarter() { return this.starter; }

    /**
     * Returns the list of attacks available to this Bugemon specie.
     *
     * @return the list of {@link Attack} objects
     */
    public List<Attack> getPossibleAttacks() {
        return Collections.unmodifiableList(this.attacks);
    }
    /**
     * Returns the sprite file used to display this Bugemon.
     *
     * @return the image file name
     */
    public String getSprite() { return this.sprite; }

    /**
     * Returns the value of a specific statistic for this Bugemon.
     *
     * @param stat the statistic to retrieve (HP, attack, defense, initiative)
     * @return the value of the requested statistic
     */
    public int getStat(final Statistics.Stat stat) {
        return this.baseStats.get(stat);
    }
}



