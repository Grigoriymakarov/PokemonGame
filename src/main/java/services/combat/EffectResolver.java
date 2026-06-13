package services.combat;

import models.combat.event.CombatEvent;
import models.combat.event.ResetMalusEvent;
import models.combat.event.StatBoostEvent;
import models.combat.event.HealEvent;
import models.combat.CombatState;
import models.exceptions.EffectApplicationException;
import models.exceptions.WrongEffectTypeException;
import models.shared.Effect;
import models.shared.bugemon.TrainerBugemon;
import models.shared.statistics.Statistics;
import models.shared.statistics.TemporaryStatsModifier;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Resolves and applies combat effects from attacks or items.
 *
 * <p>For each effect applied, a {@link CombatEvent} is recorded in the combat state
 * so the view can display what happened (stat boost, heal, malus reset).
 *
 * <p>Note: {@code UseItemAction} formats its own effect messages via {@code itemEffect()};
 * those events are not duplicated here.</p>
 *
 * @see models.shared.Effect
 * @see TemporaryStatsModifier
 */
public class EffectResolver {

    private final Collection<Effect> effects;
    private final CombatState state;
    private final TrainerBugemon author;
    private final TrainerBugemon opponent;

    /**
     * Creates a new EffectResolver with the given parameters.
     *
     * @param effects the list of effects to apply (can be null or empty)
     * @param state   the current combat state
     * @param author  the Bugémon initiating the effects
     * @param opponent  the currently active opposing Bugémon
     */
    public EffectResolver(final Collection<Effect> effects, final CombatState state,
                          final TrainerBugemon author, final TrainerBugemon opponent) {
        this.effects = effects;
        this.state = state;
        this.author = author;
        this.opponent = opponent;
    }

    /**
     * Applies all effects from an attack or consumable item.
     */
    public void apply() throws EffectApplicationException {
        if (this.effects == null || this.effects.isEmpty()) {
            return;
        }
        for (final Effect effect : this.effects) {
            this.applySingle(effect);
        }
    }

    /**
     * Applies a single effect and records the corresponding {@link CombatEvent} in the state.
     */
    private void applySingle(final Effect effect) throws EffectApplicationException {
        if (effect == null) {
            return;
        }

        final List<TrainerBugemon> receivers = this.resolveTargets(effect);
        try {
            switch (effect.getType()) {
                case HEAL:
                    this.applyHeal(effect, receivers);
                    break;
                case STAT_MODIFIER:
                    this.applyStatModifier(effect, receivers);
                    break;
                case RESET_MALUS:
                    this.applyResetMalus(receivers);
                    break;
            }
        } catch (WrongEffectTypeException e) {
            throw new EffectApplicationException(
                    "Could not apply effect " + effect + " due to effect type mismatch (type: " + effect.getType() + ")", e);
        }
    }

    /**
     * Applies a healing effect and records a {@link HealEvent} for each receiver.
     */
    private void applyHeal(final Effect effect, final Iterable<TrainerBugemon> receivers)
            throws WrongEffectTypeException {
        if (effect.getType() != Effect.EffectType.HEAL) {
            throw new WrongEffectTypeException("Effect type must be HEAL to apply healing");
        }
        for (final TrainerBugemon receiver : receivers) {
            if (receiver.isAlive()) {
                receiver.heal(effect.getValue());
                this.state.addEvent(new HealEvent(receiver.getName(), effect.getValue()));
            }
        }
    }

    /**
     * Applies a stat modifier and records a {@link StatBoostEvent} for each receiver.
     */
    private void applyStatModifier(final Effect effect, final Iterable<TrainerBugemon> receivers)
                                   throws WrongEffectTypeException {
        if (effect.getType() != Effect.EffectType.STAT_MODIFIER) {
            throw new WrongEffectTypeException("Effect type must be STAT_MODIFIER to apply stat modifications");
        }
        for (final TrainerBugemon receiver : receivers) {
            switch (effect.getDuration()) {
                case PERMANENT:
                    receiver.increaseCombatStat(effect.getStat(), effect.getModifier());
                    break;
                case SINGLE_TURN:
                    final TemporaryStatsModifier modifier = new TemporaryStatsModifier(1);
                    modifier.increaseStat(effect.getStat(), effect.getModifier());
                    receiver.addTemporaryModifier(modifier);
                    break;
            }
            this.state.addEvent(new StatBoostEvent(
                    receiver.getName(),
                    this.statLabel(effect.getStat()),
                    effect.getModifier()
            ));
        }
    }

    /**
     * Clears all maluses and records a {@link ResetMalusEvent} for each receiver.
     */
    private void applyResetMalus(final Iterable<TrainerBugemon> receivers) {
        for (final TrainerBugemon receiver : receivers) {
            receiver.resetMaluses();
            this.state.addEvent(new ResetMalusEvent(receiver.getName()));
        }
    }

    /**
     * Determines which Bugémon should receive the effect based on its target configuration.
     */
    private List<TrainerBugemon> resolveTargets(final Effect effect) {
        return switch (effect.getTarget()) {
            case USER     -> Collections.singletonList(this.author);
            case OPPONENT -> Collections.singletonList(this.opponent);
            case TEAM     -> this.state.getAlliesOf(this.author);
        };
    }

    /**
     * Returns a French display label for a stat, used in {@link StatBoostEvent}.
     */
    private static String statLabel(final Statistics.Stat stat) {
        if (stat == null) return "statistique";
        return switch (stat) {
            case ATTACK     -> "son attaque";
            case DEFENSE    -> "sa défense";
            case INITIATIVE -> "son initiative";
            case HP         -> "ses PV";
        };
    }
}