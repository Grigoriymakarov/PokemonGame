package dto;

import models.shared.Item;

import java.util.List;

public interface InventoryDTO {
    /**
     * Returns the list of available items.
     *
     * @return an unchangeable list containing all item keys currently stored in the inventory
     */
    List<Item> getAvailableItems();

    /**
     * Returns the quantity available for a given item.
     *
     * @param item the item to query
     * @return the item quantity, or {@code 0} when not present
     */
    int getQuantity(Item item);
}
