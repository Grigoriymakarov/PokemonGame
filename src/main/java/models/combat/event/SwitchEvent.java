package models.combat.event;

/**
 * Combat event representing a Bugémon switch.
 *
 * <p>Two scenarios are distinguished by the value of {@link #previousName}:</p>
 * <ul>
 *   <li><b>Voluntary switch</b> — {@code previousName} is non-null: the active
 *       Bugémon withdrew and a new one entered.</li>
 *   <li><b>Forced entry</b> — {@code previousName} is {@code null}: a Bugémon
 *       enters directly with no withdrawal line (e.g. at the start of combat or
 *       after a K.O.).</li>
 * </ul>
 *
 * @see CombatEvent
 */
public final class SwitchEvent extends CombatEvent {

    /**
     * Name of the Bugémon that left the field, or {@code null} for a forced entry.
     */
    private final String previousName;

    /** Name of the Bugémon that entered the field. */
    private final String newName;

    /**
     * Creates a new {@code SwitchEvent}.
     *
     * @param previousName name of the withdrawing Bugémon;
     *                     {@code null} indicates a forced entry with no withdrawal message
     * @param newName      name of the incoming Bugémon; must not be {@code null}
     */
    public SwitchEvent(final String previousName, final String newName) {
        this.previousName = previousName;
        this.newName      = newName;
    }


    /**
     * Returns a human-readable description of this switch event.
     *
     * <ul>
     *   <li>If {@link #previousName} is {@code null}, formats
     *       {@link constants.MessageConstants#LOG_SWITCH_ENTER} with the incoming
     *       Bugémon's name only.</li>
     *   <li>Otherwise, formats {@link constants.MessageConstants#LOG_SWITCH} with
     *       the withdrawing Bugémon's name followed by the incoming one's.</li>
     * </ul>
     *
     * @return the formatted switch message
     */
    @Override
    public String toString() {
        if (this.previousName == null) {
            return String.format(constants.MessageConstants.LOG_SWITCH_ENTER, this.newName);
        }
        return String.format(constants.MessageConstants.LOG_SWITCH, this.previousName, this.newName);
    }
}