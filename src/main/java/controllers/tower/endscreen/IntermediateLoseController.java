package controllers.tower.endscreen;

import controllers.shared.MetaController;
import models.team.Team;
import models.tower.TowerState;
import views.tower.endscreen.IntermediateLoseView;

import java.util.Optional;


/**
 * Controller for the intermediate lose screen displayed after a combat defeat.
 *
 * <p>Displays the player's team details and offers navigation options
 * to restart the tower or return to the main menu.</p>
 *
 * @see IntermediateLoseView.Listener
 * @see IntermediateLoseView
 */
public final class IntermediateLoseController extends CombatEndController<IntermediateLoseView.Listener, IntermediateLoseView> implements IntermediateLoseView.Listener {

    /**
     * Creates an IntermediateLoseController.
     *
     * @param metaController the central navigation controller
     * @param view the loose screen view
     * @param playerTeam the player's {@link Team} at the end of the combat
     * @param towerState the current tower state in tower mode, empty in free combat mode
     */

    public IntermediateLoseController(final MetaController metaController, final IntermediateLoseView view, final Team playerTeam, final Optional<TowerState> towerState){
        super(metaController,view, playerTeam, towerState);
    }

    /**
     * Handles the restart button click.
     * In tower mode: restarts the tower from NO2 with the same team.
     * In free combat mode: restarts a new combat with the same team.
     */
    @Override
    public void onAgainClicked() {
        if (this.towerState.isPresent()) {
            this.metaController.startTower(this.playerTeam);
        } else {
            this.metaController.startCombat(this.playerTeam);
        }
    }
}
