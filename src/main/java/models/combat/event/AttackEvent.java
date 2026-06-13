package models.combat.event;

import models.shared.TypeEffectiveness;

import java.util.ArrayList;
import java.util.List;

/**
 * Combat event representing a Bugémon using a move against a target.
 *
 * <p>Produces multiple battle log lines assembled in order:</p>
 * <ol>
 *   <li>The use-action line (attacker + move name).</li>
 *   <li>An effectiveness line, if the move is
 *       {@link TypeEffectiveness#SUPER_EFFECTIVE} or
 *       {@link TypeEffectiveness#NOT_VERY_EFFECTIVE}
 *       ({@link TypeEffectiveness#NEUTRAL} produces no extra line).</li>
 *   <li>A critical-hit line, if the hit is critical.</li>
 *   <li>The damage-dealt line (target + damage amount).</li>
 * </ol>
 *
 * <p>All lines are joined by {@code \n} and returned by {@link #toString()}.</p>
 *
 * @see CombatEvent
 */
public final class AttackEvent extends CombatEvent {

    /** Name of the Bugémon that used the move. */
    private final String attackerName;

    /** Name of the move that was used. */
    private final String attackName;

    /** Name of the Bugémon that was targeted. */
    private final String targetName;

    /** Amount of damage dealt to the target. */
    private final int damage;

    /** Type-effectiveness of the move against the target's type. */
    private final TypeEffectiveness effectiveness;

    /** Whether the hit was a critical strike. */
    private final boolean critical;

    /**
     * Creates a new {@code AttackEvent}.
     *
     * @param attackerName  name of the attacking Bugémon; must not be {@code null}
     * @param attackName    name of the move used; must not be {@code null}
     * @param targetName    name of the targeted Bugémon; must not be {@code null}
     * @param damage        amount of damage dealt; expected to be non-negative
     * @param effectiveness type-effectiveness of the move; must not be {@code null}
     * @param critical      {@code true} if the hit was a critical strike
     */
    public AttackEvent(final String attackerName, final String attackName,
                final String targetName, final int damage,
                final TypeEffectiveness effectiveness, final boolean critical) {
        this.attackerName  = attackerName;
        this.attackName    = attackName;
        this.targetName    = targetName;
        this.damage        = damage;
        this.effectiveness = effectiveness;
        this.critical      = critical;
    }
    /**
     * Returns the amount of damage dealt to the target.
     *
     * @return the damage amount; expected to be non-negative
     */
    public int getDamage() { return this.damage; }

    /**
     * Returns the type-effectiveness of the move against the target's type.
     *
     * @return the effectiveness value; never {@code null}
     */
    public TypeEffectiveness getEffectiveness() { return this.effectiveness; }



    /**
     * Returns the battle log lines describing this attack, joined by {@code \n}.
     *
     * <p>Lines are assembled in the following order:</p>
     * <ol>
     *   <li>{@link constants.MessageConstants#LOG_USE_ACTION} — attacker and move name.</li>
     *   <li>{@link constants.MessageConstants#LOG_SUPER_EFFECTIVE} or
     *       {@link constants.MessageConstants#LOG_NOT_VERY_EFFECTIVE} —
     *       included only for non-neutral effectiveness.</li>
     *   <li>{@link constants.MessageConstants#LOG_CRITICAL_HIT} —
     *       included only on a critical hit.</li>
     *   <li>{@link constants.MessageConstants#LOG_DAMAGE_DEALT} — target and damage amount.</li>
     * </ol>
     *
     * @return the newline-separated battle log message; never {@code null}
     */
    @Override
    public String toString() {
        final List<String> lines = new ArrayList<>();

        lines.add(String.format(
                constants.MessageConstants.LOG_USE_ACTION,
                this.attackerName,
                this.attackName
        ));

        switch (this.effectiveness) {
            case SUPER_EFFECTIVE ->
                    lines.add(constants.MessageConstants.LOG_SUPER_EFFECTIVE);
            case NOT_VERY_EFFECTIVE ->
                    lines.add(constants.MessageConstants.LOG_NOT_VERY_EFFECTIVE);
        }

        if (this.critical) {
            lines.add(constants.MessageConstants.LOG_CRITICAL_HIT);
        }

        lines.add(String.format(
                constants.MessageConstants.LOG_DAMAGE_DEALT,
                this.targetName,
                this.damage
        ));

        return String.join("\n", lines);
    }
}