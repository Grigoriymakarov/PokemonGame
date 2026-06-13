package models.exceptions;

/**
 * Exception thrown when an effect is used with an incorrect or incompatible type.
 *
 * <p>This exception is raised when the {@link services.combat.EffectResolver} or
 * {@link models.shared.Effect} encounters a type mismatch during effect application.</p>
 *
 * @see services.combat.EffectResolver
 * @see models.shared.Effect
 */
public class WrongEffectTypeException extends Exception {
    public WrongEffectTypeException() {
    }
    public WrongEffectTypeException(String message) {
        super(message);
    }
    public WrongEffectTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
