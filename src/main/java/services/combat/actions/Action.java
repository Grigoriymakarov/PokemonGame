package services.combat.actions;

import models.exceptions.ActionExecutionException;

/**
 * Interface representing an action that can be performed during combat.
 *
 * <p>This interface serves as the foundation for different types of combat actions
 * such as attacks, forfeits, switching, and object usage. Subclasses must implement
 * specific action types with their own behavior and attributes.</p>
 *
 * @see AttackAction
 * @see ForfeitAction
 */
public interface Action {
    /**
     * Returns the technical identifier of this action type.
     *
     * @return the action identifier
     */
    Identifier identifier();

    /**
     * Executes the specific logic associated with this combat action.
     *
     * @param context the current state and environment of the combat
     * @throws ActionExecutionException if the action fails to execute properly
     */
    void execute(ActionContext context) throws ActionExecutionException;

    /**
     * Closed set of combat action types.
     */
    enum Identifier {
        ATTACK,
        USE_ITEM,
        SWITCH,
        FORFEIT
    }
}
