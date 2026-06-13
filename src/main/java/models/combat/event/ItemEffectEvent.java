package models.combat.event;

/**
 * Combat event carrying a free-form description of an item's effect.
 *
 * <p>Used for item effects that do not map to a more specific event type,
 * such as HP restoration (e.g. {@code "Bouldax récupère 30 PV."}).
 * The message is pre-formatted by the caller and returned as-is by
 * {@link #toString()}.</p>
 *
 * @see CombatEvent
 */
public final class ItemEffectEvent extends CombatEvent {

    /** Pre-formatted message describing the item's effect. */
    private final String message;

    /**
     * Creates a new {@code ItemEffectEvent}.
     *
     * @param message pre-formatted description of the item's effect;
     *                must not be {@code null}
     */
    public ItemEffectEvent(final String message) {
        this.message = message;
    }

    /**
     * Returns the pre-formatted effect message exactly as supplied at construction.
     *
     * @return the item effect message; never {@code null}
     */
    @Override
    public String toString() {
        return this.message;
    }
}