package controllers.team;

import constants.MessageConstants;
import controllers.BaseController;
import controllers.shared.MetaController;
import controllers.shared.Windows;
import javafx.stage.Stage;
import models.exceptions.BugemonNotFoundException;
import models.exceptions.TeamNotFoundException;
import services.TeamService;
import views.team.TeamsManageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
/**
 * Controller responsible for managing the list of saved teams.
 * <p>
 * This controller handles user interactions related to viewing, selecting,
 * creating, and loading teams. It communicates with the {@link TeamsManageView}
 * to update the UI and with the {@link TeamService} to perform operations on
 * saved teams.
 * </p>
 * <p>
 * Instances of this controller are created by the {@link MetaController}.
 * </p>
 */
public class TeamsManageController extends BaseController<TeamsManageView.Listener, TeamsManageView> implements TeamsManageView.Listener {

    private final TeamService teamService;
    /**
     * Creates a new TeamsManageController.
     * <p>
     * Initializes the controller with the provided {@link TeamService} and
     * immediately refreshes the view to display the list of saved teams.
     * </p>
     *
     * @param metaController the main controller responsible for navigation
     * @param view the view associated with team management
     * @param teamService the service used to load and manage saved teams
     */
    public TeamsManageController(final MetaController metaController, final TeamsManageView view, final TeamService teamService) {
        super(metaController, view);
        this.teamService = teamService;
        this.refreshView();
    }
    /**
     * Updates the view with the current list of saved teams.
     * <p>
     * Retrieves all saved team names from the {@link TeamService} and displays
     * them in the view. If an error occurs while loading the data, it is
     * currently logged or handled through future error reporting UI.
     * </p>
     */
    private void refreshView() {
        try {
            final List<String> teamNames = this.teamService.getAllSavedTeamNames();
            this.view.displayTeams(teamNames);
        } catch (final IOException e) {
            this.view.showHomescreenTeamManageError(MessageConstants.ERROR_LOAD_TEAMS);
        }
    }
    /**
     * Called when the user chooses to create a new team.
     * <p>
     * Navigates to the team selection screen in edit mode, allowing the user
     * to build a new team from scratch.
     * </p>
     */
    @Override
    public void onNewTeam() {
        this.metaController.switchTo(Windows.TEAM_SELECT_EDITMODE);
    }
    /**
     * Called when the user chooses to return to the home screen.
     */
    @Override
    public void onBack() {
        final Windows previous = this.metaController.getPreviousWindow()
                .orElse(Windows.HOMESCREEN);

        if (previous == Windows.TEAM_SELECT_TOWER || previous == Windows.TEAM_SELECT) {
            this.metaController.switchTo(previous);
        } else {
            // Default/fallback behavior (includes HOMESCREEN and null): go home.
            this.metaController.switchTo(Windows.HOMESCREEN);
        }
    }
    /**
     * Displays the team management screen in the main application window.
     * <p>
     * Sets a window size appropriate for listing and selecting teams.
     * </p>
     *
     * @param stage the JavaFX stage in which the view should be displayed
     */
    @Override
    public void showView(final Stage stage) throws FileNotFoundException {
        this.showView(stage, 600, 400);
    }
    /**
     * Called when the user selects a saved team from the list.
     * <p>
     * Loads the selected team using the {@link TeamService} and immediately
     * starts a combat with that team. If loading fails, the error is caught
     * for future UI feedback implementation.
     * </p>
     *
     * @param teamName the name of the team selected by the user
     */
    @Override
    public void onTeamSelected(final String teamName) {
        final Windows previousWindow = this.metaController.getPreviousWindow()
                .orElse(Windows.HOMESCREEN);
        if (previousWindow == Windows.HOMESCREEN) {
            this.view.showHomescreenTeamManageError(MessageConstants.ERROR_NO_MODE_SELECTED);
        } else {
            try {
                this.teamService.loadSavedTeam(teamName);
                if (previousWindow == Windows.TEAM_SELECT_TOWER) {
                    this.metaController.startTower(this.teamService.getTeam());
                } else {
                    this.metaController.startCombat(this.teamService.getTeam());
                }
            } catch (final IOException e) {
                this.view.showHomescreenTeamManageError(MessageConstants.ERROR_LOAD_TEAM);
            } catch (final TeamNotFoundException e) {
                this.view.showHomescreenTeamManageError(MessageConstants.ERROR_LOAD_TEAM);
            } catch (final BugemonNotFoundException e) {
                this.LOGGER.log(java.util.logging.Level.SEVERE, "A bugemon of the team \"" + teamName + "\" could not be loaded", e);
                this.view.showHomescreenTeamManageError(MessageConstants.ERROR_LOAD_TEAM + ". " + MessageConstants.ERROR_TEAM_INVALID);
            }
        }
    }
}
