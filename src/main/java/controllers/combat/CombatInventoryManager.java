package controllers.combat;

import constants.MessageConstants;
import models.shared.Inventory;
import models.shared.Item;
import models.shared.trainer.Trainer;
import models.exceptions.ItemNotAvailableException;
import services.combat.CombatService;
import views.combat.CombatView;
import views.shared.InventoryView;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the inventory popup flow during combat.
 *
 * <p>This small component owns the popup view, the current item selection and the
 * confirmation/cancel callbacks so that {@link CombatController} can stay focused on the
 * combat turn flow.</p>
 */
final class CombatInventoryManager implements InventoryView.Listener {
    private static final Logger LOGGER = Logger.getLogger(CombatInventoryManager.class.getName());
    private final CombatView combatView;
    private final Trainer trainer;
    private final CombatService combatService;
    private final Runnable onItemConfirmed;

    private Optional<InventoryView> inventoryView;
    private Optional<Item> selectedItem;

    CombatInventoryManager(final CombatView combatView, final Trainer trainer, final CombatService combatService, final Runnable onItemConfirmed) {
        this.combatView = combatView;
        this.trainer = trainer;
        this.combatService = combatService;
        this.onItemConfirmed = onItemConfirmed;
    }

    void openInventory() {
        final Inventory inventory = this.trainer.getInventory();
        if (inventory.isEmpty()) {
            this.combatView.showInventoryError(MessageConstants.ERROR_NO_ITEMS);
            return;
        }

        try {
            this.selectedItem = Optional.empty();
            this.inventoryView = Optional.of(new InventoryView());
            this.inventoryView.get().setListener(this);
            final javafx.stage.Stage combatStage = (javafx.stage.Stage) this.combatView.getWindow();
            this.inventoryView.get().showPopup(inventory, combatStage);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, MessageConstants.ERROR_LOAD_INVENTORY, e);
            this.combatView.showInventoryError(MessageConstants.ERROR_LOAD_INVENTORY);
        }
    }

    @Override
    public void onItemSelected(final Item item) {
        this.selectedItem = Optional.of(item);
        this.refreshInventoryDisplay();
    }

    @Override
    public void onItemDeselected() {
        this.selectedItem = Optional.empty();
        this.refreshInventoryDisplay();
    }

    @Override
    public void onConfirm() {
        if (this.selectedItem.isEmpty()) {
            this.closeInventoryPopup();
            return;
        }

        final Item item = this.selectedItem.get();

        try {
            //deletes the consumed item from the players inventory
            this.trainer.consumeItemFromInventory(item);
        } catch (final ItemNotAvailableException e) {
            LOGGER.log(Level.SEVERE, "Item consumption failed", e);
            throw new IllegalStateException("User tried to use an item that was not available in its inventory. This should not happen since the UI should prevent it.", e);
        }
        this.combatService.useItem(item);
        this.selectedItem = Optional.empty();
        this.closeInventoryPopup();
        this.onItemConfirmed.run();
    }

    @Override
    public void onCancel() {
        this.selectedItem = Optional.empty();
        this.closeInventoryPopup();
    }

    private void refreshInventoryDisplay() {
        this.inventoryView.ifPresent(
                view -> view.displayInventory(this.trainer.getInventory(), this.selectedItem)
        );
    }

    private void closeInventoryPopup() {
        if (this.inventoryView.isPresent()) {
            this.inventoryView.get().closePopup();
            this.inventoryView = Optional.empty();
        }
    }
}

