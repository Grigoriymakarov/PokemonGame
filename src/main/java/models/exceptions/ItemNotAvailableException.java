package models.exceptions;

import models.shared.Item;
import models.shared.Inventory;

/**
 * Exception thrown when attempting to consume an item that is not available in the inventory.
 *
 * <p>This exception is raised when {@link models.shared.Inventory#consumeItem(models.shared.Item)}
 * is called with an item that is not present or has zero quantity in the inventory.</p>
 *
 * @see models.shared.Inventory
 */
public class ItemNotAvailableException extends Exception {
    private final Item item;
    private final Inventory inventory;

    /**
     * Constructor with error message, item, and inventory
     *
     * @param message Detailed description of the availability error
     * @param item the item that was unavailable
     * @param inventory the inventory in which the item was not found
     */
    public ItemNotAvailableException(final String message, final Item item, final Inventory inventory) {
        super(message);
        this.item = item;
        this.inventory = inventory;
    }

    /**
     * Constructor with message, cause, item, and inventory
     *
     * @param message Detailed description of the availability error
     * @param cause   The original cause of the exception
     * @param item the item that was unavailable
     * @param inventory the inventory in which the item was not found
     */
    public ItemNotAvailableException(final String message, final Throwable cause, final Item item, final Inventory inventory) {
        super(message, cause);
        this.item = item;
        this.inventory = inventory;
    }

    /**
     * Returns the item that was unavailable.
     *
     * @return the item that could not be consumed
     */
    public Item getItem() {
        return this.item;
    }

    /**
     * Returns the inventory in which the item was not available.
     *
     * @return the inventory
     */
    public Inventory getInventory() {
        return this.inventory;
    }
}

