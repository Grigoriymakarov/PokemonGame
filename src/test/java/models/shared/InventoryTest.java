package models.shared;

import models.exceptions.ItemNotAvailableException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Inventory} class.
 *
 * <p>This test class verifies item addition, quantity tracking, presence checks,
 * and item consumption behavior.</p>
 */
public class InventoryTest {
    private Inventory inventory;
    private Item baieRevigorante;
    private Item baieTonique;


    /**
     * Sets up an empty inventory and sample healing items before each test.
     */
    @Before
    public void setUp() {
        inventory = new Inventory();
        // The item id keeps the current data spelling used by the project.
        baieRevigorante = new Item("baie_revigorante", "Baie Revigorante", Item.Category.HEAL, null, "", "baie_ravigorante.png");
        baieTonique = new Item("baie_tonique", "Baie Tonique", Item.Category.HEAL, null,  "", "baie_tonique.png");
    }

    /**
     * Verifies that adding an item increases its stored quantity.
     */
    @Test
    public void addItemShouldIncreaseQuantity() {
        inventory.addItem(baieRevigorante, 3);
        assertEquals(3, inventory.getQuantity(baieRevigorante));
    }


    /**
     * Verifies that adding the same item several times sums the quantities.
     */
    @Test
    public void addSeveralItemShouldSumQuantities() {
        inventory.addItem(baieRevigorante, 2);
        inventory.addItem(baieRevigorante, 1);
        assertEquals(3, inventory.getQuantity(baieRevigorante));
    }


    /**
     * Verifies that an item is reported as present after being added.
     */
    @Test
    public void itemShouldBePresentWhenAdded() {
        inventory.addItem(baieRevigorante, 1);
        assertTrue(inventory.hasItem(baieRevigorante));
    }

    /**
     * Verifies that a different item is reported as absent when it was not added.
     */
    @Test
    public void itemShouldBeAbsentWhenNotAdded() {
        inventory.addItem(baieRevigorante, 1);
        assertFalse(inventory.hasItem(baieTonique));
    }


    /**
     * Verifies that an empty inventory returns zero for an item's quantity.
     */
    @Test
    public void getQuantityShouldReturnZeroIfEmpty() {
        assertEquals(0, inventory.getQuantity(baieRevigorante));
    }


    /**
     * Verifies that the quantity returned after several additions is correct.
     */
    @Test
    public void getQuantityShouldReturnCorrectQuantityAfterSeveralAdds() {
        inventory.addItem(baieRevigorante, 2);
        inventory.addItem(baieRevigorante, 3);
        assertEquals(5, inventory.getQuantity(baieRevigorante));
    }

    /**
     * Verifies that consuming an item decreases its quantity by one.
     *
     * @throws ItemNotAvailableException if the item cannot be consumed
     */
    @Test
    public void consumeItemShouldDecreaseQuantity() throws ItemNotAvailableException {
        inventory.addItem(baieRevigorante, 3);
        inventory.consumeItem(baieRevigorante);
        assertEquals(2, inventory.getQuantity(baieRevigorante));
    }


    /**
     * Verifies that consuming the last unit removes the item from the inventory.
     *
     * @throws ItemNotAvailableException if the item cannot be consumed
     */
    @Test
    public void consumeItemOnLastUnitShouldRemoveItemFromInventory() throws ItemNotAvailableException {
        inventory.addItem(baieRevigorante, 1);
        inventory.consumeItem(baieRevigorante);
        assertEquals(0, inventory.getQuantity(baieRevigorante));
        assertFalse(inventory.hasItem(baieRevigorante));
    }
}
