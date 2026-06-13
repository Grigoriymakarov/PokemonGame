package views;

import controllers.BaseController;

/**
 * Generic abstract base class for all FXML controllers.
 *
 * <p>An FXML controller is responsible for handling JavaFX UI components
 * declared in an FXML file. It can communicate user actions to the MVC
 * {@link BaseController} through a {@link BaseView.ViewListener}.</p>
 *
 * <p><strong>It needs to be declared in the FXML file.</strong></p>
 *
 * <p>The FXML controller should be used to do the "dirty" UI work (generating the buttons,
 * resizing components, changing colors...) while more "abstract"/"coordination" UI work
 * should be made in the {@link BaseView}.</p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * public class InventoryFXMLController extends BaseFXMLController<InventoryView.Listener> {
 *
 *    {@literal @}FXML private FlowPane itemsContainer;
 *    {@literal @}FXML private HBox     selectedItemsBar;
 *    {@literal @}FXML private Button   confirmButton;
 *    {@literal @}FXML private Button   cancelButton;
 *
 *     /** Initializes the controller after FXML components have been loaded.
 *      * ...
 *    {@literal @}FXML
 *     private void initialize() {
 *         this.confirmButton.setOnAction(e -> this.listener.onConfirm());
 *         this.cancelButton.setOnAction(e  -> this.listener.onCancel());
 *     }
 *
 *     public void clear() {
 *         this.itemsContainer.getChildren().clear();
 *         this.selectedItemsBar.getChildren().clear();
 *     }
 *     ...
 *  }
 * </pre>
 *
 * @param <L> the type of listener used to communicate with the MVC controller
 * @see BaseView
 * @see BaseView.ViewListener
 */
public abstract class BaseFXMLController<L extends BaseView.ViewListener> {

    /**
     * Listener used to notify the MVC controller of UI events.
     */
    protected L listener;

    /**
     * Sets the listener used by this FXML controller.
     *
     * @param listener the listener to register
     */
    public void setListener(final L listener) {
        this.listener = listener;
    }
}