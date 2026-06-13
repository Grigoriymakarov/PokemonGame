package services;

import models.exceptions.ElementNotFoundException;
import models.exceptions.ItemNotFoundException;
import models.shared.Inventory;
import models.shared.Item;
import models.shared.trainer.Trainer;
import repositories.ItemRepository;

import java.util.Map;

/**
 * Service responsible for loading and updating a trainer inventory.
 *
 * <p>This service retrieves item definitions from {@link ItemRepository} and applies
 * them to the {@link Inventory} owned by a {@link Trainer}.</p>
 */
public class InventoryService {

    private final Trainer trainer;
    private final ItemRepository repository;

    /**
     * Creates a service bound to a trainer.
     *
     * @param trainer    the trainer whose inventory will be managed
     * @param repository the item repository used to resolve and load items
     */
    public InventoryService(final Trainer trainer, final ItemRepository repository) {
        this.trainer = trainer;
        this.repository = repository;
    }

    /**
     * Loads the default starting inventory for the trainer.
     *
     * <p>Current inventory contents are cleared first, then the repository
     * loadout is copied into the trainer inventory.</p>
     */
    public void startInventoryLoad() throws ItemNotFoundException {
        this.trainer.clearInventory();
        final Map<Item, Integer> startingInventory;
        try {
            startingInventory = this.repository.getFirstLoadOut();
            for (final Map.Entry<Item, Integer> entry : startingInventory.entrySet()) {
                this.trainer.addItemToInventory(entry.getKey(), entry.getValue());
            }
        } catch (ElementNotFoundException e) {
            throw new ItemNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * Adds a quantity of an item to the trainer inventory using an item id.
     *
     * @param id       the item identifier used to query the repository
     * @param quantity the quantity to add
     */
    public void inventoryLoad(final String id, final int quantity) throws ItemNotFoundException {
        try {
            final Item item = this.repository.findById(id);
            this.trainer.addItemToInventory(item, quantity);
        } catch (final ElementNotFoundException e) {
            throw new ItemNotFoundException("Cannot load non-existing item (ID: " + id + ") into inventory", e);
        }
    }
}
