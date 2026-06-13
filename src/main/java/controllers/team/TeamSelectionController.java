package controllers.team;

import constants.MessageConstants;
import controllers.BaseController;
import controllers.shared.MetaController;
import javafx.stage.Stage;
import models.exceptions.*;
import models.shared.bugemon.BugemonSpecie;
import services.BugemonService;
import services.TeamService;
import views.team.TeamSelectionView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Controller responsible for managing the team selection process.
 *
 * <p>This controller handles user interactions related to selecting, adding,
 * removing, and validating Bugemon for a team. It communicates with the
 * {@link TeamSelectionView} for UI updates and with the {@link TeamService}
 * and {@link BugemonService} for model operations.</p>
 *
 * <p>Instances of this controller are created by the {@link MetaController}.
 * It supports multiple modes: standard selection, tower mode, and edit mode.</p>
 */
public class TeamSelectionController extends BaseController<TeamSelectionView.Listener, TeamSelectionView> implements TeamSelectionView.Listener {
    private final BugemonService bugemonService;
    private final TeamService teamService;
    //depending of towerMode value it will start a tower or a free combat via metaController
    private boolean towerMode = false;
    private boolean editMode = false;

    /**
     * Creates a new TeamSelectionController.
     *
     * <p>Initializes the required services and immediately refreshes the view
     * to display available Bugemon and the current team state.</p>
     *
     * @param metaController the main controller managing navigation between screens
     * @param view the view associated with team selection
     * @param bugemonService service used to create and list Bugemon
     * @throws IOException if the view fails to load
     */
    public TeamSelectionController(final MetaController metaController,
                                   final TeamSelectionView view,
                                   final BugemonService bugemonService) throws IOException {
        super(metaController, view);
        this.bugemonService = bugemonService;
        this.teamService = new TeamService(this.bugemonService);
        this.refreshView();
    }

    /**
     * Enables tower mode for this controller.
     *
     * <p>In tower mode, confirming the team will start a tower run instead of a standard combat.</p>
     */
    public void setTowerMode() {
        this.towerMode = true;
    }

    /**
     * Displays the team selection screen in the main application window.
     *
     * <p>Overrides the default dimensions to provide a larger layout suitable
     * for team selection.</p>
     *
     * @param stage the JavaFX stage in which the view should be displayed
     */
    @Override
    public void showView(final Stage stage) throws FileNotFoundException {
        this.showView(stage, 1200, 800);
    }

    /**
     * Saves the currently selected team.
     *
     * <p>Validates the team composition and the team name before saving.
     * If the controller is in edit mode, the user is returned to the team
     * management screen after saving.</p>
     *
     * <p>Displays an error message in the view if validation fails or if an
     * exception occurs during saving.</p>
     */
    @Override
    public void onSave() {
        if (!this.teamService.isTeamValid()) {
            if (this.teamService.getTeamSize() == 0) {
                this.view.showSelectionError(MessageConstants.ERROR_TEAM_EMPTY);
            } else {
                this.view.showSelectionError(MessageConstants.ERROR_TEAM_INVALID);
            }
            return;
        }

        final Optional<String> nameOfTeam = this.view.getTeamName();
        if (nameOfTeam.isEmpty()) {
            this.view.showSelectionError(MessageConstants.ERROR_INVALID_TEAM_NAME);
            return;
        }

        try {
            this.teamService.saveCurrentTeam(nameOfTeam.get());
            this.view.showSaveSuccess();
            if (this.editMode) {
                this.metaController.showTeamManagement(this.teamService);
            }
        } catch (final InvalidTeamNameException e) {
            this.view.showSelectionError(MessageConstants.ERROR_INVALID_TEAM_NAME + e.getMessage());
        } catch (final InvalidTeamCompositionException e) {
            this.view.showSelectionError(MessageConstants.ERROR_TEAM_EMPTY);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, MessageConstants.ERROR_SAVE_TEAM, e);
            this.view.showSelectionError(MessageConstants.ERROR_SAVE_TEAM);
        }
    }

    /**
     * Opens the team management screen.
     *
     * <p>Allows the user to load, edit, or delete existing teams.</p>
     */
    @Override
    public void onLoad() {
        this.metaController.showTeamManagement(this.teamService);
    }

    /**
     * Handles the selection of a Bugemon species by the user.
     *
     * <p>If the Bugemon is already part of the team, it is removed.
     * Otherwise, the controller attempts to add it to the team.</p>
     *
     * <p>Updates the view accordingly or displays an error if the team is full.</p>
     *
     * @param specie the Bugemon species selected by the user
     */
    @Override
    public void onBugemonSelected(final BugemonSpecie specie) {
        // If the Bugemon is already in the team, remove it
        if (this.teamService.containsSpecie(specie)) {
            this.onBugemonDeselected(specie);
            return;
        }

        // Otherwise, attempt to add it to the team
        try {
            this.teamService.addBugemon(specie.id());
            this.refreshView();
        } catch (TeamFullException e) {
            this.view.showSelectionError(String.format(MessageConstants.ERROR_TEAM_FULL, specie.getName()));
        } catch (TeamAlreadyHasSpecieException e) {
            this.view.showSelectionError(String.format(MessageConstants.ERROR_TEAM_ALREADY_HAS_SPECIE, specie.getName()));
        } catch (CannotAddBugemonToTeamException e) {
            this.view.showSelectionError(String.format(MessageConstants.ERROR_CANNOT_ADD_BUGEMON_TO_TEAM, specie.getName()));
        } catch (BugemonNotFoundException e) {
            this.LOGGER.log(Level.SEVERE, String.format("Bugemon needed to be added to team but could not be found %s !", specie.getName()), e);
            throw new IllegalStateException("Bugemon needed to be added to team but could not be found %s !", e);
        }
    }

    /**
     * Handles the removal of a Bugemon species from the team.
     *
     * <p>Updates the view if the removal succeeds, or displays an error message
     * if the operation fails.</p>
     *
     * @param specie the Bugemon species to remove from the team
     */
    @Override
    public void onBugemonDeselected(final BugemonSpecie specie) {
        try {
            this.teamService.removeBugemon(specie.id());
            this.refreshView();
        } catch (final BugemonNotFoundException e) {
            this.view.showSelectionError(String.format(MessageConstants.ERROR_REMOVE_BUGEMON, specie.getName()));
        }
    }

    /**
     * Synchronizes the view with the current state of the model.
     *
     * <p>Updates the list of available Bugemon and the list of Bugemon currently
     * selected in the team.</p>
     */
    private void refreshView() {
        this.view.displaySelectionScreen(
                this.bugemonService.getAllBugemons(),
                this.teamService.getTeam()
        );
    }

    /**
     * Confirms the selected team and proceeds to the next step.
     *
     * <p>If the team is invalid (e.g., empty), an error message is displayed.
     * Otherwise:</p>
     * <ul>
     *     <li>In tower mode: starts a tower run.</li>
     *     <li>In normal mode: starts a standard combat.</li>
     * </ul>
     */
    @Override
    public void onConfirmTeam() {
        if (!this.teamService.isTeamValid()) {
            this.view.showSelectionError(MessageConstants.ERROR_TEAM_EMPTY);
            return;
        }
        if (this.towerMode) {
            this.metaController.startTower(this.teamService.getTeam());
        } else {
            this.metaController.startCombat(this.teamService.getTeam());
        }
    }

    /**
     * Enables edit mode for this controller.
     *
     * <p>Edit mode allows the user to modify an existing team rather than creating
     * a new one. The view is updated to reflect this mode.</p>
     */
    public void setEditMode() {
        this.editMode = true;
        this.view.setEditMode();
    }
}
