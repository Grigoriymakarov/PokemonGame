package models.combat.event;

/**
 * Combat event representing a trainer using an item.
 *
 * <p>This event is fired when a trainer consumes an item from their inventory
 * during combat, recording who used it and which item was used.</p>
 *
 * @see CombatEvent
 */
public final class ItemUsedEvent extends CombatEvent {

    /** Name of the trainer who used the item. */
    private final String trainerName;

    /** Name of the item that was used. */
    private final String itemName;

    /**
     * Creates a new {@code ItemUsedEvent}.
     *
     * @param trainerName name of the trainer who used the item; must not be {@code null}
     * @param itemName    name of the item that was consumed; must not be {@code null}
     */
    public ItemUsedEvent(final String trainerName, final String itemName) {
        this.trainerName = trainerName;
        this.itemName    = itemName;
    }

    /**
     * Returns the name of the item that was used.
     *
     * @return the item's name; never {@code null}
     */
    public String getItemName() { return this.itemName; }


    /**
     * Returns a human-readable description of this item use event.
     *
     * <p>Formats {@link constants.MessageConstants#LOG_USE_ACTION} with
     * the trainer's name followed by the item's name.</p>
     *
     * @return the formatted item use message
     */
    @Override
    public String toString() {
        return String.format(constants.MessageConstants.LOG_USE_ACTION, this.trainerName, this.itemName);
    }
}