package views.team;

import views.BaseFXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
/**
 * JavaFX controller for the Teams Management screen.
 * <p>
 * This class handles the UI logic defined in the corresponding FXML file.
 * It delegates user actions to a {@link TeamsManageView.Listener} and provides
 * helper methods to update the displayed list of teams.
 * </p>
 */
public class TeamsManageFXMLController extends BaseFXMLController<TeamsManageView.Listener> {

    @FXML private VBox teamList;
    @FXML private Button newTeamButton;
    @FXML private Button backButton;
    /**
     * Initializes the UI components after the FXML has been loaded.
     * <p>
     * Sets up event handlers for the "New Team" and "Back" buttons,
     * delegating actions to the listener.
     * </p>
     */
    @FXML
    private void initialize() {
        this.newTeamButton.setOnAction(e -> this.listener.onNewTeam());
        this.backButton.setOnAction(e -> this.listener.onBack());
    }
    /**
     * Clears all team entries from the displayed list.
     * <p>
     * Typically called before repopulating the list with updated data.
     * </p>
     */
    public void clear() {
        this.teamList.getChildren().clear();
    }
    /**
     * Adds a team button to the list of displayed teams.
     *
     * @param teamButton the button representing a saved team
     */
    public void addTeamToList(final Button teamButton) {
        this.teamList.getChildren().add(teamButton);
    }
}