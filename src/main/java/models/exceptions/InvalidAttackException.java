package models.exceptions;


/**
 * Exception thrown when attempting to use an invalid attack.
 *
 * <p>Error cases include:</p>
 * <ul>
 * <li>The attack is null</li>
 * <li>The attack is not in the available arsenal of the Bugemon</li>
 * </ul>
 *
 * @see services.combat.CombatService
 */
public class InvalidAttackException extends RuntimeException {
    /**
     * Constructor with error message
     *
     * @param message Detailed description of the attack error
     */
    public InvalidAttackException(final String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     *
     * @param message Detailed description of the attack error
     * @param cause   The original cause of the exception
     */
    public InvalidAttackException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
