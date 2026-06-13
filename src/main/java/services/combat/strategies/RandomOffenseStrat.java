package services.combat.strategies;

import dto.CombatStateDTO;
import models.exceptions.CouldNotDetermineActionException;
import models.shared.Attack;

import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * Offensive strategy that selects a random attack from the active Bugemon's move set.
 *
 * <p>This implementation is intended for simple AI behavior. It only considers the
 * enemy active Bugemon's currently available attacks and returns the selected attack.</p>
 *
 * @see AttackChoiceStrategy
 */
public final class RandomOffenseStrat implements AttackChoiceStrategy {

    private final RandomGenerator random = new Random();

    /**
     * Chooses a random attack for the enemy side.
     *
     * @param combatState the current combat state used to access available attacks
     * @return one randomly selected attack
     */
    @Override
    public Attack chooseAttack(final CombatStateDTO combatState) throws CouldNotDetermineActionException {
        final List<Attack> availableAttacks = combatState.getEnemyBugemonCurrentAttacks();
        if (availableAttacks.isEmpty()) {
            throw new CouldNotDetermineActionException("No attacks available to choose from for the enemy");
        }
        return availableAttacks.get(this.random.nextInt(availableAttacks.size()));
    }
}
