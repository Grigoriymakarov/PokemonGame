package models.combat.event;

/**
 * Combat event representing the removal of all stat maluses from a Bugémon.
 *
 * <p>This event is fired when an effect or item clears every negative stat
 * modifier currently applied to the affected Bugémon.</p>
 *
 * @see CombatEvent
 */
public final class ResetMalusEvent extends CombatEvent {

    /** Name of the Bugémon whose maluses were cleared. */
    private final String bugemonName;

    /**
     * Creates a new {@code ResetMalusEvent}.
     *
     * @param bugemonName name of the Bugémon whose maluses were cleared;
     *                    must not be {@code null}
     */
    public ResetMalusEvent(final String bugemonName) {
        this.bugemonName = bugemonName;
    }


    /**
     * Returns a human-readable description of this malus reset event.
     *
     * <p>Formats {@link constants.MessageConstants#LOG_ITEM_RESET_MALUS} with
     * the affected Bugémon's name.</p>
     *
     * @return the formatted malus reset message
     */
    @Override
    public String toString() {
        return String.format(constants.MessageConstants.LOG_ITEM_RESET_MALUS, this.bugemonName);
    }
}