package views.team;

import javafx.scene.control.Button;
import views.BaseView;

import java.io.IOException;

/**
 * View for the team management screen.
 *
 * <p>This class manages the UI for viewing and selecting previously saved teams.
 * Users can browse a list of saved teams and select one to load or manage.</p>
 *
 * <p>It inherits from {@link BaseView} and works with {@link Listener}
 * (for event callbacks) and {@link TeamsManageFXMLController} (for FXML components).
 * The main display method is {@link #displayTeams(Iterable)}, which renders a clickable
 * list of saved team names.</p>
 *
 * @see Listener
 * @see TeamsManageFXMLController
 * @see BaseView
 */
public class TeamsManageView extends BaseView<TeamsManageView.Listener, TeamsManageFXMLController> {

    /**
     * Listener interface for handling user interactions in the team management screen.
     * <p>
     * Implementations of this interface define the actions triggered when the user
     * creates a new team, navigates back to the home screen, or selects an existing
     * team from the list.
     * </p>
     */
    public static interface Listener extends ViewListener {
        /**
         * Called when the user chooses to create a new team.
         */
        void onNewTeam();
        /**
         * Called when the user chooses to return to the previous screen.
         */
        void onBack();
        /**
         * Called when the user selects a saved team from the list.
         *
         * @param teamName the name of the selected team
         */
        void onTeamSelected(String teamName);
    }


    /**
     * Creates a new team management view by loading its FXML layout.
     *
     * <p>This constructor loads the FXML file {@code TeamsManage.fxml} from the resources,
     * initializes the root node, and retrieves the associated FXML controller
     * ({@link TeamsManageFXMLController}).</p>
     *
     * @throws IOException if the FXML file cannot be found or loaded
     */
    public TeamsManageView() throws IOException {
        this.loadFXML(constants.PathConstants.FXML_TEAMS_MANAGE);
    }

    /**
     * Displays the list of saved team names as clickable buttons.
     *
     * <p>This method dynamically generates a button for each saved team name.
     * Each button is linked to the {@link Listener#onTeamSelected(String)}
     * callback.</p>
     *
     * @param teamNames the list of saved team names to display
     */
    public void displayTeams(final Iterable<String> teamNames) {
        this.fxmlController.clear();
        for (final String name : teamNames) {
            final Button teamButton = new Button(name);
            teamButton.setPrefWidth(440);
            teamButton.setPrefHeight(40);
            teamButton.getStyleClass().add("teams-list-btn");
            teamButton.setOnAction(e -> this.listener.onTeamSelected(name));
            this.fxmlController.addTeamToList(teamButton);
        }
    }
    /**
     * Displays an error message specifically related to team management tasks.
     * * <p>This can be used to notify the user if a team fails to load or
     * if there is a problem accessing the saved data.</p>
     *
     * @param message the descriptive error message to show
     */
     public void showHomescreenTeamManageError(final String message) {
         this.showErrorMessage(message);
     }
}

