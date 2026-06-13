package views.shared;

import dto.InventoryDTO;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.shared.Item;
import views.BaseView;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * Inventory view used during combat.
 *
 * <p>This class manages the UI that lets the player select one item to use on the
 * active Bugemon. Selection is exclusive, so only one item can be selected at a time.
 * The player can change the selection before confirming. Confirming consumes the turn.</p>
 *
 * <p>It extends {@link BaseView} and collaborates with {@link Listener}
 * (event callbacks) and {@link InventoryFXMLController} (FXML component handling).</p>
 *
 * @see Listener
 * @see InventoryFXMLController
 * @see BaseView
 */
public class InventoryView extends BaseView<InventoryView.Listener, InventoryFXMLController> {

    /**
     * Listener interface for inventory screen events.
     *
     * <p>Implemented by the controller that handles inventory interactions during combat.
     * The inventory allows the player to select a single item before confirming its use,
     * which consumes their turn.</p>
     */
    public static interface Listener extends ViewListener {

        /**
         * Called when the player selects an item.
         *
         * <p>Replaces any previously selected item — only one item can be selected at a time.</p>
         *
         * @param item the item the player selected
         */
        void onItemSelected(Item item);

        /**
         * Called when the player deselects the currently selected item.
         */
        void onItemDeselected();

        /**
         * Called when the player confirms their item choice.
         *
         * <p>The implementing controller is responsible for applying the item's effect,
         * calling {@link models.shared.Inventory#consumeItem(Item)}, and closing the view.
         * Confirming consumes the player's turn.</p>
         */
        void onConfirm();

        /**
         * Called when the player cancels without using an item.
         *
         * <p>No item is consumed and the turn is not spent.</p>
         */
        void onCancel();
    }


    private Stage popup;

    /**
     * Creates a new inventory view by loading its FXML layout.
     *
     * @throws IOException if the FXML file cannot be found or loaded
     */
    public InventoryView() throws IOException {
        this.loadFXML(constants.PathConstants.FXML_INVENTORY);
    }

    /**
     * Displays all available items and highlights the current selection.
     *
     * <p>Each card shows the item image, name, and quantity. The confirm button is enabled
     * only when an item is selected.</p>
     *
     * <p>Items with quantity {@code 0} are not shown because
     * {@link InventoryDTO#getAvailableItems()} returns only available entries.</p>
     *
     * @param inventory the player's inventory to display
     * @param selectedItem the currently selected item, if any
     */
    public void displayInventory(final InventoryDTO inventory, final Optional<Item> selectedItem) {
        this.fxmlController.clear();
        this.fxmlController.setConfirmEnabled(selectedItem.isPresent());

        // Render item grid
        for (final Item item : inventory.getAvailableItems()) {
            final int quantity = inventory.getQuantity(item);
            final boolean isSelected = selectedItem.isPresent() && item.equals(selectedItem.get());
            this.fxmlController.addItemCard(item, quantity, isSelected);
        }
    }

    /**
     * Opens the inventory popup as a modal dialog.
     *
     * <p>The popup is initialized with no selected item and blocks the owner window
     * until the dialog is closed.</p>
     *
     * @param inventory the inventory to display
     * @param owner the parent window that owns the modal dialog
     */
    public void showPopup(final InventoryDTO inventory, final Window owner) {
        this.displayInventory(inventory, Optional.empty());
        final Scene popupScene = new Scene(this.root, 420, 350);
        final URL css = getClass().getResource("/css/styles.css");
        if (css != null) {
            popupScene.getStylesheets().add(css.toExternalForm());
        }
        this.popup = new Stage();
        this.popup.setTitle("choisir un objet");
        this.popup.initModality(Modality.WINDOW_MODAL);
        this.popup.initOwner(owner);
        this.popup.setScene(popupScene);
        this.popup.showAndWait();
    }

    /**
     * Closes the inventory popup dialog.
     */
    public void closePopup() {
        this.popup.close();
    }
}
