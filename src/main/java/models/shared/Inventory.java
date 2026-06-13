package models.shared;

import java.util.*;

import dto.InventoryDTO;
import models.exceptions.ItemNotAvailableException;

/**
 * Represents a trainer inventory containing items and their quantities.
 *
 * <p>The inventory stores each {@link Item} with an integer quantity and provides
 * helper methods to add, consume, query, and clear entries.</p>
 */
public class Inventory implements InventoryDTO {
    private final HashMap<Item, Integer> items;

    /**
     * Creates an empty inventory.
     */
    public Inventory(){
        this.items = new HashMap<>();
    }

    /**
     * Returns an unmodifiable view of the inventory items.
     * To modify the inventory, use {@link #addItem} or {@link #consumeItem}.
     *
     * @return a map of items and their respective quantities.
     */
    public Map<Item, Integer> getItems() {
        return Collections.unmodifiableMap(this.items);
    }
    /**
     * Adds a quantity of an item to the inventory.
     *
     * <p>If the item is already present, the quantity is incremented.</p>
     *
     * @param item the item to add
     * @param quantity the quantity to add
     */
    public void addItem(final Item item, final int quantity){
        this.items.put(item, this.items.getOrDefault(item,0)+quantity);
    }

    /**
     * Returns the quantity available for a given item.
     *
     * @param item the item to query
     * @return the item quantity, or {@code 0} when not present
     */
    public int getQuantity(final Item item){
        return this.items.getOrDefault(item,0);
    }

    /**
     * Checks whether at least one unit of an item is available.
     *
     * @param item the item to check
     * @return {@code true} if quantity is greater than {@code 0}, otherwise {@code false}
     */
    public boolean hasItem(final Item item) {
        return this.items.getOrDefault(item, 0) > 0;
    }

    /**
     * Consumes one unit of the given item.
     *
     * <p>If the resulting quantity is zero, the item is removed from the map.
     * If the item is absent, this method does nothing.</p>
     *
     * @param item the item to consume
     * @throws ItemNotAvailableException if the item is not present in the inventory
     */
    public void consumeItem(final Item item) throws ItemNotAvailableException {
        final int quantity = this.getQuantity(item);
        if(quantity == 0) {
            throw new ItemNotAvailableException("Item " + item.name() + " is not available in the inventory", item, this);
        }
        if (quantity == 1){
            this.items.remove(item);
        }
        else{
            this.items.put(item, quantity - 1);
        }

    }

    /**
     * Returns the list of available items.
     *
     * @return an unchangeable list containing all item keys currently stored in the inventory
     */
    public List<Item> getAvailableItems() {
        return Collections.unmodifiableList(new ArrayList<>(this.items.keySet()));
    }

    /**
     * Indicates whether the inventory contains no entries.
     *
     * @return {@code true} if the inventory is empty, otherwise {@code false}
     */
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    /**
     * Removes all entries from the inventory.
     */
    public void clear() {
        this.items.clear();
    }
}
