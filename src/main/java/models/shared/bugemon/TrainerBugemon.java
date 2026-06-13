package models.shared.bugemon;

import dto.TrainerBugemonDTO;
import models.combat.CombatState;
import models.shared.Attack;
import models.shared.Type;
import models.shared.statistics.Statistics;
import models.shared.statistics.StatsModifier;
import models.shared.statistics.TemporaryStatsModifier;
import models.shared.xp.XP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents a Bugemon during a combat session.
 *
 * <p>While {@link BugemonSpecie} is an immutable template loaded from JSON,
 * {@code TrainerBugemon} holds the mutable state of a Bugemon in a team and in combat:
 * current XP, HP, alive status, temporary stat modifiers, temporary/persistent status effects, and potentially modified attack list.</p>
 *
 * @see BugemonSpecie
 * @see CombatState
 */
public class TrainerBugemon implements TrainerBugemonDTO {
    /** The immutable specie template for this Bugemon. */
    private BugemonSpecie specie;

    /** Optional nickname given by the trainer. Does not currently affect display name in combat. */
    private Optional<String> nickname;

    /** Current hit points. Starts at base HP and decreases during combat. */
    private int currentHP;

    /** Current attack list. May differ from base if attacks are swapped (Story 11). */
    private List<Attack> currentAttacks;

    /** The XP progression tracker, containing current level and experience points. */
    private XP xp;

    /** Whether this Bugemon is still able to fight. */
    private boolean isAlive;

    /**
     * The current level stats modifier held by this Bugemon.
     * Those stats modifiers are never reset.
     */
    private StatsModifier levelStatsModifier;

    /**
     * The current combat stats modifier held by this Bugemon.
     * Those stats modifiers are reset at the end of combat.
     */
    private StatsModifier combatStatsModifier;

    /**
     * The current temporary stats modifiers held by this Bugemon.
     * Those stats modifiers are reset after a pre-defined number of turns.
     */
    private List<TemporaryStatsModifier> temporaryStatsModifiers;

    /**
     * Builder class for constructing TrainerBugemon instances.
     * Provides a fluent interface for flexible object creation.
     */
    public static class Builder {
        private BugemonSpecie specie;
        private String nickname;
        private Integer currentHP; // Nullable to distinguish between set and unset
        private List<Attack> currentAttacks; // Nullable to distinguish between set and unset
        private XP xp = new XP();
        private boolean isAlive = true;
        private StatsModifier combatStatsModifier;
        private List<TemporaryStatsModifier> temporaryStatsModifiers;

        /**
         * Sets the specie of the Bugemon.
         * @param specie the BugemonSpecie to set (must not be null)
         * @return this Builder for method chaining
         */
        public Builder withSpecie(final BugemonSpecie specie) {
            this.specie = specie;
            return this;
        }

        /**
         * Sets the nickname for the Bugemon.
         *
         * @param nickname the nickname to set
         * @return this Builder for method chaining
         */
        public Builder withNickname(final String nickname) {
            this.nickname = nickname;
            return this;
        }

        /**
         * Sets the current HP.
         *
         * @param currentHP the current HP value
         * @return this Builder for method chaining
         */
        public Builder withCurrentHP(final int currentHP) {
            this.currentHP = currentHP;
            return this;
        }

        /**
         * Sets the current attack list.
         *
         * @param currentAttacks the list of attacks
         * @return this Builder for method chaining
         */
        public Builder withCurrentAttacks(final List<Attack> currentAttacks) {
            this.currentAttacks = new ArrayList<>(currentAttacks);
            return this;
        }

        /**
         * Sets the XP progression for the Bugemon.
         *
         * @param xp the XP tracker containing the level and experience points
         * @return this Builder for method chaining
         */
        public Builder withXPProgression(final XP xp) {
            this.xp = new XP(xp);
            return this;
        }

        /**
         * Sets the alive status.
         *
         * @param isAlive true if the Bugemon is alive
         * @return this Builder for method chaining
         */
        public Builder withAlive(final boolean isAlive) {
            this.isAlive = isAlive;
            return this;
        }

        /**
         * Sets the permanent stats' modifier.
         *
         * @param combatStatsModifier the permanent stats modifier
         * @return this Builder for method chaining
         */
        public Builder withCombatStatsModifier(final StatsModifier combatStatsModifier) {
            this.combatStatsModifier = combatStatsModifier;
            return this;
        }

        /**
         * Sets the list of temporary stats modifiers.
         *
         * @param temporaryStatsModifiers the list of temporary stats modifiers
         * @return this Builder for method chaining
         */
        public Builder withTemporaryStatsModifiers(final List<TemporaryStatsModifier> temporaryStatsModifiers) {
            this.temporaryStatsModifiers = new ArrayList<>(temporaryStatsModifiers);
            return this;
        }

        /**
         * Validates that combat and temporary stat modifiers do not contain HP modifications.
         *
         * @throws AmbiguousHPModifierException if any modifier has a non-zero HP value
         */
        private void validateHPModifiers() {
            if (this.combatStatsModifier != null && this.combatStatsModifier.get(Statistics.Stat.HP) != 0) {
                throw new AmbiguousHPModifierException(
                    "Combat stat modifier contains HP modifications."
                );
            }

            if (this.temporaryStatsModifiers != null) {
                for (final TemporaryStatsModifier modifier : this.temporaryStatsModifiers) {
                    if (modifier.get(Statistics.Stat.HP) != 0) {
                        throw new AmbiguousHPModifierException(
                            "Temporary stat modifier contains HP modifications."
                        );
                    }
                }
            }
        }

        /**
         * Builds and returns a new TrainerBugemon instance.
         *
         * @return a new TrainerBugemon configured with the builder's values
         * @throws AmbiguousHPModifierException if combat or temporary stat modifiers contain HP modifications
         */
        public TrainerBugemon build() {
            validateHPModifiers();

            final TrainerBugemon bugemon = new TrainerBugemon();
            bugemon.specie = this.specie;
            bugemon.nickname = Optional.ofNullable(this.nickname);
            bugemon.currentAttacks = (this.currentAttacks != null)
                    ? new ArrayList<>(this.currentAttacks)
                    : new ArrayList<>(this.specie.getPossibleAttacks());
            bugemon.xp=this.xp;
            bugemon.isAlive = this.isAlive;
            bugemon.levelStatsModifier = new StatsModifier();
            bugemon.combatStatsModifier = (this.combatStatsModifier != null) ? this.combatStatsModifier : new StatsModifier();
            bugemon.temporaryStatsModifiers = (this.temporaryStatsModifiers != null)
                    ? new ArrayList<>(this.temporaryStatsModifiers)
                    : new ArrayList<>();

            final int maxHP =  this.specie.getStat(Statistics.Stat.HP);
            bugemon.currentHP = (this.currentHP != null) ? this.currentHP : maxHP;

            return bugemon;
        }
    }

    //Getters
    /** @return current hit points */
    public int getCurrentHP(){
        return this.currentHP;
    }

    public int getLevel(){
        return this.xp.getLevel();
    }

    public int getXP(){
        return this.xp.getCurrentXP();
    }

    /** @return XP required to reach the next level from current level */
    public int getXPToNextLevel() {
        return this.xp.getXPToNextLevel();
    }

    /** @return the name from the base template */
    public String getName(){
        return this.specie.getName();
    }

    /**
     * Gets the display name of the Bugemon (nickname if set, otherwise the specie name).
     *
     * @return the display name
     */
    public String getDisplayName(){
        return this.nickname.orElse(this.specie.getName());
    }

    /** @return the current list of available attacks */
    public List<Attack> getCurrentAttacks() {
        return Collections.unmodifiableList(this.currentAttacks);
    }

    /** @return the immutable base Bugemon template */
    public BugemonSpecie getSpecie() {
        return this.specie;
    }

    /** @return true if this Bugemon can still fight */
    public boolean isAlive() {
        return this.isAlive;
    }

    /** @return maximum hit points of the Bugemon */
    public int getMaxHp() {
        return this.getStatistics().getHp() ;
    }

    /** @return the sprite file name from the base template */
    public String getSprite() {
        return this.specie.getSprite();
    }

    /**
     * Gets the computed statistics including permanent and temporary stat modifiers.
     *
     * @return the calculated {@link Statistics}
     */
    public Statistics getStatistics() {
        final Statistics stats = new Statistics(this.specie.getBaseStats());

        stats.add(this.levelStatsModifier);
        stats.add(this.combatStatsModifier);
        for (final TemporaryStatsModifier modifier : this.temporaryStatsModifiers) {
            stats.add(modifier);
        }

        return stats;
    }

    /** Returns the value of a specific stat, including all modifiers.
     *
     * @param stat the {@link Statistics.Stat} to retrieve
     * @return the calculated value of the specified stat
     */
    public int getStat(Statistics.Stat stat) {
        return this.getStatistics().get(stat);
    }


    /**
     * Checks if the specified attack is currently available to this Bugemon.
     * @param attack the attack to check
     * @return true if the attack is available, false otherwise
     */
    public boolean hasAttack(Attack attack) {
        return this.currentAttacks.contains(attack);
    }


    //Setters

    /** @param alive false to mark this Bugemon as fainted */
    public void setAlive(final boolean alive) {
        this.isAlive = alive;
        this.currentHP = (this.isAlive) ? this.getStatistics().getHp() : 0;
    }

    // Modifiers
    /**
     * Applies damage to this Bugemon, reducing current HP and potentially marking it as fainted.
     *
     * @param damage the amount of damage to apply
     */
    public void applyDamage(final int damage) {
        int effectiveDamage = damage;
        if (effectiveDamage >= this.currentHP) {
            effectiveDamage = this.currentHP;
        }
        this.currentHP -= effectiveDamage;
        if (this.currentHP <= 0) {
            this.isAlive = false;
        }
    }

    /**
     * Heals this Bugemon, increasing current HP up to the maximum HP from the base template.
     *
     * @param healAmount the amount of HP to restore
     */
    public void heal(final int healAmount) {
        this.currentHP += healAmount;
        final int maxHp = this.getMaxHp();
        if (this.currentHP > maxHp) {
            this.currentHP = maxHp;
        }
    }

    /**
     * Resets all negative stat modifiers on this Bugemon, both temporary and permanent.
     */
    public void resetMaluses() {
        this.combatStatsModifier.resetMaluses();
    }

    /**
     * Resets this Bugemon to its initial state for combat: restores max HP, marks as alive, and clears all modifiers.
     */
    public void resetForCombat() {
        this.currentHP = this.getMaxHp();
        this.isAlive = true;
        this.combatStatsModifier = new StatsModifier();
        this.resetTemporaryModifiers();
    }

    /**
     * Increases a specific stat for the duration of a combat, using a combat {@link StatsModifier}.
     *
     * @param stat the {@link Statistics.Stat} to increase
     * @param modifier the amount to increase
     */
    public void increaseCombatStat(final Statistics.Stat stat, final int modifier) {
        this.combatStatsModifier.increaseStat(stat, modifier);
    }

    /**
     * Adds statistics permanently to the Bugemon's level {@link StatsModifier}.
     *
     * @param stat the {@link Statistics.Stat} to add
     */
    public void addLevelStats(final Statistics stat) {
        this.levelStatsModifier.add(stat);
    }

    /**
     * Adds a temporary stat modifier to this Bugemon.
     *
     * @param modifier the {@link TemporaryStatsModifier} to apply
     */
    public void addTemporaryModifier(final TemporaryStatsModifier modifier) {
        this.temporaryStatsModifiers.add(modifier);
    }

    /**
     * Processes one turn of time for temporary modifiers, reducing their remaining duration and removing expired ones.
     */
    public void registerTimeTick() {
        for (final TemporaryStatsModifier modifier : this.temporaryStatsModifiers) {
            modifier.shortenRemainingTime(1);
        }
        this.temporaryStatsModifiers.removeIf(m -> !m.isActive());
    }

    /**
     * Resets all temporary stat modifiers for this Bugemon.
     *
     * <p>This clears the list of temporary modifiers, removing any active buffs or debuffs
     * that have been applied. This is typically called when a Bugemon is switched out or combat ends.</p>
     */
    public void resetTemporaryModifiers() {
        this.temporaryStatsModifiers = new ArrayList<>();
    }


    /**
     * Resets this Bugemon to its initial state: level 1, 0 XP, no level bonuses, full HP.
     * Used when restarting a tower run from the beginning.
     */
    public void resetProgression() {
        this.xp = new XP();
        this.levelStatsModifier = new StatsModifier();
        this.resetForCombat();
    }

    /**
     * Adds experience points to this Bugemon and processes any resulting level-ups.
     *
     * <p>When this Bugemon levels up after gaining XP, its current HP is restored to maximum.</p>
     *
     * @param amount the experience points to add
     */
    public void gainXP(final int amount) {
        final int levelBefore = this.getLevel();
        this.xp.addXP(amount);
        final int levelAfter = this.getLevel();

        if (levelAfter > levelBefore) {
            this.currentHP = this.getMaxHp();
        }
    }

    /**
     * Returns the elemental type of this Bugemon's specie.
     *
     * @return the type of this Bugemon
     */
    public Type getSpecieType() {
        return this.getSpecie().getType();
    }

    /**
     * Checks if this Bugemon's specie matches the given specie.
     * @param specie the specie to compare with
     * @return true if the species match, false otherwise
     */
    public boolean isSpecie(BugemonSpecie specie) {
        return this.specie.equals(specie);
    }
}

