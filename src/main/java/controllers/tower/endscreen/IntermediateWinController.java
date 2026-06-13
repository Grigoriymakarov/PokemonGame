package controllers.tower.endscreen;

import controllers.shared.MetaController;
import controllers.shared.Windows;
import models.team.Team;
import models.tower.TowerState;
import views.tower.endscreen.IntermediateWinView;

import java.util.Optional;

/**
 * Controller for the intermediate win screen displayed after a combat victory.
 *
 * <p>Displays the player's team details and offers navigation options
 * to continue to the next tower step or return to the main menu.</p>
 *
 * @see IntermediateWinView.Listener
 * @see IntermediateWinView
 */
public final class IntermediateWinController extends CombatEndController<IntermediateWinView.Listener, IntermediateWinView> implements IntermediateWinView.Listener {

    /**
     * Creates an IntermediateWinController.
     *
     * <p>If a tower state is provided, the floor label is updated accordingly.
     *      * Otherwise, the floor label is hidden (free combat mode).</p>
     *
     * @param metaController the central navigation controller
     * @param view the win screen view
     * @param playerTeam the player's {@link Team} at the end of the combat
     * @param towerState the current tower state in tower mode, empty in free combat mode
     */
    public IntermediateWinController(final MetaController metaController, final IntermediateWinView view, final Team playerTeam, final Optional<TowerState> towerState){
        super(metaController, view, playerTeam, towerState);
    }

    /**
     * Handles the next step button click.
     * Advances to the next combat in the tower with the current player team.
     *
     * @see MetaController#nextTowerStep(TowerState, Team)
     */
    @Override
    public void onNextClicked() {
        if (this.towerState .isEmpty()) {
            this.metaController.switchTo(Windows.TEAM_SELECT);
            return;
        }
        this.metaController.nextTowerStep(this.towerState.get(), this.playerTeam);
    }


}
