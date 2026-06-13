package services.combat;

import services.combat.actions.Action;
import services.combat.actions.ActionContext;

/**
 * Immutable data carrier holding all necessary components to resolve a single combat turn.
 * <p>
 * This record bundles the actions chosen by both participants and their respective
 * execution contexts. It ensures that the {@link TurnEngine} has a consistent snapshot
 * of the turn's intent before processing.
 * </p>
 * @param playerAction        The action selected by the player.
 * @param playerActionContext The contextual information (trainers, state) for the player's action.
 * @param enemyAction         The action selected by the enemy AI.
 * @param enemyActionContext  The contextual information (trainers, state) for the enemy's action.
 */
public record TurnData(
        Action playerAction, ActionContext playerActionContext,
        Action enemyAction, ActionContext enemyActionContext) {

    public TurnData {
    if (playerActionContext.combatState() != enemyActionContext.combatState()) {
        throw new IllegalStateException("Player and enemy contexts must share the same combat state");
    }
}
}
