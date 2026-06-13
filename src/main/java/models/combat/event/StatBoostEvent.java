package models.combat.event;

/**
 * Combat event representing a stat boost applied to a Bugémon.
 *
 * <p>A stat boost can originate from an attack effect or a consumed item.
 * It records which Bugémon was affected, which stat was raised, and by how much.</p>
 *
 * @see CombatEvent
 */
public final class StatBoostEvent extends CombatEvent {

    /** Name of the Bugémon whose stat was boosted. */
    private final String bugemonName;

    /** Name of the stat that was raised (e.g. {@code "Attack"}, {@code "Speed"}). */
    private final String statName;

    /** Amount by which the stat was increased. */
    private final int amount;

    /**
     * Creates a new {@code StatBoostEvent}.
     *
     * @param bugemonName name of the Bugémon that received the boost;
     *                    must not be {@code null}
     * @param statName    name of the stat that was raised; must not be {@code null}
     * @param amount      amount by which the stat increased; expected to be positive
     */
    public StatBoostEvent(final String bugemonName, final String statName, final int amount) {
        this.bugemonName = bugemonName;
        this.statName    = statName;
        this.amount      = amount;
    }

    /**
     * Returns a human-readable description of this stat boost event.
     *
     * <p>Formats {@link constants.MessageConstants#LOG_ITEM_STAT_BOOST} with
     * the Bugémon's name, the stat name, and the boost amount.</p>
     *
     * @return the formatted stat boost message
     */
    @Override
    public String toString() {
        if (this.amount < 0) {
            return String.format(constants.MessageConstants.LOG_ITEM_STAT_DECREASE,
                    this.bugemonName, this.statName, Math.abs(this.amount));
        }
        return String.format(constants.MessageConstants.LOG_ITEM_STAT_BOOST,
                this.bugemonName, this.statName, this.amount); }
}