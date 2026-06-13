package repositories;

import models.exceptions.ElementNotFoundException;
import models.shared.Item;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link ItemRepository} class.
 *
 * <p>This test class verifies item database loading,
 * item lookup, and first loadout creation.</p>
 */
public class ItemRepositoryTest {

    private ItemRepository repository;

    /**
     * Builds the item repository data before each test.
     *
     * @throws IOException if item data cannot be loaded
     */
    @Before
    public void setUp() throws IOException {
        this.repository = new ItemRepository();
        this.repository.buildDataBase();
    }

    /**
     * Verifies that building the database loads at least one item.
     */
    @Test
    public void buildDataBase_loadsAtLeastOneItem() {
        List<Item> items = this.repository.getList();
        assertFalse("The item list should not be empty", items.isEmpty());
    }

    /**
     * Verifies that finding an existing item by id returns the matching item.
     */
    @Test
    public void findById_existingId_returnsCorrectItem() {
        Item first = this.repository.getList().get(0);
        Item found = null;
        try {
            found = this.repository.findById(first.id());
        } catch (ElementNotFoundException e) {
            fail("findById should return an item for a valid id");
        }
        assertEquals("The returned item should have the expected id", first.id(), found.id());
    }

    /**
     * Verifies that finding an unknown item id throws an exception.
     */
    @Test
    public void findById_nonExistingId() {
        assertThrows(ElementNotFoundException.class, () -> this.repository.findById("id_inexistant"));
    }

    /**
     * Verifies that every item in the first loadout is non-null.
     */
    @Test
    public void getFirstLoadOut_allItemsAreNonNull() throws ElementNotFoundException {
        Map<Item, Integer> loadOut = this.repository.getFirstLoadOut();
        for (Item item : loadOut.keySet()) {
            assertNotNull("Each first loadout item should be non-null", item);
        }
    }

    /**
     * Verifies that the first loadout contains the expected total number of items.
     */
    @Test
    public void getFirstLoadOut_matchesEnonce() throws ElementNotFoundException {
        Map<Item, Integer> loadOut = this.repository.getFirstLoadOut();

        int totalItems = loadOut.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals("The starting inventory should contain 7 items in total", 7, totalItems);
    }

    /**
     * Verifies that retrieving the first loadout twice returns distinct map instances.
     */
    @Test
    public void getFirstLoadOut_calledTwice_returnsDifferentMapInstances() throws ElementNotFoundException {
        Map<Item, Integer> first  = this.repository.getFirstLoadOut();
        Map<Item, Integer> second = this.repository.getFirstLoadOut();
        assertNotSame("Two calls should return distinct map instances", first, second);
    }
}
