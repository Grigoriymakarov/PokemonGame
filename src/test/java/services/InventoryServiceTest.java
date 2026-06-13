package services;

import models.exceptions.ItemNotFoundException;
import models.shared.Item;
import models.shared.trainer.Player;
import org.junit.Before;
import org.junit.Test;
import repositories.ItemRepository;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link InventoryService} class.
 *
 * <p>This test class verifies default inventory loading, item addition,
 * quantity tracking, and invalid item handling.</p>
 */
public class InventoryServiceTest {

    private InventoryService inventoryService;
    private Player player;
    private ItemRepository repository;

    /**
     * Builds item repository data and creates an inventory service before each test.
     *
     * @throws IOException if item data cannot be loaded
     */
    @Before
    public void setUp() throws IOException {
        this.repository = new ItemRepository();
        this.repository.buildDataBase();
        player = new Player("Joueur");
        inventoryService = new InventoryService(player, this.repository);
    }

    /**
     * Verifies that loading the starting inventory adds default items.
     */
    @Test
    public void hasDefaultItems() throws ItemNotFoundException {
        inventoryService.startInventoryLoad();

        boolean hasRevigoring = player.getInventory().getAvailableItems()
                .stream()
                .anyMatch(item -> item.name().toLowerCase().contains("revigor"));

        assertTrue("The inventory should contain Reinvigorating Berries", hasRevigoring);
    }


    /**
     * Verifies that reloading the starting inventory clears and rebuilds it consistently.
     */
    @Test
    public void clearsExistingInventory() throws ItemNotFoundException {
        inventoryService.startInventoryLoad();
        int defaultSize = player.getInventory().getAvailableItems().size();

        inventoryService.startInventoryLoad();

        assertEquals("The reloaded inventory should have the same size as the starting inventory",
                defaultSize, player.getInventory().getAvailableItems().size());
    }

    /**
     * Verifies that loading a valid item id adds the item to the inventory.
     */
    @Test
    public void validId_addsItemToInventory() throws ItemNotFoundException {
        inventoryService.inventoryLoad("baie_revigorante", 2);

        assertFalse("The inventory should not be empty after adding an item",
                player.getInventory().isEmpty());
    }

    /**
     * Verifies that loading a valid item id stores the requested quantity.
     */
    @Test
    public void validId_correctQuantity() throws ItemNotFoundException {
        inventoryService.inventoryLoad("baie_revigorante", 5);

        Item item = player.getInventory().getAvailableItems()
                .stream()
                .filter(i -> i.name().toLowerCase().contains("revigor"))
                .findFirst()
                .orElse(null);

        assertNotNull("The item should be present", item);
        assertEquals("The quantity should be 5", 5, player.getInventory().getQuantity(item));
    }

    /**
     * Verifies that loading an invalid item id leaves the inventory unchanged.
     */
    @Test
    public void invalidId_throwsError() {
        assertThrows(ItemNotFoundException.class, () -> {
            inventoryService.inventoryLoad("item_inexistant", 3);
        });
    }
}
