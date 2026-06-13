package services.combat.actions;

import constants.LabelConstants;

/**
 * Action representing a player's forfeit of the combat.
 *
 * <p>When a player chooses to forfeit (surrender), this action immediately marks the
 * combat as lost for the player side. The battle ends immediately without further
 * action resolution or turn processing.</p>
 *
 * <p>Forfeiting is a valid strategic choice that may result from:</p>
 * <ul>
 *   <li>All player Bugemon have fainted (forced loss)</li>
 *   <li>Player chooses to surrender voluntarily</li>
 * </ul>
 *
 * @see Action
 * @see ActionContext
 * @see models.combat.CombatState#markPlayerDefeat(String)
 */
public final class ForfeitAction implements Action {

    /**
     * Creates a forfeit action.
     *
     * <p>No additional state is needed; the action is fully defined by its type.</p>
     */
    public ForfeitAction() {
    }

    @Override
    public Identifier identifier() {
        return Identifier.FORFEIT;
    }

    /**
     * Executes the forfeit, immediately marking the combat as a player loss.
     *
     * <p>This action:</p>
     * <ol>
     *   <li>Accesses the shared combat state</li>
     *   <li>Records the forfeit with a message in the combat log</li>
     *   <li>Ends the combat, setting the player as the loser</li>
     * </ol>
     *
     * @param context the combat context containing the shared state
     * @throws NullPointerException if context is null
     */
    @Override
    public void execute(final ActionContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ActionContext cannot be null");
        }

        context.combatState().markPlayerDefeat(LabelConstants.FORFEIT_MESSAGE);
    }
}
