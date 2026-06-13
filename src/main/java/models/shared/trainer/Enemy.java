package models.shared.trainer;

import dto.CombatStateDTO;
import models.shared.Attack;
import services.combat.strategies.ReplacementStrategy;
import models.exceptions.CouldNotDetermineActionException;
import models.exceptions.NoAvailableReplacerException;
import models.shared.bugemon.TrainerBugemon;
import services.combat.strategies.AttackChoiceStrategy;

import java.util.Objects;

/**
 * Represents the non-player trainer participating in a combat.
 *
 * <p>{@code Enemy} is the concrete {@link Trainer} type used for the opposing side.
 * The class itself only models the enemy trainer as a combat participant with a name,
 * a team, an active Bugemon, and an inventory inherited from {@link Trainer}.</p>
 *
 * <p>The decision logic used to control this trainer in battle is handled elsewhere
 * by combat services and attack choice strategies, not by this class directly.</p>
 *
 * @see Trainer
 * @see Player
 */
public class Enemy extends Trainer {

    private final AttackChoiceStrategy attackChoiceStrategy;
    private final ReplacementStrategy replacementStrategy;

    /**
     * Creates an enemy trainer with the given name.
     *
     * @param name the name of the enemy trainer
     */
    public Enemy(
            final String name,
            final AttackChoiceStrategy attackChoiceStrategy,
            final ReplacementStrategy replacementStrategy
    ) {
        super(name);
        this.attackChoiceStrategy = Objects.requireNonNull(attackChoiceStrategy, "attackChoiceStrategy cannot be null");
        this.replacementStrategy = Objects.requireNonNull(replacementStrategy, "replacementStrategy cannot be null");
    }

    /**
     * Chooses an attack for this trainer based on the current combat state.
     *
     * <p>The enemy only decides which attack it wants to use. The combat service is
     * responsible for turning that decision into an executable action.</p>
     *
     * @param combatState the current state of the combat
     * @return the chosen attack
     */

    public Attack chooseAttack(final CombatStateDTO combatState) throws CouldNotDetermineActionException {
        return this.attackChoiceStrategy.chooseAttack(combatState);
    }

    /**
     * Chooses a replacement Bugemon for this trainer after the current active Bugemon has been knocked out.
     *
     * <p>This method is abstract and must be implemented by concrete subclasses of {@code Trainer}.
     * It should contain the logic for selecting a new active Bugemon from the trainer's team after a knockout,
     * which may involve analyzing the combat state, the remaining alive Bugemon, and other factors.</p>
     *
     * @param combatState the current state of the combat
     * @return the chosen switch action to perform
     */
    @Override
    public TrainerBugemon chooseReplacerAfterKO(CombatStateDTO combatState) throws NoAvailableReplacerException {
        return this.replacementStrategy.chooseBugemon(this.team);
    }
}
