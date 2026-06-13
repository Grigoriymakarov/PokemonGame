package services.combat.actions;

import models.combat.event.CombatEvent;
import models.combat.event.SwitchEvent;
import models.combat.CombatSide;
import models.combat.CombatState;
import models.shared.bugemon.TrainerBugemon;
import models.shared.trainer.Trainer;

/**
 * Action representing a voluntary switch of the active Bugemon.
 *
 * <p>The switch action allows a trainer to replace their active Bugemon with another
 * from their team.</p>
 *
 * @param replacer the Bugemon that will replace the current active Bugemon
 */
public record SwitchAction(TrainerBugemon replacer) implements Action {

    public SwitchAction {
        if (replacer == null) {
            throw new IllegalArgumentException("Replacement Bugemon cannot be null");
        }
    }

    @Override
    public Identifier identifier() {
        return Identifier.SWITCH;
    }

    @Override
    public void execute(final ActionContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ActionContext cannot be null");
        }

        final Trainer actorTrainer = context.actorTrainer();
        final CombatState combatState = context.combatState();
        final TrainerBugemon previousActive = context.getActor();

        actorTrainer.setActiveBugemon(this.replacer);

        if (context.actorSide() == CombatSide.PLAYER) {
            combatState.setPlayerActiveBugemon(this.replacer);
        } else {
            combatState.setEnemyActiveBugemon(this.replacer);
        }

        combatState.addEvent(new SwitchEvent(
                previousActive.getName(),
                this.replacer.getName()
        ));
    }
}
