package controllers.tower.endscreen;

import controllers.shared.MetaController;
import models.team.Team;
import models.tower.TowerState;
import views.tower.endscreen.FloorUnlockView;

import java.util.Objects;
import java.util.Optional;

/**
 * Controller for the floor unlock screen, shown after defeating a non-final boss.
 *
 * <p>Displays the next floor's number and the player's team state.
 * "Continuer" advances to the first step of the unlocked floor.</p>
 */
public final class FloorUnlockController extends CombatEndController<FloorUnlockView.Listener, FloorUnlockView> implements FloorUnlockView.Listener {

    /**
     * Creates a FloorUnlockController and sets the floor label to the next floor.
     *
     * @param metaController the central navigation controller
     * @param view the floor unlock screen view
     * @param playerTeam the player's team
     * @param towerState the current tower state (must be on a non-final floor)
     */
    public FloorUnlockController(final MetaController metaController, final FloorUnlockView view,
                                 final Team playerTeam, final TowerState towerState) {
        super(metaController, view, playerTeam, Optional.of(Objects.requireNonNull(towerState, "towerState cannot be null")));
        this.view.setFloorLabel(towerState.getNextFloorLabel());
    }

    /**
     * Advances to the first step of the newly unlocked floor.
     */
    @Override
    public void onContinueClicked() {
        this.metaController.nextTowerStep(this.towerState.get(), this.playerTeam);
    }
}
