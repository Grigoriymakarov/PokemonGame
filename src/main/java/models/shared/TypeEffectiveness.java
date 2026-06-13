package models.shared;

import constants.LogicConstants;

/**
 * Enumeration representing the damage effectiveness of an elemental type against another.
 * <p>
 * Each effectiveness level is associated with a numerical multiplier used
 * in combat damage calculations to increase or decrease the final damage output.
 * </p>
 */
public enum TypeEffectiveness {
    /** High effectiveness: damage is increased by 50% (multiplier: 1.5). */
    SUPER_EFFECTIVE(LogicConstants.SUPER_EFFECTIVE_MULTIPLIER),
    /** Standard effectiveness: damage is applied normally (multiplier: 1.0). */
    NEUTRAL(LogicConstants.NEUTRAL_MULTIPLIER),
    /** Low effectiveness: damage is reduced by 25% (multiplier: 0.75). */
    NOT_VERY_EFFECTIVE(LogicConstants.NOT_VERY_EFFECTIVE_MULTIPLIER);

    /** The numerical value to multiply the base damage by. */
    private final float multiplier;

    /**
     * Internal constructor for effectiveness levels.
     * @param multiplier the damage multiplier associated with this state
     */
    TypeEffectiveness(final float multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Gets the numerical multiplier of this effectiveness.
     *
     * @return the double value of the multiplier (e.g., 1.5 for SUPER_EFFECTIVE)
     */
    public float getMultiplier() {
        return this.multiplier;
    }
}

