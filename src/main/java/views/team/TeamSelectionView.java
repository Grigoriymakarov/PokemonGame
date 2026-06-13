package views.team;

import controllers.shared.MetaController;
import controllers.team.TeamSelectionController;
import dto.TeamDTO;
import javafx.scene.control.TextInputDialog;
import models.shared.bugemon.BugemonSpecie;
import dto.TrainerBugemonDTO;
import models.team.Team;
import services.TeamService;
import views.BaseView;

import java.io.IOException;
import java.util.Optional;

/**
 * View for the team selection screen.
 *
 * <p>This class manages the UI for team composition, allowing users to select
 * and deselect Bugemon from the available roster and assemble their team.</p>
 *
 * <p>It inherits from {@link BaseView} and works with {@link Listener}
 * (for event callbacks) and {@link TeamSelectionFXMLController} (for FXML components).
 * The main display method is {@link #displaySelectionScreen(Iterable, TeamDTO)}, which renders
 * all available Bugemon and the currently selected team members.</p>
 *
 * @see Listener
 * @see TeamSelectionFXMLController
 * @see BaseView
 */
public class TeamSelectionView extends BaseView<TeamSelectionView.Listener, TeamSelectionFXMLController> {

    /**
     * Listener interface for team selection screen events.
     *
     * <p>This interface defines the contract for all user interactions on the team
     * selection screen. The concrete implementation is provided by
     * {@link TeamSelectionController}, which handles the business logic
     * corresponding to each user action.</p>
     *
     * <p>The interface extends {@link ViewListener} to integrate with the framework's
     * view/listener architecture.</p>
     *
     * @see TeamSelectionController
     * @see TeamSelectionView
     */
    public static interface Listener extends ViewListener {

        /**
         * Called when the user confirms their team selection and is ready to start combat.
         *
         * <p>The controller should validate that the team is valid (contains at least one Bugemon)
         * and trigger the start of combat via {@link MetaController#startCombat(Team)}.</p>
         */
        void onConfirmTeam();

        /**
         * Called when the user deselects a Bugemon from their team.
         *
         * <p>The controller should remove the specified Bugemon from the active team
         * and refresh the UI to reflect the change.</p>
         *
         * @param bugemon the Bugemon species being deselected (must not be {@code null})
         */
        void onBugemonDeselected(BugemonSpecie bugemon);

        /**
         * Called when the user selects a Bugemon to add to their team.
         *
         * <p>The controller should add the specified Bugemon to the active team if possible
         * (respecting constraints like maximum team size and duplicate prevention)
         * and refresh the UI accordingly.</p>
         *
         * @param bugemon the Bugemon species being selected (must not be {@code null})
         */
        void onBugemonSelected(BugemonSpecie bugemon);

        /**
         * Called when the user saves their current team composition.
         *
         * <p>The controller should validate the team, prompt for a team name via the view,
         * and persist the team via {@link TeamService#saveCurrentTeam(String)}.</p>
         */
        void onSave();

        /**
         * Called when the user wants to load a previously saved team.
         *
         * <p>The controller should navigate to the team management screen
         * via {@link MetaController#showTeamManagement(TeamService)}
         * to allow the user to choose and load a saved team.</p>
         */
        void onLoad();
    }


    /**
     * Creates a new team selection view by loading its FXML layout.
     *
     * <p>This constructor loads the FXML file {@code TeamSelection.fxml} from the resources,
     * initializes the root node, and retrieves the associated FXML controller
     * ({@link TeamSelectionFXMLController}).</p>
     *
     * @throws IOException if the FXML file cannot be found or loaded
     */
    public TeamSelectionView() throws IOException {
        this.loadFXML(constants.PathConstants.FXML_TEAM_SELECTION);
    }

    /**
     * Displays the team selection screen with all available Bugemon and current team.
     *
     * <p>This method dynamically generates UI elements for each available Bugemon species,
     * creating clickable cards with images and their current level (if in team).
     * Cards are highlighted if already selected. Clicking a card toggles selection/deselection via the listener.</p>
     *
     * <p>Each Bugemon card displays:</p>
     * <ul>
     *     <li>The Bugemon name and current level (e.g., "Bugachu (Lv.5)") if already in the team</li>
     *     <li>A tooltip showing base stats (HP, Attack, Defense, Initiative)</li>
     *     <li>If already in the team: additional stats display showing current stats with applied modifiers
     *         and XP progression (e.g., "XP: 45/100 to next level")</li>
     * </ul>
     *
     * <p>Additionally, it renders the currently selected team members in a horizontal bar,
     * each displaying their name, current level, and a detailed tooltip with:</p>
     * <ul>
     *     <li>Current level</li>
     *     <li>Maximum HP (with stat modifiers applied)</li>
     *     <li>Attack, Defense, Initiative (with modifiers)</li>
     *     <li>XP progression to next level</li>
     * </ul>
     * <p>Clicking a team member removes them from the team.</p>
     *
     * <p>The method clears the previous display before rendering new content.</p>
     *
     * @param allSpecies the complete list of available Bugemon species to display
     * @param currentTeam the list of currently selected Bugemon for level display, highlighting, and team bar display
     */
    public void displaySelectionScreen(final Iterable<BugemonSpecie> allSpecies, final TeamDTO currentTeam) {
        this.fxmlController.clear();
        for (final BugemonSpecie bugemon : allSpecies) {
            this.fxmlController.addCardToGrid(
                    this.fxmlController.createBugemonCard(bugemon, currentTeam)
            );
        }
        for (final TrainerBugemonDTO member : currentTeam.getMembers()) {
            this.fxmlController.addMemberToBar(
                    this.fxmlController.createTeamMemberSlot(member)
            );
        }
    }
    /**
     * Displays an error message dialog to the user.
     *
     * <p>Shows a modal error dialog with the given message. Used to inform the user
     * of invalid operations (e.g., team too small, duplicate name, etc.).</p>
     *
     * @param message the error message to display
     */
    public void showSelectionError(final String message) {
        this.showErrorMessage(message);
    }

    /**
     * Displays a success message confirming the team was saved.
     *
     * <p>Shows a modal info dialog indicating the team has been successfully saved
     * to persistent storage.</p>
     */
    public void showSaveSuccess() {
        this.showInfoMessage("Équipe sauvegardée", null, "Votre équipe a été sauvegardée avec succès");
    }

    /**
     * Displays a dialog asking the user to enter a name for the team to save.
     *
     * <p>This method opens a {@link TextInputDialog} where the user can type
     * a name for the current team. It is used by the controller during the
     * save process.</p>
     *
     * <p>The method returns an {@link Optional} to handle the case where the
     * user cancels the dialog.</p>
     *
     * @return an {@link Optional} containing the entered team name,
     *         or {@code Optional.empty()} if the user cancels the dialog
     */
    public Optional<String> getTeamName() {
        final TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sauvegarder l'équipe");
        dialog.setHeaderText("Nom de l'équipe");
        return dialog.showAndWait();
    }

    /**
     * Enables edit mode by hiding certain buttons.
     *
     * <p>When the view is in edit mode (e.g., editing an existing team),
     * the "Load" and "Confirm" buttons are hidden to prevent conflicting actions.
     * Only "Save" button remains visible.</p>
     */
    public void setEditMode() {
        this.fxmlController.hideLoadAndConfirmButtons();
    }
}

