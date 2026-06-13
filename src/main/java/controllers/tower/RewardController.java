package controllers.tower;

import controllers.BaseController;
import controllers.shared.MetaController;
import javafx.stage.Stage;
import models.team.Team;
import models.tower.TowerState;
import views.tower.RewardView;

import java.io.FileNotFoundException;

/**
 * Controller for the reward screen displayed after certain combats in the tower.
 *
 * <p>This screen is shown when the current step is of type {@code REWARD}.
 * For now, it just displays a placeholder screen with a "Next" button
 * that advances to the next step in the tower.</p>
 *
 * <p>The reward logic itself (choosing a bonus, an attack, etc.) will be
 * implemented in a later user story (Story 11).</p>
 *
 * @see RewardView.Listener
 * @see RewardView
 */
public class RewardController extends BaseController<RewardView.Listener, RewardView> implements RewardView.Listener {

    private final TowerState towerState;
    private final Team playerTeam;

    /**
     * Creates a RewardController and sets up the floor label in the view.
     *
     * @param metaController the central navigation controller
     * @param view           the reward screen view
     * @param towerState     the current state of the tower run
     * @param playerTeam     the player's current team
     */
    public RewardController(final MetaController metaController, final RewardView view,
                            final TowerState towerState, final Team playerTeam) {
        super(metaController, view);
        this.towerState = towerState;
        this.playerTeam = playerTeam;

        view.setFloorLabel(towerState.getFloorLabel());
    }

    /**
     * Called when the player clicks "Next".
     * Delegates to MetaController to advance to the next tower step.
     */
    @Override
    public void onNextClicked() {
        this.metaController.nextTowerStep(this.towerState, this.playerTeam);
    }

    /**
     * Displays the reward screen with fixed dimensions (1200x800).
     *
     * @param stage the primary JavaFX stage
     */
    @Override
    public void showView(final Stage stage) throws FileNotFoundException {
        this.showView(stage, 1200, 800);
    }
}