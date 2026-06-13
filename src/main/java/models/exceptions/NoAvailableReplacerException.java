package models.exceptions;

import services.combat.strategies.ReplacementStrategy;

/**
 * Exception thrown when no available Bugemon can be selected to replace the current active one.
 *
 * <p>This exception is raised during combat when a team has no alive Bugemon remaining
 * to use as a replacement, either due to a required forced switch or a team wipe.</p>
 *
 * @see ReplacementStrategy
 * @see services.combat.KOResolver
 */
public class NoAvailableReplacerException extends Exception {
    public NoAvailableReplacerException() {
        super("No available replacer found");
    }
    public NoAvailableReplacerException(String message) {
        super(message);
    }
    public NoAvailableReplacerException(String message, Throwable cause) {
        super(message, cause);
    }
}
