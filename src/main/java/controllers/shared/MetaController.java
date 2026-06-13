package controllers.shared;

import constants.MessageConstants;
import controllers.team.TeamSelectionController;
import controllers.tower.endscreen.IntermediateLoseController;
import controllers.tower.endscreen.IntermediateWinController;
import javafx.stage.Stage;
import models.exceptions.ItemNotFoundException;
import models.shared.trainer.Player;
import models.team.Team;
import models.tower.StepType;
import models.tower.TowerState;
import services.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central navigation controller for the application.
 *
 * <p>MetaController owns the JavaFX {@link Stage} and is responsible for
 * switching between screens (team selection, combat, etc.).</p>
 *
 * <p>Sub-controllers such as {@link TeamSelectionController } {@link IntermediateWinController} {@link IntermediateLoseController}  are created here
 * and receive a reference to this MetaController to trigger navigation.</p>
 */
public class MetaController {
    /** Logger for tracking navigation events and errors. */
    private static final Logger LOGGER = Logger.getLogger(MetaController.class.getName());

    /** The primary JavaFX stage where scenes are rendered. */
    private final Stage stage;

    /** Manages all mutable game state (team, tower, navigation history). */
    private final GameStateManager gameState;

    /** Service responsible for tower floor and step progression logic. */
    private final TowerService towerService;

    /** Service managing the player's items and inventory loading. */
    private final InventoryService inventoryService;

    private final BugemonService bugemonService;

    /** The unique player instance for the session. */
    private final Player trainer;

    /** Factory responsible for instantiating views and their controllers. */
    private final ScreenFactory screenFactory;

    /** Orchestrates combat-related operations and navigation flows. */
    private final CombatOrchestrator combatOrchestrator;

    /** Orchestrates tower progression flows. */
    private final TowerOrchestrator towerOrchestrator;

    /** Registry of commands associated with each window for automated navigation. */
    private final Map<Windows, NavigationCommand> commands;

    /**
     * Returns the primary JavaFX stage.c
     *
     * @return the application's primary stage
     */
    public Stage getPrimaryStage() {
        return this.stage;
    }

    /**
     * Creates the MetaController and initializes the team selection screen.
     *
     * @param stage the primary JavaFX stage
     * @param trainer the player used for the current session
     * @param bugemonService the service used to create and retrieve Bugemons
     * @param inventoryService the service managing the player inventory
     * @param towerService the service responsible for tower progression
     */
    public MetaController(final Stage stage,
                          final Player trainer,
                          final BugemonService bugemonService,
                          final InventoryService inventoryService,
                          final TowerService towerService) {
        this.bugemonService = bugemonService;
        this.trainer = trainer;
        this.inventoryService = inventoryService;
        this.resetTrainerInventory();

        this.stage = stage;
        this.towerService = towerService;
        this.gameState = new GameStateManager();
        this.screenFactory = new ScreenFactory(this, stage);
        this.combatOrchestrator = new CombatOrchestrator(this.screenFactory,
                this.inventoryService, this.bugemonService, this.trainer);
        this.towerOrchestrator = new TowerOrchestrator(() -> this.switchTo(Windows.HOMESCREEN),
                this.screenFactory, this.towerService,
                this.inventoryService, this.bugemonService, this.trainer);
        this.commands = new EnumMap<>(Windows.class);
        this.initCommands();
    }

    /**
     * Maps each window type to its corresponding navigation command.
     */
    private void initCommands() {
        this.commands.put(Windows.HOMESCREEN,
                this.screenFactory::showHomeScreen);
        this.commands.put(Windows.TEAM_SELECT,
                this.screenFactory::showTeamSelect);
        this.commands.put(Windows.TEAM_SELECT_TOWER,
                this.screenFactory::showTeamSelectTower);
        this.commands.put(Windows.TEAM_SELECT_EDITMODE,
                this.screenFactory::showTeamSelectEditMode);
        this.commands.put(Windows.WIN,
                () -> this.screenFactory.showWin(this.gameState.getCurrentPlayerTeam(), this.gameState.getCurrentTowerState()));
        this.commands.put(Windows.LOSE,
                () -> this.screenFactory.showLose(this.gameState.getCurrentPlayerTeam(), this.gameState.getCurrentTowerState()));
        this.commands.put(Windows.FLOOR_UNLOCK,
                () -> this.screenFactory.showFloorUnlock(this.gameState.getCurrentPlayerTeam(), this.gameState.getCurrentTowerState().get()));
        this.commands.put(Windows.TOWER_WIN,
                () -> this.screenFactory.showTowerWin(this.gameState.getCurrentPlayerTeam(), this.gameState.getCurrentTowerState().get()));
        this.commands.put(Windows.TEAM_MANAGEMENT,
                () -> this.screenFactory.showTeamManagement(this.gameState.getCurrentTeamService()));
    }

    /** @return the service used to load and create Bugemon instances */
    public BugemonService getBugemonService() { return this.bugemonService; }

    /**
     * Peeks at the previous window in the history stack.
     * @return an Optional containing the previous Window, or empty if stack is empty.
     */
    public Optional<Windows> getPreviousWindow() {
        return this.gameState.getPreviousWindow();
    }
    /**
     * Navigates to the given screen.
     *
     * @param window the target screen
     */
    public void switchTo(final Windows window) {
        this.gameState.navigateTo(window);

        if (!this.commands.containsKey(window)) {
            // Invalid window switch
            throw new IllegalArgumentException("MetaController was asked to switch to an unknown window: " + window);
        }

        try {
            final NavigationCommand command = this.commands.get(window);
            command.execute();
        } catch (final IOException e) {
            this.logNavigationError(e);
        }
    }

    /**
     * Starts a combat session with the given player team.
     * Creates an enemy team automatically and launches the combat screen.
     *
     * @param playerTeam the player's {@link Team}
     */
    public void startCombat(final Team playerTeam) {
        this.combatOrchestrator.startCombat(playerTeam);
    }

    /**
     * Retries a tower combat with the same team and tower state.
     * Used when the player loses and wants to try the same floor again.
     *
     * @param playerTeam the player's team
     * @param towerState the current tower state
     */
    public void retryTowerCombat(final Team playerTeam, final TowerState towerState) {
        this.combatOrchestrator.retryTowerCombat(playerTeam, towerState);
    }

    /**
     * Navigates to the next step in the tower, handling floor completion and tower ending.
     *
     * @param towerState the current tower state
     * @param playerTeam the player's team
     */
    public void nextTowerStep(final TowerState towerState, final Team playerTeam) {
        this.towerOrchestrator.nextTowerStep(towerState, playerTeam);
    }
    

    /**
     * Starts a new tower run from the beginning.
     *
     * @param playerTeam the team selected for the tower run
     */
    public void startTower(final Team playerTeam) {
        this.towerOrchestrator.startTower(playerTeam);
    }

    /**
     * Stores the player's team and tower state, then navigates to the appropriate win screen.
     *
     * <p>Boss victories route to a dedicated screen:
     * <ul>
     *   <li>Final boss (NO9): {@link Windows#TOWER_WIN}</li>
     *   <li>Non-final boss: {@link Windows#FLOOR_UNLOCK}</li>
     * </ul>
     * All other victories use {@link Windows#WIN}.</p>
     *
     * @param playerTeam the player's {@link Team} at the end of the combat
     * @param towerState the current tower state if in tower mode, empty if in free combat
     */
    public void switchToWin(final Team playerTeam, final Optional<TowerState> towerState) {
        this.gameState.storeCombatEndState(playerTeam, towerState);
        if (towerState.isPresent() && towerState.get().getCurrentStepType() == StepType.BOSS) {
            this.switchTo(towerState.get().isTowerComplete() ? Windows.TOWER_WIN : Windows.FLOOR_UNLOCK);
        } else {
            this.switchTo(Windows.WIN);
        }
    }

    /**
     * Stores the player's team and tower state, then navigates to the loss screen.
     *
     * @param playerTeam the player's {@link Team} at the end of the combat
     * @param towerState the current tower state if in tower mode, empty if in free combat
     */
    public void switchToLose(final Team playerTeam, final Optional<TowerState> towerState) {
        this.gameState.storeCombatEndState(playerTeam, towerState);
        this.switchTo(Windows.LOSE);
    }


    /**
     * Displays the team management screen.
     * <p>
     * This screen allows the player to create, edit, or organize their teams.
     * A {@link TeamService} instance is provided to handle team-related operations.
     * </p>
     *
     * @param teamService the service responsible for managing team data
     */
    public void showTeamManagement(final TeamService teamService) {
        this.gameState.setCurrentTeamService(teamService);
        if (this.gameState.getCurrentWindow() != Windows.TEAM_MANAGEMENT) {
            this.gameState.navigateTo(Windows.TEAM_MANAGEMENT);
        }
        this.switchTo(Windows.TEAM_MANAGEMENT);
    }

    /**
     * Resets the player trainer's inventory to ensure a fresh start
     * for a new combat or tower run.
     */
    public void resetTrainerInventory() {
        try {
            this.inventoryService.startInventoryLoad();
        } catch (final ItemNotFoundException e) {
            MetaController.LOGGER.log(Level.SEVERE, "Error occurred while loading inventory", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Logs navigation errors to the application logger.
     *
     * <p>This method provides centralized error logging for all IOException
     * that occur during screen navigation transitions.</p>
     *
     * @param e the IOException to log
     */
    private void logNavigationError(final IOException e) {
        MetaController.LOGGER.log(Level.SEVERE, MessageConstants.ERROR_NAVIGATION, e);
    }
}
