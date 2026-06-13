package models.shared;

import com.google.gson.annotations.SerializedName;
import models.exceptions.WrongEffectTypeException;
import models.shared.statistics.Statistics;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Represents an effect applied when an object is used, or as secondary effect of an attack.
 *
 * <p>An effect can modify a Bugemon's stats during combat, either
 * temporarily (one turn) or for the whole fight. Effects can heal,
 * or modify stats such as attack, defense, or initiative.</p>
 *
 * <p>Effects are defined in the {@code objets.json} and {@code attaques.json} files, and are strongly typed.</p>
 *
 * @see Attack
 */
public class Effect {

    // Static lists for type validation
    // TODO: subject to expansion as suggested by the client
    /** Effect types that support "modifier" field. */
    public static final Collection<EffectType> TYPES_WITH_MODIFIER = List.of(EffectType.STAT_MODIFIER);
    /** Effect types that support "value" field. */
    public static final Collection<EffectType> TYPES_WITH_VALUE = List.of(EffectType.HEAL);

    private final EffectType type;
    @SerializedName("cible")
    private final TargetType target;
    private final Statistics.Stat stat;
    @SerializedName("duree")
    private final DurationType duration;

    // Modifier and value are optional because they are not applicable to all effect types, but at least one of them must be present depending on the type
    // Initialized to Optional.empty() for GSON parsing
    @SerializedName("modificateur")
    private Optional<Integer> modifier = Optional.empty();
    @SerializedName("valeur")
    private Optional<Integer> value = Optional.empty();

    /**
     * Constructs an Effect with the specified parameters.
     *
     * @param type     the type of effect
     * @param target   the target of the effect
     * @param stat     the affected statistic
     * @param duration the duration of the effect
     * @param modifier modifier for {@link Effect#TYPES_WITH_MODIFIER}
     * @param value    modifier for {@link Effect#TYPES_WITH_VALUE}
     */
    public Effect(final EffectType type, final TargetType target, final Statistics.Stat stat, final DurationType duration, final Optional<Integer> modifier, final Optional<Integer> value) {
        // Coherence checks between effect type and modifier/value presence
        if (Effect.TYPES_WITH_MODIFIER.contains(type) && value.isPresent()) {
            throw new IllegalArgumentException("Effects of type " + type + " should not have a heal value");
        } else if (Effect.TYPES_WITH_VALUE.contains(type) && modifier.isPresent()) {
            throw new IllegalArgumentException("Effects of type " + type + " should not have a modifier value");
        } else if (value.isEmpty() && modifier.isEmpty()) {
            String missing = "??";
            if (Effect.TYPES_WITH_MODIFIER.contains(type)) {
                missing = "modifier";
            } else if (Effect.TYPES_WITH_VALUE.contains(type)) {
                missing = "value";
            }
            throw new IllegalArgumentException("Effects of type " + type + " should have at least a " + missing);
        }

        this.type = type;
        this.target = target;
        this.stat = stat;
        this.modifier = modifier;
        this.duration = duration;
        this.value = value;
    }

    // TODO: Support for more complex effects may be needed, as the client suggested there could be.
    //  Thus, the enums and logic of this class are subjet to change.

    /**
     * Enumeration of available effect types.
     */
    public enum EffectType {
        /** Heal effect type. */
        @SerializedName("soin")
        HEAL,

        /** Stat modifier effect type. */
        @SerializedName("stat_modifier")
        STAT_MODIFIER,

        /** Reset malus effect type. */
        @SerializedName("reset_malus")
        RESET_MALUS
    }

    /**
     * Enumeration of possible effect targets.
     */
    public enum TargetType {
        /** User (attacking trainer) target. */
        @SerializedName("lanceur")
        USER,

        /** Opponent target. */
        @SerializedName("adversaire")
        OPPONENT,

        /** Team target. */
        @SerializedName("equipe")
        TEAM
    }

    /**
     * Enumeration of effect duration types.
     */
    public enum DurationType {
        /** Permanent duration throughout the entire battle. */
        @SerializedName("permanent")
        PERMANENT,

        /** Single turn duration. */
        @SerializedName("1_tour")
        SINGLE_TURN
    }

    // GETTERS

    /**
     * Gets the effect type.
     *
     * @return the effect type
     */
    public final EffectType getType() {
        return this.type;
    }

    /**
     * Gets the effect target.
     *
     * @return the target type
     */
    public final TargetType getTarget() {
        return this.target;
    }

    /**
     * Gets the affected statistic.
     *
     * @return the stat
     */
    public final Statistics.Stat getStat() {
        return this.stat;
    }

    /**
     * Gets the duration of this effect.
     *
     * @return the duration type
     */
    public final DurationType getDuration() {
        return this.duration;
    }

    /**
     * Gets the modifier for this effect.
     * This method should only be called for effect types that support modifiers (see {@link Effect#TYPES_WITH_MODIFIER}).
     * For other effect types, or if the modifier value is missing, an exception will be thrown.
     *
     * @return the modifier value
     * @throws WrongEffectTypeException if the effect type does not support modifiers
     * @throws IllegalStateException    if modifier value is missing for a modifier-type effect
     */
    public final int getModifier() throws WrongEffectTypeException {
        if (!Effect.TYPES_WITH_MODIFIER.contains(this.type)) {
            throw new WrongEffectTypeException("Modifier is only applicable for STAT_MODIFIER effects");
        }

        return this.modifier.orElseThrow(
                () ->  new IllegalStateException("Modifier value is missing for STAT_MODIFIER effect")
        );
    }

    /**
     * Gets the value for this effect.
     * This method should only be called for effect types that support values (see {@link Effect#TYPES_WITH_VALUE}).
     * For other effect types, or if the value is missing, an exception will be thrown
     *
     * @return the value
     * @throws WrongEffectTypeException if the effect type does not support values
     * @throws IllegalStateException    if value is missing for a value-type effect
     */
    public final int getValue() throws WrongEffectTypeException {
        if (!Effect.TYPES_WITH_VALUE.contains(this.type)) {
            throw new WrongEffectTypeException("Value is only applicable for HEAL effects");
        }

        return this.value.orElseThrow(
                () ->  new IllegalStateException("Heal value is missing for HEAL effect")
        );
    }
}


