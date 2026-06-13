package services.combat.strategies;

import dto.CombatStateDTO;
import models.exceptions.CouldNotDetermineActionException;
import models.shared.Attack;

/**
 * Strategy interface used by the enemy AI to choose an attack for one combat turn.
 */
public interface AttackChoiceStrategy {

    /**
     * Chooses the attack to use based on the current combat state.
     *
     * @param combatState the combat state used to make the decision
     * @return the selected attack
     */
    Attack chooseAttack(CombatStateDTO combatState) throws CouldNotDetermineActionException;
}
