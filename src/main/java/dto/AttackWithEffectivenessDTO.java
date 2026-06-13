package dto;

import models.shared.Attack;
import models.shared.Type;
import models.shared.TypeEffectiveness;

/**
 * Data Transfer Object (DTO) representing an attack with its computed effectiveness.
 *
 * <p>This class encapsulates the essential information needed to display an attack in the combat UI,
 * including the attack's name, type, and its effectiveness against a specific target.
 * It is used to pass attack data from the business logic layer to the presentation layer.</p>
 *
 * <p>The effectiveness is computed at the time of creation based on the target's type,
 * allowing the UI to display visual cues to inform the player about the attack's advantage or disadvantage.</p>
 *
 * @see Attack
 * @see Type
 * @see TypeEffectiveness
 */
public class AttackWithEffectivenessDTO {
    /** Display name of the attack. */
    private final String name;

    /** Elemental type of the attack. */
    private final Type type;

    /** Effectiveness of the attack against the current target. */
    private final TypeEffectiveness effectiveness;

    /**
     * Constructs an AttackWithEffectivenessDTO from an Attack and its computed effectiveness.
     *
     * @param attack the attack containing name and type information
     * @param typeEffectiveness the pre-computed effectiveness of this attack against the target
     * @throws NullPointerException if attack or typeEffectiveness is null
     */
    public AttackWithEffectivenessDTO(final Attack attack, final TypeEffectiveness typeEffectiveness) {
        this.name = attack.getName();
        this.type = attack.getType();
        this.effectiveness = typeEffectiveness;
    }

    /**
     * Returns the display name of the attack.
     *
     * @return the attack's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the elemental type of the attack.
     *
     * @return the attack's type (FLORA, AQUA, PYRO, or LITHO)
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Returns the effectiveness of this attack against the current target.
     *
     * <p>The effectiveness indicates how well this attack performs against the target's type:</p>
     * <ul>
     *   <li>{@link TypeEffectiveness#SUPER_EFFECTIVE} - Attack deals 1.5x damage</li>
     *   <li>{@link TypeEffectiveness#NEUTRAL} - Attack deals 1.0x damage (no modifier)</li>
     *   <li>{@link TypeEffectiveness#NOT_VERY_EFFECTIVE} - Attack deals 0.75x damage</li>
     * </ul>
     *
     * @return the effectiveness value
     */
    public TypeEffectiveness getEffectiveness() {
        return this.effectiveness;
    }
}
