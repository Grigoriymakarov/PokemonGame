package views;

import constants.UIConstants;
import controllers.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URL;

import static constants.ColorConstants.INFO;
import static constants.ColorConstants.WARNING;

/**
 * Abstract base class for all JavaFX views.
 *
 * <p>A view encapsulates the UI components defined in an FXML file and provides methods
 * to interact with them. It loads the FXML file, stores the root node, and retrieves the
 * associated FXML controller. It also provides utility methods for displaying alerts.</p>
 *
 * <p><strong>The {@code ConcreteView.Listener} interface should be coherent with the {@code ConcreteController} encapsulating the view.</strong></p>
 *
 * <p>The view should be used to do the "abstract"/"coordination" UI work (tracking if an item is selected,
 *  choosing to display a specific element on a specific place...) while more "dirty" work
 *  should be made in the {@link BaseView}. However, neither the view nor the FXML controller should not do any
 *  business logic, this part being delegated to the {@link BaseController}.</p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * public class CombatView extends BaseView{@literal <}CombatView.Listener, CombatFXMLController> {
 *
 *     /**
 *      * Listener interface for combat view events.
 *      * ...
 *      public static interface Listener extends ViewListener {
 *          ...
 *      }
 *
 *      public CombatView() throws IOException {
 *          this.loadFXML(PathConstants.FXML_COMBAT);
 *      }
 *      public void functionCalledByController() {
 *          ...
 *      }
 * }
 * </pre>
 *
 * @param <L>  the type of listener used to communicate with the MVC controller
 * @param <FC> the type of FXML controller associated with this view
 * @see BaseController
 * @see BaseFXMLController
 * @see ViewListener
 */
public abstract class BaseView<L extends BaseView.ViewListener, FC extends BaseFXMLController<L>> {

    /**
     * Base interface for view listeners.
     *
     * <p>The view listeners allow decoupling the {@link BaseController} owing the view
     * from the view itself, by defining an interface through which the view/fxmlcontroller
     * can notify the controller of user actions.</p>
     *
     * <p>Concrete views should define their own listener interfaces
     * (extending this one, for clarity, but without any effect since it's empty).
     * This allows the listener to be refered as {@code ConcreteView.Listener}</p>
     */
    public static interface ViewListener {
    }


    /**
     * Root node of the loaded FXML view.
     */
    protected Parent root;

    /**
     * FXML controller associated with the loaded FXML file.
     */
    protected FC fxmlController;

    /**
     * Listener used to notify the MVC controller of UI events.
     */
    protected L listener;

    /**
     * Loads the FXML file associated with this view.
     *
     * @param fxmlPath path to the FXML file
     */
    protected void loadFXML(final String fxmlPath) throws IOException {

        final URL url = this.getClass().getResource(fxmlPath);
        if (url == null) {
            throw new IllegalArgumentException("FXML file not found: " + fxmlPath);
        }

        final FXMLLoader loader = new FXMLLoader(url);
        this.root = loader.load();
        this.fxmlController = loader.getController();
        if  (this.fxmlController == null) {
            throw new IllegalStateException("Could not retrieve FXML Controller for " + this.getClass().getSimpleName() + ". Please make sure that the FXML controller is correctly declared in the file " + fxmlPath);
        }

    }
    /**
     * Returns the root node of the view.
     *
     * @return the root JavaFX node
     */
    public Parent getRoot() {
        return this.root;
    }


    /**
     * Returns the window (Stage) containing this view.
     * @return the stage window
     */
    public javafx.stage.Window getWindow() {
        return this.root.getScene().getWindow();
    }

    /**
     * Displays an information message.
     *
     * @param title the dialog title
     * @param header the dialog header text
     * @param message the message content to display
     */
    protected void showInfoMessage(final String title, final String header, final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        if (header != null) {
            alert.setHeaderText(header);
        }
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-border-color: " + INFO + "; -fx-border-width: 2;");
        alert.showAndWait();
    }

    /**
     * Displays an error message.
     *
     * @param message the error message content to display
     */
    protected void showErrorMessage(final String message) {
        final Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(UIConstants.ERROR_HEADER_DEFAULT);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-border-color: " + WARNING + "; -fx-border-width: 2; -fx-background-color: white;");
        alert.showAndWait();
    }

    /**
     * Sets the listener used by this view.
     *
     * @param listener listener to register
     */
    public void setListener(final L listener) {
        this.listener = listener;
        this.fxmlController.setListener(listener);
    }
}