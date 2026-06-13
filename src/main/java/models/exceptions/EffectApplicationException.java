package models.exceptions;

/**
 * Exception thrown when an effect fails to be applied during combat.
 *
 * <p>This exception indicates an error during the application of a combat effect
 * (such as healing, stat modifications, or status changes) by the {@link services.combat.EffectResolver}.</p>
 *
 * @see services.combat.EffectResolver
 * @see models.shared.Effect
 */
public class EffectApplicationException extends Exception {
    public EffectApplicationException(String message) {
        super(message);
    }
    public EffectApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
