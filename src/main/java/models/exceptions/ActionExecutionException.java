package models.exceptions;

/**
 * Exception thrown when a combat action fails to execute properly.
 *
 * <p>This checked exception indicates that an action (attack, item use, switch, etc.)
 * could not be completed due to unexpected conditions during execution. It wraps
 * more specific exceptions to provide a consistent error handling mechanism at the
 * service layer.</p>
 *
 * @see EffectApplicationException
 * @see InvalidAttackException
 */
public class ActionExecutionException extends Exception {
    
    /**
     * Creates an action execution exception with the specified message and cause.
     *
     * @param message the detail message explaining what went wrong
     * @param cause the underlying exception that caused this failure
     */
    public ActionExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates an action execution exception with the specified message.
     *
     * @param message the detail message explaining what went wrong
     */
    public ActionExecutionException(final String message) {
        super(message);
    }
}