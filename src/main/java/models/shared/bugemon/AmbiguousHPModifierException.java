package models.shared.bugemon;

/**
 * Exception thrown when attempting to build a TrainerBugemon with HP modifications
 * in combat or temporary stat modifiers.
 *
 * <p>HP modifications in these contexts create ambiguous behavior:</p>
 * <ul>
 *     <li>If a Bugemon is at full HP and gains an HP buff, its current HP doesn't change</li>
 *     <li>However, if it heals afterward, its total HP can increase to the new maximum</li>
 * </ul>
 * <p>This ambiguity makes it impossible to determine the correct initial HP value when building.</p>
 *
 * <p><strong>Valid HP modifications</strong> should only be applied via:</p>
 * <ul>
 *     <li>Base stats (from the specie template)</li>
 *     <li>Level stat modifiers (permanent)</li>
 * </ul>
 */
public class AmbiguousHPModifierException extends IllegalArgumentException {

    private static final String DEFAULT_MESSAGE =
            "HP modifications in combat or temporary stat modifiers create ambiguous behavior during initialization. " +
            "HP buffs do not affect current HP when the Bugemon is at full health, but can increase max HP after healing. " +
            "Valid HP modifications should only come from base stats or level modifiers.";

    /**
     * Constructs an AmbiguousHPModifierException with the default detail message.
     */
    public AmbiguousHPModifierException() {
        super(AmbiguousHPModifierException.DEFAULT_MESSAGE);
    }

    /**
     * Constructs an AmbiguousHPModifierException with a custom detail message.
     *
     * @param message the detail message
     */
    public AmbiguousHPModifierException(final String message) {
        super(message);
    }

    /**
     * Constructs an AmbiguousHPModifierException with a custom detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public AmbiguousHPModifierException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

