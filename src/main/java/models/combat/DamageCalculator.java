package models.combat;

import constants.LogicConstants;
import models.shared.Attack;
import models.shared.TypeEffectiveness;
import models.shared.bugemon.TrainerBugemon;
import models.shared.statistics.Statistics;

import java.util.Random;
import static java.lang.Math.round;

/**
 * Calculates damage dealt during a combat attack.
 *
 * <p>This class encapsulates the damage calculation formula used in combat. It considers:
 * <ul>
 *   <li>Attacker's attack stat</li>
 *   <li>Target's defense stat</li>
 *   <li>Attack base power</li>
 *   <li>Type effectiveness (super effective, neutral, not very effective)</li>
 *   <li>Critical hit chance</li>
 *   <li>Random variance</li>
 * </ul>
 *
 * <p>The calculator is executed during construction to compute the final damage value.
 * Once computed, the damage and related information can be retrieved
 * through getter methods.</p>
 *
 * @see TypeEffectiveness
 * @see Attack
 * @see TrainerBugemon
 */
public class DamageCalculator {
    private final TrainerBugemon attacker;
    private final TrainerBugemon target;
    private final Attack attack;

    private TypeEffectiveness effectiveness;
    private boolean isCritical;

    private final Random random;

    private int damage;

    public DamageCalculator(final TrainerBugemon attacker, final TrainerBugemon target, final Attack attack) {
        this(attacker, target, attack, new Random());
    }
    // Constructor that allows predictable random behavior for tests
    public DamageCalculator(final TrainerBugemon attacker, final TrainerBugemon target, final Attack attack, final Random random) {
        this.attacker = attacker;
        this.target = target;
        this.attack = attack;
        this.random = random;
        this.isCritical = false;
        this.computeDamage();
    }

    private void computeDamage() {
        // Compute base damage using stats
        final float attackFactor = (float) (100 + this.attacker.getStat(Statistics.Stat.ATTACK)) / 100;
        final float defenseFactor = (float) 100 / (100 + this.target.getStat(Statistics.Stat.DEFENSE));

        float damageFloat = this.attack.getPower() * attackFactor * defenseFactor;

        // Apply type effectiveness
        this.effectiveness = this.attack.getEffectivenessAgainst(this.target.getSpecieType());
        final float typeMultiplier = this.effectiveness.getMultiplier();

        damageFloat *= typeMultiplier;

        // Critical attack
        if (this.random.nextDouble() > LogicConstants.CRITICAL_HIT_THRESHOLD){
            damageFloat *= LogicConstants.CRITICAL_HIT_MULTIPLIER;
            this.isCritical = true;
        }

        this.damage = round(damageFloat);
    }

    // Getters
    public int getDamage() {
        return this.damage;
    }
    public boolean isCritical() {
        return this.isCritical;
    }
    public TypeEffectiveness getEffectiveness() {
        return this.effectiveness;
    }
}
