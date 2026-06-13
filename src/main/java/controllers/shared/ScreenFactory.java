package controllers.shared;

import controllers.combat.CombatController;
import controllers.team.TeamSelectionController;
import controllers.team.TeamsManageController;
import controllers.tower.endscreen.FloorUnlockController;
import controllers.tower.endscreen.IntermediateLoseController;
import controllers.tower.endscreen.IntermediateWinController;
import controllers.tower.endscreen.TowerWinController;
import controllers.tower.RewardController;
import javafx.stage.Stage;
import models.shared.trainer.Player;
import models.team.Team;
import models.tower.TowerState;
import services.TeamService;
import views.combat.CombatView;
import views.shared.HomeScreenView;
import views.team.TeamSelectionView;
import views.team.TeamsManageView;
import views.tower.endscreen.FloorUnlockView;
import views.tower.endscreen.IntermediateLoseView;
import views.tower.endscreen.IntermediateWinView;
import views.tower.endscreen.TowerWinView;
import views.tower.RewardView;

import java.io.IOException;
import java.util.Optional;

/**
 * Factory responsible for creating controllers and displaying application screens.
 *
 * <p>{@code ScreenFactory} centralizes screen construction so that navigation code
 * can switch views without duplicating controller and view initialization logic.</p>
 */
public class ScreenFactory {

    private final MetaController metacontroller;
    private final Stage stage;

    /**
     * Creates a screen factory bound to the main navigation controller and stage.
     *
     * @param metacontroller the controller coordinating application navigation
     * @param stage the JavaFX stage on which screens are displayed
     */
    public ScreenFactory(final MetaController metacontroller, final Stage stage) {
        this.metacontroller = metacontroller;
        this.stage = stage;
    }

    /**
     * Displays the home screen.
     *
     * @throws IOException if the home screen cannot be loaded
     */
    public void showHomeScreen() throws IOException {
        new HomeScreenController(this.metacontroller, new HomeScreenView()).showView(this.stage);
    }

    /**
     * Displays the standard team selection screen.
     *
     * @throws IOException if the team selection screen cannot be loaded
     */
    public void showTeamSelect() throws IOException {
        new TeamSelectionController(this.metacontroller, new TeamSelectionView(),
                this.metacontroller.getBugemonService()).showView(this.stage);
    }

    /**
     * Displays the team selection screen configured for tower mode.
     *
     * @throws IOException if the team selection screen cannot be loaded
     */
    public void showTeamSelectTower() throws IOException {
        final TeamSelectionController controller = new TeamSelectionController(
                this.metacontroller, new TeamSelectionView(), this.metacontroller.getBugemonService());
        controller.setTowerMode();
        controller.showView(this.stage);
    }

    /**
     * Displays the team selection screen configured for team editing.
     *
     * @throws IOException if the team selection screen cannot be loaded
     */
    public void showTeamSelectEditMode() throws IOException {
        final TeamSelectionController controller = new TeamSelectionController(
                this.metacontroller, new TeamSelectionView(), this.metacontroller.getBugemonService());
        controller.setEditMode();
        controller.showView(this.stage);
    }

    /**
     * Displays the intermediate win screen after a successful tower combat.
     *
     * @param playerTeam the player's current team
     * @param towerState the current tower progression state
     * @throws IOException if the win screen cannot be loaded
     */
    public void showWin(final Team playerTeam, final Optional<TowerState> towerState) throws IOException {
        final IntermediateWinController controller = new IntermediateWinController(this.metacontroller, new IntermediateWinView(), playerTeam, towerState);
        controller.showView(this.stage);
    }

    /**
     * Displays the intermediate lose screen after a failed combat.
     *
     * @param playerTeam the player's current team
     * @param towerState the current tower progression state (if applicable)
     * @throws IOException if the loss screen cannot be loaded
     */
    public void showLose(final Team playerTeam, final Optional<TowerState> towerState) throws IOException {
        final IntermediateLoseController controller = new IntermediateLoseController(this.metacontroller, new IntermediateLoseView(), playerTeam, towerState);
        controller.showView(this.stage);
    }

    /**
     * Displays the team management screen.
     *
     * @param teamService the service used to load, save, and manage teams
     * @throws IOException if the team management screen cannot be loaded
     */
    public void showTeamManagement(final TeamService teamService) throws IOException {
        new TeamsManageController(this.metacontroller, new TeamsManageView(), teamService).showView(this.stage);
    }

    /**
     * Displays a standard combat screen.
     *
     * @param playerTeam the player's team
     * @param enemyTeam the enemy team
     * @param trainer the player trainer used for the encounter
     * @throws IOException if the combat screen cannot be loaded
     */
    public void showCombat(final Team playerTeam, final Team enemyTeam, final Player trainer) throws IOException {
        new CombatController(this.metacontroller, new CombatView(), playerTeam, enemyTeam, trainer).showView(this.stage);
    }

    /**
     * Displays a tower combat screen.
     *
     * @param playerTeam the player's team
     * @param enemyTeam the enemy team
     * @param towerState the current tower progression state
     * @param trainer the player trainer used for the encounter
     * @throws IOException if the combat screen cannot be loaded
     */
    public void showCombatTower(final Team playerTeam, final Team enemyTeam, final TowerState towerState, final Player trainer) throws IOException {
        new CombatController(this.metacontroller, new CombatView(), playerTeam, enemyTeam, trainer, towerState).showView(this.stage);
    }

    /**
     * Displays the reward screen for the current tower state.
     *
     * @param towerState the current tower progression state
     * @param playerTeam the player's current team
     * @throws IOException if the reward screen cannot be loaded
     */
    public void showReward(final TowerState towerState, final Team playerTeam) throws IOException {
        new RewardController(this.metacontroller, new RewardView(), towerState, playerTeam).showView(this.stage);
    }

    /**
     * Displays the floor unlock screen after defeating a non-final boss.
     *
     * @param playerTeam the player's current team
     * @param towerState the current tower state (must be on a non-final floor)
     * @throws IOException if the floor unlock screen cannot be loaded
     */
    public void showFloorUnlock(final Team playerTeam, final TowerState towerState) throws IOException {
        new FloorUnlockController(this.metacontroller, new FloorUnlockView(), playerTeam, towerState).showView(this.stage);
    }

    /**
     * Displays the tower win screen after defeating the final boss.
     *
     * @param playerTeam the player's current team
     * @param towerState the current tower state
     * @throws IOException if the tower win screen cannot be loaded
     */
    public void showTowerWin(final Team playerTeam, final TowerState towerState) throws IOException {
        new TowerWinController(this.metacontroller, new TowerWinView(), playerTeam, towerState).showView(this.stage);
    }
}
