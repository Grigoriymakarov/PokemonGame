package models.combat.event;

/**
 * Combat event representing a Bugémon recovering HP.
 *
 * <p>This event is fired when a Bugémon regains hit points, whether from
 * a consumed item or any other healing effect.</p>
 *
 * @see CombatEvent
 */
public final class HealEvent extends CombatEvent {

    /** Name of the Bugémon that recovered HP. */
    private final String bugemonName;

    /** Amount of HP recovered. */
    private final int amount;

    /**
     * Creates a new {@code HealEvent}.
     *
     * @param bugemonName name of the Bugémon that recovered HP; must not be {@code null}
     * @param amount      amount of HP restored; expected to be positive
     */
    public HealEvent(final String bugemonName, final int amount) {
        this.bugemonName = bugemonName;
        this.amount      = amount;
    }

    /**
     * Returns a human-readable description of this heal event.
     *
     * <p>Formats {@link constants.MessageConstants#LOG_ITEM_HEAL} with
     * the Bugémon's name and the amount of HP recovered.</p>
     *
     * @return the formatted heal message
     */
    @Override
    public String toString() {
        return String.format(constants.MessageConstants.LOG_ITEM_HEAL, this.bugemonName, this.amount);
    }
}