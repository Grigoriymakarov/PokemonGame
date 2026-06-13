package services.combat;

import models.combat.event.KoEvent;
import models.combat.CombatSide;
import models.combat.CombatState;
import models.exceptions.NoAvailableReplacerException;
import services.combat.actions.ActionContext;
import models.shared.bugemon.TrainerBugemon;
import models.shared.trainer.Trainer;
import services.combat.actions.AttackAction;

import java.util.List;


/**
 * Resolves knock-out consequences and manages team transitions during combat.
 *
 * <p>{@code KOResolver} is responsible for:
 * <ul>
 *   <li>Detecting when a Bugemon has reached zero HP (fainted)</li>
 *   <li>Ending the combat if one trainer has no remaining Bugemon</li>
 *   <li>Requesting a player switch if the player's Bugemon faints</li>
 *   <li>Automatically selecting and sending the next enemy Bugemon</li>
 * </ul>
 *
 * <p>This resolver is typically invoked by attack actions after determining that
 * damage dealt has reduced a target's HP to zero or below.</p>
 *
 * @see services.combat.AttackResolver
 * @see AttackAction
 * @see CombatState
 */
public class KOResolver {
    private final ActionContext context;

    /**
     * Creates a resolver with the given context.
     *
     * @param context the action context containing the combat state, target information, and side data
     */
    public KOResolver(final ActionContext context) {
        this.context = context;
    }

    /**
     * Checks whether the target in the provided context has fainted and, if so,
     * applies the appropriate knock-out handling.
     *
     * <p>If the target is still alive (HP > 0), this method returns immediately without action.
     * Otherwise, it:
     * <ol>
     *   <li>Marks the Bugemon as no longer alive</li>
     *   <li>Logs the K.O. to the combat state</li>
     *   <li>Delegates the postK.O. flow to {@link #handleKO(Trainer, boolean, CombatState)}</li>
     * </ol>
     */
    public void processFainted() {
        final TrainerBugemon bugemon = this.context.getTarget();
        final CombatState combatState = this.context.combatState();
        //isPlayer = True if target is a player
        final boolean isPlayer = this.context.getTargetSide() == CombatSide.PLAYER;
        final Trainer trainer = this.context.targetTrainer();

        // Check if the target is still alive
        if (bugemon.isAlive()) {
            return;
        }

        combatState.addEvent(new KoEvent(bugemon.getName()));

        // Handle the consequences of the knock-out
        this.handleKO(trainer, isPlayer, combatState);
    }

    /**
     * Applies the full consequences of a knock-out for the specified trainer.
     *
     * <p>The response depends on whether the fainted side has any remaining alive Bugemon:
     * <ul>
     *   <li><strong>No remaining Bugemon:</strong> the combat ends immediately, setting
     *       the opposing side as the winner</li>
     *   <li><strong>Player side faint:</strong> the combat enters a waiting state
     *       ({@link CombatState#setAwaitingPlayerSwitch(boolean) awaitingPlayerSwitch} = true)
     *       until the player chooses a replacement</li>
     *   <li><strong>Enemy side faint:</strong> the next alive Bugemon is automatically
     *       selected using the replacement strategy and sent to the field</li>
     * </ul>
     *
     * <p>This separation of player vs. automatic enemy replacement ensures that the player
     * always has agency in selecting their next Bugemon, while the enemy follows a deterministic strategy.</p>
     *
     * @param trainer the trainer whose active Bugemon has fainted
     * @param isPlayer {@code true} if the fainted side is the player side, {@code false} for enemy
     * @param combatState the combat state to update with the new state
     * @throws IllegalStateException if the enemy replacement strategy fails to select a valid Bugemon
     */
    private void handleKO(final Trainer trainer, final boolean isPlayer, final CombatState combatState) {
        // Get the list of remaining alive Bugemon for this trainer
        final List<TrainerBugemon> aliveTeam = trainer.getAliveTeam();

        // If no Bugemon remain, the opposite side has won
        if (aliveTeam.isEmpty()) {
            combatState.setPlayerWon(!isPlayer);
            combatState.setCombatOver(true);
            return;
        }

        // If the player's Bugemon fainted, wait for the player to select a replacement
        if (isPlayer) {
            combatState.setAwaitingPlayerSwitch(true);
            return;
        }

        try {
            // If an enemy Bugemon fainted, automatically select and send the next one
            final TrainerBugemon next = trainer.chooseReplacerAfterKO(combatState);
            // Update the enemy trainer and shared state with the new active Bugemon
            trainer.setActiveBugemon(next);
            combatState.setEnemyActiveBugemon(next);
        } catch (final NoAvailableReplacerException err) {
            throw new IllegalStateException(
                    "The trainer could not choose a valid replacement after KO.",
                    err
            );
        }
    }

}
