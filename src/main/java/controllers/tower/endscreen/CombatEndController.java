package controllers.tower.endscreen;

import controllers.BaseController;
import controllers.exceptions.ControllerMissingListenerException;
import controllers.shared.MetaController;
import controllers.shared.Windows;
import javafx.stage.Stage;
import models.shared.bugemon.TrainerBugemon;
import models.team.Team;
import models.tower.TowerState;
import views.tower.endscreen.CombatEndView;

import java.io.FileNotFoundException;
import java.util.Optional;

public abstract class CombatEndController<L extends CombatEndView.CombatEndListener, V extends CombatEndView<L, ?>> extends BaseController<L, V> {

    protected Optional<TowerState> towerState;
    protected Team playerTeam;

    /**
     * Creates an CombatEndController.
     *
     * <p>If a tower state is provided, the floor label is updated accordingly.
     * Otherwise, the floor label is hidden (free combat mode).</p>
     * <p>The player's team details are added to the view, with XP displayed only if a tower state is present.</p>
     *
     * <p><strong>Important:</strong> Daughter controller classes must implement a sub-interface of {@link CombatEndView.CombatEndListener} to receive events from the view.</p>
     *
     * @param metaController the central navigation controller
     * @param view the associated combat end screen view
     * @param playerTeam the player's {@link Team} at the end of the combat
     * @param towerState the current tower state in tower mode, empty in free combat mode
     */
    public CombatEndController(final MetaController metaController, final V view, final Team playerTeam, final Optional<TowerState> towerState){
        super(metaController, view);

        // Ensure the controller implements the necessary listener interface for the view
        if (!(this instanceof CombatEndView.CombatEndListener)) {
            throw new ControllerMissingListenerException("The controller " + this.getClass().getSimpleName() + " must implement a sub-interface of CombatEndView.CombatEndListener");
        }

        this.playerTeam = playerTeam;
        this.towerState = towerState;
        this.updateDetails();
        this.addTeamBugemons();
    }


    /**
     * Updates the player's team and tower state to display on the win screen.
     *
     *
     */
    private void updateDetails() {
        if (this.towerState.isPresent()) {
            this.view.setFloorLabel(this.towerState.get().getFloorLabel());
        } else {
            this.view.hideFloorLabel();
        }
    }

    /**
     * Populates the view with the details of each Bugemon in the player's team.
     * Clears previous entries before adding updated data.
     */
    private void addTeamBugemons(){
        // XP only has meaning if the player can progress through tower stages and floors
        final boolean shouldDisplayXP = this.towerState.isPresent();

        this.view.clearBugemonDetails();

        for (TrainerBugemon bugemon: this.playerTeam.getMembers()) {
            this.view.addBugemonDetails(bugemon, shouldDisplayXP);
        }
    }

    /**
     * Handles the main menu button click by returning to the home screen.
     */
    public void onMainClicked(){
        this.metaController.switchTo(Windows.HOMESCREEN);
    }

    /**
     * Displays the win screen with updated Bugemon details.
     * Overridden to set custom window dimensions (1200x800).
     *
     * @param stage the primary JavaFX stage
     */
    @Override
    public void showView(final Stage stage) throws FileNotFoundException {
        this.showView(stage, 1200, 800);
    }
}
