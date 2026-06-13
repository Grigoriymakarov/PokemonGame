package models.exceptions;

/**
 * Exception thrown when a trainer cannot determine which action to perform during combat.
 *
 * <p>This exception is typically raised when either the player has not yet chosen an action
 * or the enemy's action strategy cannot determine an appropriate action based on the current
 * combat state.</p>
 */
public class CouldNotDetermineActionException extends Exception {
    /**
     * Creates an exception with the given message.
     *
     * @param message the error message describing why the action could not be determined
     */
    public CouldNotDetermineActionException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the given message and cause.
     *
     * @param message the error message describing why the action could not be determined
     * @param cause the underlying exception that caused this error
     */
    public CouldNotDetermineActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
