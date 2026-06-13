package models.exceptions;

/**
 * Exception thrown when attempting to perform an action that requires an active Bugemon, but none exists.
 *
 * <p>This runtime exception is raised when a trainer has no active Bugemon during combat,
 * which typically indicates a game state inconsistency.</p>
 *
 * @see models.shared.trainer.Trainer
 * @see services.combat.CombatService
 */
public class NoActiveBugemonException extends RuntimeException {
    public NoActiveBugemonException(String message) {
        super(message);
    }
    public NoActiveBugemonException(String message, Throwable cause) {
        super(message, cause);
    }
}
