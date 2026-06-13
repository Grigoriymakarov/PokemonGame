package controllers.shared;

import controllers.BaseController;
import constants.MessageConstants;
import javafx.stage.Stage;
import services.BugemonService;
import services.TeamService;
import views.shared.HomeScreenView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Main controller for the Bugemon home screen.
 * <p>
 * This class handles user interactions on the home screen and delegates
 * navigation actions to the {@link MetaController}. It provides access to
 * combat mode, tower mode, team management, and game exit.
 * </p>
 */
public class HomeScreenController extends BaseController<HomeScreenView.Listener, HomeScreenView> implements HomeScreenView.Listener {
    /**
     * Creates a new controller for the home screen.
     *
     * @param metaController the main controller responsible for navigating between views
     * @param view the view associated with the home screen
     */
    public HomeScreenController(final MetaController metaController, final HomeScreenView view) {
        super(metaController, view);
    }
    /**
     * Triggered when the user chooses to start a combat.
     * Navigates to the team selection screen.
     */
    @Override
    public void onStartCombat() {
        this.metaController.switchTo(Windows.TEAM_SELECT);
    }
    /**
     * Triggered when the user chooses to start the tower mode.
     * Navigates to the tower-specific team selection screen.
     */
    @Override
    public void onStartTower() {
        this.metaController.switchTo(Windows.TEAM_SELECT_TOWER);
    }
    /**
     * Triggered when the user chooses to manage their teams.
     * <p>
     * Temporarily instantiates a {@link BugemonService} and a {@link TeamService}
     * before delegating to the team management screen.
     * </p>
     * <p>
     * Note: Direct instantiation of services here is temporary and may later
     * be replaced by dependency injection.
     * </p>
     */
    @Override
    public void onManageTeams() {
        try {
            this.metaController.showTeamManagement(
                    new TeamService(this.metaController.getBugemonService()));
        } catch (final IOException e) {
            this.LOGGER.log(Level.SEVERE, MessageConstants.ERROR_TEAM_SERVICE_LOAD, e);
        }
    }
    /**
     * Displays the home screen view in the given JavaFX stage.
     *
     * @param stage the JavaFX window in which the scene should be displayed
     */
    @Override
    public void showView(final Stage stage) throws FileNotFoundException {
        this.showView(stage, 800, 600);
    }
    /**
     * Triggered when the user chooses to exit the game.
     * Closes the JavaFX application cleanly.
     */
    @Override
    public void onLeaveGame() {
        javafx.application.Platform.exit();
    }


}