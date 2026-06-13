package models.combat.event;

/**
 * Combat event representing a Bugémon being knocked out.
 *
 * <p>This event is fired when a Bugémon's HP reaches zero, signalling that
 * it can no longer participate in combat.</p>
 *
 * @see CombatEvent
 */
public final class KoEvent extends CombatEvent {

    /** Name of the Bugémon that was knocked out. */
    private final String targetName;

    /**
     * Creates a new {@code KoEvent}.
     *
     * @param targetName name of the Bugémon that fainted; must not be {@code null}
     */
    public KoEvent(final String targetName) {
        this.targetName = targetName;
    }

    /**
     * Returns a human-readable description of this K.O. event.
     *
     * <p>Formats {@link constants.MessageConstants#LOG_KO} with the
     * fainted Bugémon's name.</p>
     *
     * @return the formatted K.O. message
     */
    @Override
    public String toString() {
        return String.format(constants.MessageConstants.LOG_KO, this.targetName);
    }
}