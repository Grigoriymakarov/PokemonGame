package controllers.shared;

import models.shared.trainer.Player;
import models.team.RandomTeamFactory;
import models.team.Team;
import models.tower.TowerState;
import services.BugemonService;
import services.InventoryService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import constants.MessageConstants;

/**
 * Orchestrates combat-related operations and navigation flows.
 *
 * <p>Handles starting combat sessions, retrying tower combats, and managing
 * combat end states (win/loss). Delegates to ScreenFactory for view display.</p>
 */
public class CombatOrchestrator {
    private static final Logger LOGGER = Logger.getLogger(CombatOrchestrator.class.getName());

    private final ScreenFactory screenFactory;
    private final InventoryService inventoryService;
    private final BugemonService bugemonService;
    private final Player player;

    /**
     * Creates a CombatOrchestrator.
     *
     * @param screenFactory the factory for creating and displaying screens
     * @param inventoryService service for inventory management
     * @param bugemonService service for bugemon operations
     * @param player the player's trainer instance
     */
    public CombatOrchestrator(final ScreenFactory screenFactory,
                             final InventoryService inventoryService,
                             final BugemonService bugemonService,
                             final Player player) {
        this.screenFactory = screenFactory;
        this.inventoryService = inventoryService;
        this.bugemonService = bugemonService;
        this.player = player;
    }

    /**
     * Starts a combat session with the given player team.
     * Creates an enemy team automatically and launches the combat screen.
     *
     * @param playerTeam the player's {@link Team}
     */
    public void startCombat(final Team playerTeam) {
        this.resetPlayerInventory();
        playerTeam.resetForCombat();
        final Team enemyTeam = this.createEnemyTeam(playerTeam.size());

        try {
            this.screenFactory.showCombat(playerTeam, enemyTeam, this.player);
        } catch (final IOException e) {
            this.logCombatError(e);
        }
    }

    /**
     * Retries a tower combat with the same team and tower state.
     * Used when the player loses and wants to try the same floor again.
     *
     * @param playerTeam the player's team
     * @param towerState the current tower state
     */
    public void retryTowerCombat(final Team playerTeam, final TowerState towerState) {
        this.resetPlayerInventory();
        playerTeam.resetForCombat();
        final Team enemyTeam = this.createEnemyTeam(playerTeam.size());

        try {
            this.screenFactory.showCombatTower(playerTeam, enemyTeam, towerState, this.player);
        } catch (final IOException e) {
            this.logCombatError(e);
        }
    }


    /**
     * Creates a random enemy team of the given size.
     *
     * @param size the number of bugemon in the team
     * @return a randomly generated Team
     */
    private Team createEnemyTeam(final int size) {
        return new RandomTeamFactory(this.bugemonService).createTeam(size);
    }

    /**
     * Resets the player trainer's inventory to ensure a fresh start
     * for a new combat or tower run.
     */
    private void resetPlayerInventory() {
        try {
            this.inventoryService.startInventoryLoad();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while loading inventory", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Logs combat-related errors.
     *
     * @param e the IOException to log
     */
    private void logCombatError(final IOException e) {
        LOGGER.log(Level.SEVERE, MessageConstants.ERROR_NAVIGATION, e);
    }
}

