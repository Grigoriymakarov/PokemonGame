package views.shared;

import views.BaseView;

import java.io.IOException;

/**
 * View for the home screen of the application.
 *
 * <p>This class represents the home screen UI and inherits from the abstract {@link BaseView}
 * class to manage FXML loading and listener registration.</p>
 *
 * <p>It loads its layout from {@code HomeScreen.fxml} and provides a bridge between
 * the {@link Listener} (events) and the UI components managed by
 * {@link HomeScreenFXMLController}.</p>
 *
 * @see Listener
 * @see HomeScreenFXMLController
 * @see BaseView
 */
public class HomeScreenView extends BaseView<HomeScreenView.Listener, HomeScreenFXMLController> {

    /**
     * Listener interface for handling user interactions on the home screen.
     * <p>
     * Implementations of this interface define the actions triggered by the user
     * when interacting with the Bugemon home screen, such as starting a combat,
     * entering tower mode, managing teams, or leaving the game.
     * </p>
     */
    public static interface Listener extends ViewListener {
        /**
         * Called when the user chooses to start a combat.
         */
        void onStartCombat();
        /**
         * Called when the user chooses to start the tower mode.
         */
        void onStartTower();
        /**
         * Called when the user chooses to manage their teams.
         */
        void onManageTeams();
        /**
         * Called when the user chooses to exit the game.
         */
        void onLeaveGame();
    }


    /**
     * Creates a new home screen view by loading its FXML layout.
     *
     * <p>This constructor loads the FXML file {@code HomeScreen.fxml} from the resources,
     * initializes the root node, and retrieves the associated FXML controller
     * ({@link HomeScreenFXMLController}).</p>
     *
     * @throws IOException if the FXML file cannot be found or loaded
     */
    public HomeScreenView() throws IOException {
        this.loadFXML(constants.PathConstants.FXML_HOME);
    }
}