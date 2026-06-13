package services.combat.actions;

import models.exceptions.ActionExecutionException;
import models.shared.Attack;
import services.combat.AttackResolver;
import services.combat.KOResolver;

import java.util.Objects;

/**
 * Action representing a direct attack against the opposing active Bugemon.
 *
 * <p>The attack action encapsulates a single move performed during combat. Its execution
 * delegates to specialized resolvers:
 * <ul>
 *   <li>{@link services.combat.AttackResolver} handles damage calculation and attack effect application</li>
 *   <li>{@link services.combat.KOResolver} handles post-attack knock-out consequences</li>
 * </ul>
 *
 * <p>The action follows a clear separation of concerns: it knows what attack to perform,
 * but lets dedicated resolvers determine how and what happens.</p>
 *
 * @see Action
 * @see ActionContext
 * @see services.combat.AttackResolver
 * @see services.combat.KOResolver
 */
public final class AttackAction implements Action {

    private final Attack attack;


    /**
     * Creates an attack action for the supplied attack.
     *
     * @param attack the attack that will be executed during {@link #execute(ActionContext)}
     * @throws NullPointerException if attack is null
     */
    public AttackAction(final Attack attack) {
        this.attack = Objects.requireNonNull(attack, "Attack cannot be null");
    }

    @Override
    public Identifier identifier() {
        return Identifier.ATTACK;
    }

    /**
     * Executes the configured attack in the provided combat context.
     *
     * <p>The execution process:
     * <ol>
     *   <li>Delegates to {@link AttackResolver} to apply damage and effects</li>
     *   <li>If the target fainted, immediately triggers knock-out handling via
     *       {@link KOResolver}</li>
     * </ol>
     *
     * @param context the combat context containing the shared state and trainers
     * @throws NullPointerException if context is null
     */
    @Override
    public void execute(final ActionContext context) throws ActionExecutionException {
        if (context == null) {
            throw new IllegalArgumentException("ActionContext cannot be null");
        }

        AttackResolver attackResolver = new AttackResolver(
                context.combatState(),
                context.getActor(),
                context.getTarget(),
                this.attack
        );
        attackResolver.resolve();

        if (attackResolver.isTargetFainted()) {
            KOResolver koResolver = new KOResolver(context);
            koResolver.processFainted();
        }
    }

    /**
     * Returns the attack wrapped by this action.
     *
     * @return the configured attack
     */
    public Attack getAttack() {
        return this.attack;
    }
}
