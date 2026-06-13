package views.shared;

import javafx.scene.control.Tooltip;
import views.BaseFXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import models.shared.Item;

/**
 * FXML controller for the inventory screen.
 *
 * <p>Handles JavaFX components declared in {@code InventoryView.fxml} and forwards
 * user actions to the {@link InventoryView.Listener}.</p>
 */
public class InventoryFXMLController extends BaseFXMLController<InventoryView.Listener> {

    @FXML private FlowPane itemsContainer;
    @FXML private HBox     selectedItemsBar;
    @FXML private Button   confirmButton;
    @FXML private Button   cancelButton;

    /**
     * Initializes the controller after FXML components have been loaded.
     * Wires the Confirm and Cancel buttons to the listener.
     */
    @FXML
    private void initialize() {
        this.confirmButton.setOnAction(e -> this.listener.onConfirm());
        this.cancelButton.setOnAction(e  -> this.listener.onCancel());
    }

    /**
     * Clears both the items grid and the selected-item bar.
     */
    public void clear() {
        this.itemsContainer.getChildren().clear();
        this.selectedItemsBar.getChildren().clear();
    }

    /**
     * Adds an item card (button) to the scrollable items grid.
     *
     * @param itemCard the button representing an inventory item
     */
    public void addCardToGrid(final Button itemCard) {
        this.itemsContainer.getChildren().add(itemCard);
    }

    /**
     * Enables or disables the confirm button.
     *
     * <p>Should be disabled when no item is selected to prevent confirming with no choice.</p>
     *
     * @param enabled {@code true} to enable, {@code false} to disable
     */
    public void setConfirmEnabled(final boolean enabled) {
        this.confirmButton.setDisable(!enabled);
    }


    /**
     * Creates and adds an item card button to the items grid.
     *
     * <p>The card displays the item name, quantity, and sprite. If the item is currently
     * selected, the card is highlighted. Clicking the card toggles selection through
     * the listener callbacks.</p>
     *
     * @param item the inventory item represented by the card
     * @param quantity the available quantity for the item
     * @param selectedItem {@code true} if this item is currently selected
     */
    public void addItemCard(final Item item, final int quantity, final boolean selectedItem){
        final Image image = new Image(
                this.getClass().getResource("/png/" + item.sprite()).toExternalForm()
        );
        final ImageView imageView = new ImageView(image);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);

        final Button itemCard = new Button(item.name() + " x" + quantity);
        itemCard.setPrefSize(200, 150);
        itemCard.setGraphic(imageView);

        // Hovering the item shows its description
        Tooltip tooltip = new Tooltip(item.description());
        tooltip.setShowDelay(javafx.util.Duration.millis(300));
        itemCard.setTooltip(tooltip);


        // Highlight the selected item
        if (selectedItem) {
            itemCard.getStyleClass().add("item-selected");
        }

        // Exclusive toggle: selecting a new item replaces the previous one
        itemCard.setOnAction(e -> {
            if (selectedItem) {
                this.listener.onItemDeselected();
            } else {
                this.listener.onItemSelected(item);
            }
        });

        this.addCardToGrid(itemCard);

    }
}
