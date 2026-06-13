package views.shared;

import views.BaseFXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * FXML controller for the home screen.
 *
 * <p>This controller manages the UI components of the home screen (buttons)
 * and relays user interactions to the {@link HomeScreenView.Listener} interface.</p>
 *
 * <p>It inherits from {@link BaseFXMLController} and uses the generic type
 * {@link HomeScreenView.Listener} to ensure type-safe listener callbacks.</p>
 *
 * <p>All button clicks are wired to corresponding listener methods during
 * initialization via the {@link #initialize()} method.</p>
 *
 * @see HomeScreenView.Listener
 * @see HomeScreenView
 * @see BaseFXMLController
 */
public class HomeScreenFXMLController extends BaseFXMLController<HomeScreenView.Listener> {

    /**
     * Button to start a combat session.
     * Linked to {@link HomeScreenView.Listener#onStartCombat()}.
     */
    @FXML private Button startCombatButton;

    /**
     * Button to start the tower mode.
     * Linked to {@link HomeScreenView.Listener#onStartTower()}.
     */
    @FXML private Button startTowerButton;

    /**
     * Button to manage saved teams.
     * Linked to {@link HomeScreenView.Listener#onManageTeams()}.
     */
    @FXML private Button manageTeamsButton;

    /**
     * Button to leave and exit the game.
     * Linked to {@link HomeScreenView.Listener#onLeaveGame()}.
     */
    @FXML private Button leave;

    /**
     * Initializes the controller after FXML components have been loaded.
     *
     * <p>This method is called automatically by JavaFX after all {@code @FXML}
     * annotated fields have been injected. It wires up the button click handlers
     * to corresponding listener methods:</p>
     * <ul>
     *   <li>{@code startCombatButton} -> {@link HomeScreenView.Listener#onStartCombat()}</li>
     *   <li>{@code startTowerButton} -> {@link HomeScreenView.Listener#onStartTower()}</li>
     *   <li>{@code manageTeamsButton} -> {@link HomeScreenView.Listener#onManageTeams()}</li>
     *   <li>{@code leave} -> {@link HomeScreenView.Listener#onLeaveGame()}</li>
     * </ul>
     *
     * <p>If the listener is not set before a button is clicked, a {@code NullPointerException}
     * may occur. The listener should be injected via {@link #setListener(HomeScreenView.Listener)}
     * before user interaction.</p>
     */
    @FXML
    private void initialize() {
        this.startCombatButton.setOnAction(e -> this.listener.onStartCombat());
        this.startTowerButton.setOnAction(e -> this.listener.onStartTower());
        this.manageTeamsButton.setOnAction(e -> this.listener.onManageTeams());
        this.leave.setOnAction(e -> this.listener.onLeaveGame());
    }
}

