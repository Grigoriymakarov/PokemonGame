package controllers.shared;

import models.shared.trainer.Player;
import models.team.RandomTeamFactory;
import models.team.Team;
import models.tower.StepType;
import models.tower.TowerState;
import services.BugemonService;
import services.InventoryService;
import services.TowerService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import constants.MessageConstants;
import models.exceptions.ItemNotFoundException;

/**
 * Orchestrates tower progression: starting a run, advancing steps and launching
 * the appropriate screens for combat or rewards.
 */
public class TowerOrchestrator {
    private static final Logger LOGGER = Logger.getLogger(TowerOrchestrator.class.getName());

    private final Runnable onNavigateHome;
    private final ScreenFactory screenFactory;
    private final TowerService towerService;
    private final InventoryService inventoryService;
    private final BugemonService bugemonService;
    private final Player player;

    public TowerOrchestrator(final Runnable onNavigateHome,
                             final ScreenFactory screenFactory,
                             final TowerService towerService,
                             final InventoryService inventoryService,
                             final BugemonService bugemonService,
                             final Player player) {
        this.onNavigateHome = onNavigateHome;
        this.screenFactory = screenFactory;
        this.towerService = towerService;
        this.inventoryService = inventoryService;
        this.bugemonService = bugemonService;
        this.player = player;
    }

    /**
     * Starts a new tower run from the beginning.
     *
     * @param playerTeam the team selected for the tower run.
     */
    public void startTower(final Team playerTeam) {
        try {
            this.inventoryService.startInventoryLoad();
        } catch (final ItemNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while loading inventory", e);
            throw new IllegalStateException(e);
        }

        final TowerState towerState = this.towerService.createInitialState();
        playerTeam.resetProgression();
        this.launchNextCombatStep(towerState, playerTeam);
    }

    /**
     * Navigates to the next step in the tower, handling floor completion and tower ending.
     *
     * @param towerState the current tower state
     * @param playerTeam the player's team
     */
    public void nextTowerStep(final TowerState towerState, final Team playerTeam) {
        if (this.advanceProgress(towerState)) {
            this.onNavigateHome.run();
            return;
        }
        playerTeam.resetForCombat();
        this.launchNextCombatStep(towerState, playerTeam);
    }

    /**
     * Advances the tower state by one step, then one floor if the floor is complete.
     *
     * @param towerState the current tower state
     * @return {@code true} if the run has ended (tower won or no more floors)
     */
    private boolean advanceProgress(final TowerState towerState) {
        towerState.nextStep();
        if (!towerState.isFloorComplete()) {
            return false;
        }
        return !this.towerService.advanceToNextFloor(towerState);
    }

    /**
     * Launches the next step of the tower progression based on the current tower state.
     *
     * @param towerState the current state of the tower progression
     * @param playerTeam the player's team used throughout the tower run
     */
    private void launchNextCombatStep(final TowerState towerState, final Team playerTeam) {
        final StepType stepType = towerState.getCurrentStepType();
        try {
            switch (stepType) {
                case COMBAT, BOSS -> {
                    final Team enemyTeam = new RandomTeamFactory(this.bugemonService).createTeam(playerTeam.size());
                    this.screenFactory.showCombatTower(playerTeam, enemyTeam, towerState, this.player);
                }
                case REWARD -> this.screenFactory.showReward(towerState, playerTeam);
            }
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, MessageConstants.ERROR_NAVIGATION, e);
            this.onNavigateHome.run();
        }
    }
}
