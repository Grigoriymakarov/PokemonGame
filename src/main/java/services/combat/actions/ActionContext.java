package services.combat.actions;

import models.combat.CombatSide;
import models.combat.CombatState;
import models.shared.bugemon.TrainerBugemon;
import models.shared.trainer.Trainer;

/**
 * Context object that encapsulates all relevant information about the current state of combat
 * and the trainers involved in an action. This record is passed to action implementations
 * to provide them with the necessary data to execute their logic.
 *
 * <p>The {@code ActionContext} includes references to the current {@link CombatState},
 * the acting trainer, the target trainer, and the side (PLAYER or ENEMY) that is performing
 * the action. It also provides convenience methods to access the active Bugemon of each trainer
 * and their initiative values.</p>
 *
 * @param combatState   The global state of the current combat, including all trainers and their Bugemon.
 * @param actorTrainer  The trainer executing the current action.
 * @param targetTrainer The trainer receiving the current action.
 * @param actorSide     The side (PLAYER or ENEMY) that is currently playing.
 */
public record ActionContext(
        CombatState combatState,
        Trainer actorTrainer,
        Trainer targetTrainer,
        CombatSide actorSide
) {
    public ActionContext {
        if (combatState == null) throw new IllegalArgumentException("combatState is required");
        if (actorTrainer == null) throw new IllegalArgumentException("actorTrainer is required");
        if (targetTrainer == null) throw new IllegalArgumentException("targetTrainer is required");
        if (actorSide == null) throw new IllegalArgumentException("actorSide is required");
    }

    /** @return The active Bugemon of the acting trainer. */
    public TrainerBugemon getActor() {
        return this.actorTrainer.getActiveBugemon();
    }

    /** @return The active Bugemon of the target trainer. */
    public TrainerBugemon getTarget() {
        return this.targetTrainer.getActiveBugemon();
    }



    /** @return The side (PLAYER or ENEMY) that is receiving the action. */
    public CombatSide getTargetSide() {
        return this.actorSide.opponent();
    }


    /** @return The initiative value of the acting Bugemon. */
    public int getActorInitiative() {
        return this.actorTrainer.getActiveBugemonInitiative();
    }
}
