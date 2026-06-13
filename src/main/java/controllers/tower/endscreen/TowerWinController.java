package controllers.tower.endscreen;

import controllers.shared.MetaController;
import models.team.Team;
import models.tower.TowerState;
import views.tower.endscreen.TowerWinView;

import java.util.Objects;
import java.util.Optional;

/**
 * Controller for the tower win screen, shown after defeating the final boss.
 *
 * <p>Displays the player's team state. Only the "Menu Principal" button is available.</p>
 */
public final class TowerWinController extends CombatEndController<TowerWinView.Listener, TowerWinView> implements TowerWinView.Listener {

    /**
     * Creates a TowerWinController.
     *
     * @param metaController the central navigation controller
     * @param view the tower win screen view
     * @param playerTeam the player's team
     * @param towerState the current tower state (must be in the completed state)
     */
    public TowerWinController(final MetaController metaController, final TowerWinView view,
                              final Team playerTeam, final TowerState towerState) {
        super(metaController, view, playerTeam, Optional.of(Objects.requireNonNull(towerState, "towerState cannot be null")));
        this.view.hideFloorLabel();
    }
}
