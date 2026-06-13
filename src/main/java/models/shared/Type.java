package models.shared;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Represents the elemental types of Bugemon and their attacks.
 * <p>
 * This enum defines the elemental strengths and weaknesses used in combat
 * damage calculations. It also supports GSON serialization for loading types from JSON.
 * </p>
 */
public enum Type {
    /** Flora type (Plant/Nature), strong against Aqua but weak against Litho. */
    @SerializedName("Flora") FLORA,
    /** Pyro type (Fire), strong against Litho but weak against Aqua. */
    @SerializedName("Pyro") PYRO,
    /** Aqua type (Water), strong against Pyro but weak against Flora. */
    @SerializedName("Aqua") AQUA,
    /** Litho type (Rock/Earth), strong against Flora but weak against Pyro. */
    @SerializedName("Litho") LITHO;

    /**
     * Map defining which type is vulnerable to which attacker.
     * Format: {Attacker -> Defender it is strong against}.
     */
    private static final Map<Type, Type> SUPER_EFFECTIVE_AGAINST = Map.of(
            Type.FLORA, Type.AQUA,
            Type.AQUA, Type.PYRO,
            Type.PYRO, Type.LITHO,
            Type.LITHO, Type.FLORA
    );

    /**
     * Map defining which type is resistant to which attacker.
     * Format: {Attacker -> Defender it is weak against}.
     */
    private static final Map<Type, Type> NOT_VERY_EFFECTIVE_AGAINST = Map.of(
            Type.FLORA, Type.LITHO,
            Type.AQUA, Type.FLORA,
            Type.PYRO, Type.AQUA,
            Type.LITHO, Type.PYRO
    );

    /**
     * Determines the effectiveness of this attacking type against a defending type.
     *
     * @param defenderType the elemental type of the target being attacked.
     * @return the {@link TypeEffectiveness} (SUPER_EFFECTIVE, NOT_VERY_EFFECTIVE, or NEUTRAL).
     */
    public TypeEffectiveness getEffectivenessAgainst(final Type defenderType) {
        if (defenderType == SUPER_EFFECTIVE_AGAINST.get(this)) {
            return TypeEffectiveness.SUPER_EFFECTIVE;
        }
        if (defenderType == NOT_VERY_EFFECTIVE_AGAINST.get(this)) {
            return TypeEffectiveness.NOT_VERY_EFFECTIVE;
        }
        return TypeEffectiveness.NEUTRAL;
    }
    /**
     * Returns a capitalized String representation of the type.
     *
     * @return the name of the type as a String (e.g., "Flora").
     */
    public String toString() {
        return switch (this) {
            case FLORA -> "Flora";
            case PYRO -> "Pyro";
            case AQUA -> "Aqua";
            case LITHO -> "Litho";
        };
    }
}

