package models.combat.event;

/**
 * Abstract base class for all combat events emitted during battle resolution.
 *
 * <p>Each subclass represents a specific combat event and provides its own
 * {@link #toString()} implementation so the view can directly display the
 * event message without requiring {@code switch} statements or
 * {@code instanceof} checks.</p>
 *
 * <p>Combat events are typically produced by combat-related services and
 * consumed by the view layer through the combat state.</p>
 *
 * <p><b>Adding a new event type:</b></p>
 * <ol>
 *   <li>Create a subclass of {@code CombatEvent}.</li>
 *   <li>Override {@link #toString()} to provide the event message.</li>
 *   <li>Emit the event from the appropriate combat service or state logic.</li>
 * </ol>
 */
public abstract class CombatEvent {

    /**
     * Returns a human-readable description of this combat event.
     *
     * @return the formatted event message
     */
    @Override
    public abstract String toString();
}