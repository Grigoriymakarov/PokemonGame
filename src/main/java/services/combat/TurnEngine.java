package services.combat;

import models.exceptions.ActionExecutionException;
import models.combat.CombatState;
import services.combat.actions.Action;
import services.combat.actions.ActionContext;
import models.shared.bugemon.TrainerBugemon;

/**
 * Stateless coordinator responsible for resolving one combat turn.
 *
 * <p>{@code TurnEngine} does not decide which actions each side should choose.
 * Instead, it receives a fully prepared {@link TurnData} object containing both actions
 * and their associated {@link ActionContext}. Its responsibility is to enforce turn order,
 * skip an action when the acting side can no longer act, and apply the end-of-turn tick
 * when the combat is still ongoing.</p>
 *
 * <p>The current implementation always gives priority to the player side through
 * {@link #playerActsFirst(TurnData)}. This keeps the sequencing logic isolated in one place,
 * which makes future priority rules easier to introduce.</p>
 *
 * @see TurnData
 * @see Action
 * @see ActionContext
 * @see CombatState
 */
public class TurnEngine {

    /**
     * Creates a new turn engine.
     *
     * <p>The class is stateless, so instances are interchangeable.</p>
     */
    TurnEngine() {
    }

    /**
     * Processes a single turn using the provided turn data.
     *
     * <p>If the combat is already over, the state is returned unchanged. Otherwise,
     * the engine resolves the two actions in initiative order, re-checks whether the
     * second side is still allowed to act, then applies end-of-turn effects if the
     * encounter is not finished.</p>
     *
     * @param turnData the data required to resolve the current turn
     * @return the updated combat state
     * @throws ActionExecutionException if any action fails to execute properly
     */
    public CombatState processTurn(final TurnData turnData) throws ActionExecutionException {
        final CombatState state = turnData.playerActionContext().combatState();

        if (state.isCombatOver()) {
            return state;
        }

        resolveActionsWithTurdData(turnData);
        applyEndOfTurn(state);
        return state;
    }

    /**
     * Resolves actions of player and enemy in order with respect to thers initiatives which is computed then in the playerActsFirst(turnData) method.
     *
     * <p>Before the second action is executed, the engine checks whether the acting side
     * is still allowed to act. This prevents a knocked-out Bugemon from acting later
     * in the same turn.</p>
     * @param turnData the container holding both actions and contexts
     * @throws ActionExecutionException if any action fails to execute properly
     */
    private void resolveActionsWithTurdData(final TurnData turnData) throws ActionExecutionException {
        final CombatState state = turnData.playerActionContext().combatState();
        final ActionContext playerActionContext = turnData.playerActionContext();
        final ActionContext enemyActionContext = turnData.enemyActionContext();
        final Action playerAction = turnData.playerAction();
        final Action enemyAction = turnData.enemyAction();

        if (playerActsFirst(turnData)) {
            // We need to get the target Bugemon before player action, in case the enemy is switched out
            final TrainerBugemon enemyActiveBugemonBeforePlayerAction = state.getEnemyActiveBugemon();
            resolveAction(playerAction, playerActionContext);
            // Check if the target has not fainted before applying action
            if (!state.isCombatOver() && enemyActiveBugemonBeforePlayerAction.isAlive()) {
                resolveAction(enemyAction, enemyActionContext);
            }
            return;
        }

        final TrainerBugemon playerBeforeEnemyAction = state.getPlayerActiveBugemon();
        resolveAction(enemyAction, enemyActionContext);
        if (!state.isCombatOver() && playerBeforeEnemyAction.isAlive()) {
            resolveAction(playerAction, playerActionContext);
        }


    }


    /**
     * Executes a single action in its associated context.
     *
     * @param action the action to execute
     * @param context the combat context visible to the action
     * @throws ActionExecutionException if the action fails to execute properly
     */
    private void resolveAction(final Action action, final ActionContext context) throws ActionExecutionException {
        action.execute(context);
    }

    /**
     * Indicates whether the player side should act before the enemy side.
     *
     * <p>The current implementation always returns {@code true}, but the method isolates
     * the initiative rule so it can later depend on speed, priority, or status effects.</p>
     *
     * @return {@code true} when the player acts first
     */
    private boolean playerActsFirst(final TurnData turnData) {
        final int playerInitiative = turnData.playerActionContext().getActorInitiative();
        final int enemyInitiative  = turnData.enemyActionContext().getActorInitiative();

        // In case of a tie: random order
        if (playerInitiative == enemyInitiative) {
            return Math.random() < 0.5;
        }

        return playerInitiative > enemyInitiative;
    }


    /**
     * Applies end-of-turn updates if the combat is still ongoing.
     *
     * @param state the combat state to update
     */
    private void applyEndOfTurn(final CombatState state) {
        if (!state.isCombatOver()) {
            state.applyEndOfTurnTicks();
        }
    }
}
