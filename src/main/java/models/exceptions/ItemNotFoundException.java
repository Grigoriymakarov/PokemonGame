package models.exceptions;

/**
 * Exception thrown when an item with a specific ID cannot be found in the repository.
 *
 * <p>This exception is raised when {@link repositories.ItemRepository} or {@link services.InventoryService}
 * attempt to access an item that does not exist.</p>
 *
 * @see repositories.ItemRepository
 * @see services.InventoryService
 */
public class ItemNotFoundException extends ElementNotFoundException {
    public ItemNotFoundException(String message) {
        super(message);
    }
    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
