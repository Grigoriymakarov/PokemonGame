package services.combat;

import models.combat.event.CombatEvent;
import models.combat.event.ItemEffectEvent;
import models.combat.CombatState;
import models.combat.DamageCalculator;
import models.exceptions.EffectApplicationException;
import models.exceptions.InvalidAttackException;
import models.shared.Attack;
import models.shared.bugemon.TrainerBugemon;
import models.combat.event.AttackEvent ;
/**
 * Resolves attack resolution during combat.
 *
 * <p>{@code AttackResolver} is responsible for the complete lifecycle of an attack action:
 * <ul>
 *   <li>Validating that the attack is available to the performing Bugemon</li>
 *   <li>Applying direct damage to the target</li>
 *   <li>Recording the attack in the combat log</li>
 *   <li>Applying secondary effects (status changes, healing, stat boosts, etc.)</li>
 *   <li>Determining if the target has fainted as a result</li>
 * </ul>
 *
 * <p>This class delegates effect application to {@link EffectResolver}, ensuring
 * a clean separation of concerns between damage resolution and status effect handling.</p>
 *
 * @see EffectResolver
 * @see KOResolver
 * @see Attack
 * @see TrainerBugemon
 */
public class AttackResolver {
    private final CombatState state;
    private final TrainerBugemon author;
    private final TrainerBugemon target;
    private final Attack attack;
    private boolean isTargetFainted;

    /**
     * Creates an attack resolver with the given parameters.
     *
     * @param state the shared combat state where messages are logged
     * @param author the Bugemon performing the attack
     * @param target the Bugemon receiving the attack
     * @param attack the attack being executed
     * @throws InvalidAttackException if the attack is null or not available to the author
     * @throws NullPointerException if state, author, or target is null
     */
    public AttackResolver(final CombatState state, final TrainerBugemon author, final TrainerBugemon target, final Attack attack) {
        if (state == null) {
            throw new NullPointerException("state cannot be null");
        }
        if (author == null) {
            throw new NullPointerException("author cannot be null");
        }
        if (target == null) {
            throw new NullPointerException("target cannot be null");
        }
        if (attack == null) {
            throw new InvalidAttackException("attack cannot be null");
        }

        // Verify the attack is available to the author
        this.validateAttackAvailability(author, attack);

        this.state = state;
        this.author = author;
        this.target = target;
        this.attack = attack;
        this.isTargetFainted = false;
    }

    /**
     * Resolves a complete attack action.
     *
     * <p>The resolution process follows these steps:
     * <ol>
     *   <li>Applies direct damage to the target</li>
     *   <li>Records the action in the combat log</li>
     *   <li>Triggers any secondary effects from the attack</li>
     *   <li>Updates the target fainted flag</li>
     * </ol>
     */
    public void resolve() {
        // Apply damage
        final DamageCalculator damageCalculator = new DamageCalculator(this.author, this.target, this.attack);
        final int inflictedDamage = Math.min(damageCalculator.getDamage(), this.target.getCurrentHP());
        this.target.applyDamage(damageCalculator.getDamage());

        this.state.addEvent( new AttackEvent(this.author.getName(), this.attack.getName(), this.target.getName(),
                inflictedDamage, damageCalculator.getEffectiveness(), damageCalculator.isCritical())
        );

        try {
            EffectResolver resolver = new EffectResolver(this.attack.getEffects(), this.state, this.author, this.target);
            resolver.apply();
        } catch (EffectApplicationException e) {
        this.state.addEvent(new ItemEffectEvent("L'effet de " + this.attack.getName() + " n'a pas fonctionné."));
    }

        // Update the target fainted flag
        this.isTargetFainted = !this.target.isAlive();
    }

    /**
     * Returns whether the target fainted as a result of this attack.
     *
     * @return {@code true} if the target fainted, {@code false} otherwise
     */
    public boolean isTargetFainted() {
        return this.isTargetFainted;
    }

    /**
     * Validates that the provided attack is available to the provided Bugemon.
     *
     * @param author the Bugemon attempting the attack
     * @param attack the attack to validate
     * @throws InvalidAttackException if the attack is not in the Bugemon's moveset
     */
    private void validateAttackAvailability(final TrainerBugemon author, final Attack attack) {
        if (!author.hasAttack(attack)) {
            throw new InvalidAttackException(
                String.format(
                    "Attack '%s' is not avaiable for Bugemon '%s'",
                    attack.getName(),
                    author.getName()
                )
            );
        }
    }
}
