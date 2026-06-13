package controllers.combat;

import constants.LabelConstants;
import constants.MessageConstants;
import controllers.BaseController;
import controllers.shared.MetaController;
import javafx.stage.Stage;
import models.combat.CombatState;
import models.exceptions.CouldNotDetermineActionException;
import models.shared.Attack;
import models.shared.Type;
import models.shared.bugemon.TrainerBugemon;
import models.shared.statistics.Statistics;
import models.shared.trainer.Player;
import models.shared.xp.LevelUpEvent;
import models.team.Team;
import models.tower.StepType;
import models.tower.TowerState;
import services.combat.CombatService;
import services.XPGainBonusFactory;
import services.XPService;
import dto.AttackWithEffectivenessDTO;
import views.combat.CombatView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Controller responsible for managing the combat flow.
 *
 * <p>This class connects the combat view to the combat service.
 * It listens to user actions from the view, calls the appropriate
 * methods in the service, and updates the interface with the new
 * combat state.</p>
 *
 * <p>It plays the role of the controller in the MVC architecture
 * of the combat module.</p>
 *
 * @see CombatService
 * @see CombatView
 */
public class CombatController extends BaseController<CombatView.Listener, CombatView> implements CombatView.Listener {
    private final CombatService combatService;
    private final XPService xpService;
    private final Team playerTeam;
    private final CombatInventoryManager inventoryManager;
    // An empty value indicates that this combat is in free mode (i.e. not in tower mode)
    private final Optional<TowerState> towerState;

    /**
     * Creates a combat controller for a combat in a not tower mode.
     *
     * @param metaController the global navigation controller
     * @param view the combat view to manage
     * @param playerTeam the player's team
     * @param enemyTeam the opponent's team
     * @param trainer the player's trainer instance
     *
     * @see CombatService
     * @see CombatView
     */
    public CombatController(final MetaController metaController, final CombatView view, final Team playerTeam, final Team enemyTeam, final Player trainer) {
        this(metaController, view, playerTeam, enemyTeam, trainer, Optional.empty());
    }

    /**
     * Creates a combat controller for a tower encounter.
     *
     * @param metaController the global navigation controller
     * @param view the combat view
     * @param playerTeam the player's current team
     * @param enemyTeam the generated enemy team
     * @param towerState the current tower progression state
     * @param trainer the player's trainer instance
     */
    public CombatController(final MetaController metaController, final CombatView view, final Team playerTeam, final Team enemyTeam, final Player trainer, final TowerState towerState) {
        this(metaController, view, playerTeam, enemyTeam, trainer, Optional.of(towerState));
    }

    /**
     * Private constructor that initializes the combat controller with all parameters.
     *
     * <p>This constructor is used internally to handle both standard and tower combat initialization.
     * It sets up the combat service, inventory manager, and configures the encounter header based on the tower state.</p>
     *
     * @param metaController the global navigation controller
     * @param view the combat view
     * @param playerTeam the player's team
     * @param enemyTeam the opponent's team
     * @param player the player's trainer instance
     * @param towerState optional tower state for configuring tower encounters
     */
    private CombatController(final MetaController metaController, final CombatView view, final Team playerTeam, final Team enemyTeam, final Player player, final Optional<TowerState> towerState) {
        super(metaController, view);
        this.towerState = towerState;
        this.playerTeam = playerTeam;
        this.combatService = new CombatService(playerTeam, enemyTeam, player);
        this.xpService = new XPService();
        this.inventoryManager = new CombatInventoryManager(view, player, this.combatService, this::updateView);
        this.configureEncounterHeader(this.getState());
        view.showCombatState(this.getState());
    }

    /**
     * Retrieves the current combat state from the service and refreshes switch candidates.
     *
     * @return the current combat state
     */
    private CombatState getState() {
        // Refresh switch candidates every time we get the state to ensure they are up to date,
        // to prevent the switch dialog to be decorrelated from the game state
        this.refreshSwitchCandidates();
        return this.combatService.getState();
    }

    /**
     * Handles the attack action triggered by the player.
     *
     * <p>When the player clicks the attack button, this method:</p>
     * <ol>
     *     <li>Checks if a forced Bugemon switch is required (e.g., after a KO)</li>
     *     <li>If yes, displays an error message and prevents the attack</li>
     *     <li>If no, retrieves available attacks and opens the attack selection dialog</li>
     * </ol>
     *
     * <p>The actual attack execution is deferred to {@link #onAttackChoice(String)},
     * which is called when the player selects an attack from the dialog.</p>
     *
     * @see #onAttackChoice(String)
     * @see CombatView.Listener#onAttackClicked()
     */
    @Override
    public void onAttackClicked(){
        // Don't show attack menu if we're waiting for player to switch Bugemon
        if (this.combatService.isAwaitingPlayerSwitch()) {
            this.view.showMessage(MessageConstants.ERROR_KO_SWITCH);
            return;
        }

        // Get the available attacks and their effectiveness
        final List<Attack> attacks = this.combatService.getAvailablePlayerAttacks();
        final Type enemyType = this.combatService.getEnemyType();
        final List<AttackWithEffectivenessDTO> attacksWithEffectiveness = new ArrayList<>();
        for (final Attack attack: attacks) {
            attacksWithEffectiveness.add(new AttackWithEffectivenessDTO(attack, attack.getEffectivenessAgainst(enemyType)));
        }

        // Show the attack selection menu to the player
        this.view.showAttackMenu(attacksWithEffectiveness);
    }

    /**
     * Handles the forfeit action triggered by the player.
     *
     * <p>When the player clicks the forfeit button, this method:</p>
     * <ol>
     * <li>Checks if a forced Bugemon switch is required</li>
     * <li>If yes, displays an error message and prevents the forfeit</li>
     * <li>If no, marks the player as defeated immediately</li>
     * <li>Navigates to the loss screen</li>
     * </ol>
     *
     * <p>Forfeiting results in immediate loss, ensuring the forfeit is respected
     * even if the player's Bugemon would be knocked out in the same turn.</p>
     */
    @Override
    public void onForfeitClicked(){
        // Don't forfeit if we're waiting for player to switch Bugemon
        if (this.combatService.isAwaitingPlayerSwitch()) {
            this.view.showMessage(MessageConstants.ERROR_FORFEIT_KO);
            return;
        }

        this.combatService.markPlayerDefeatImmediate();
        this.navigateCombatOutcome(this.getState());
    }

    /**
     * Updates the combat view after a player action.
     *
     * <p>This is the central method coordinating the turn flow. It:</p>
     * <ol>
     *     <li>Updates switch candidates for manual switching (all alive except current)</li>
     *     <li>Handles forced switch state: displays KO dialog if player Bugemon is down</li>
     *     <li>Otherwise:
     *         <ul>
     *             <li>Applies pending Bugemon choice from Player object</li>
     *             <li>Processes the turn via {@link CombatService#processTurn()}</li>
     *             <li>Displays updated combat state (HP, messages, sprites)</li>
     *             <li>Checks if a new KO occurred and handles it</li>
     *             <li>Navigates to win/lose screen if combat ended</li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * <p>Note: The switch candidates are recalculated every time to stay in sync with
     * the actual game state, even though they were initialized at combat start.</p>
     *
     * @see MetaController#switchToWin(Team, Optional)
     * @see MetaController#switchToLose(Team, Optional)
     * @see CombatService#processTurn()
     */
    @Override
    public void updateView(){
        CombatState currentState = this.getState();

        if (currentState.isAwaitingPlayerSwitch()) {
            this.handleForcedSwitch();
            return;
        }

        try {
            this.combatService.processTurn();
        } catch (CouldNotDetermineActionException e) {
            throw new IllegalStateException("Could not determine an action: " + e.getMessage(), e);
        }
        currentState = this.getState();
        this.view.showCombatState(currentState);
        currentState.clearEvents();

        if (currentState.isAwaitingPlayerSwitch()) {
            this.handleForcedSwitch();
        } else if (currentState.isCombatOver()) {
            this.navigateCombatOutcome(currentState);
        }
    }

    /**
     * Handles Bugemon switch triggered by the player's choice.
     *
     * <p>This method is called after the player selects a Bugemon from either:</p>
     * <ul>
     * <li>A forced KO switch dialog</li>
     * <li>A manual switch dialog</li>
     * </ul>
     *
     * <p>The method:</p>
     * <ol>
     * <li>Finds the Bugemon by name in the player's team (must be alive)</li>
     * <li>Updates CombatState to set it as the active Bugemon</li>
     * <li>Resets the forced switch flag</li>
     * <li>Displays an entry message and refreshes the view</li>
     * </ol>
     *
     * <p>Note: This does NOT process a turn. The next turn will be processed when
     * the player clicks Attack or Forfeit.</p>
     *
     * @param bugemonName name of the Bugemon to switch to
     *
     * @throws IllegalArgumentException if the name doesn't match any alive Bugemon
     *
     * @see #onAttackClicked()
     * @see #updateView()
     */
    @Override
    public void onSwitchClicked(final String bugemonName) {
        final boolean forcedSwitch = this.combatService.isAwaitingPlayerSwitch();
        final TrainerBugemon chosen = this.combatService.findAliveBugemon(bugemonName);
        this.combatService.switchOut(true);
        if (!forcedSwitch) {
            this.combatService.switchBugemon(chosen);
            this.updateView();
            return;
        }
        this.combatService.confirmForcedSwitch(chosen);
        this.view.showCombatState(this.getState());
        this.combatService.clearCombatMessages();
    }

    /**
     * Updates the switch candidates displayed in the view with their current HP data.
     *
     * <p>This method:</p>
     * <ol>
     * <li>Retrieves the list of available Bugemon that can be switched to</li>
     * <li>Collects their current and maximum HP values</li>
     * <li>Forwards both the Bugemon data and HP information to the view for display</li>
     * </ol>
     *
     * <p>The HP data is used to show HP progress bars next to each switch candidate
     * in the switch dialog, allowing the player to make informed decisions about
     * which Bugemon to switch to.</p>
     *
     * @see #getState()
     * @see #handleForcedSwitch()
     */

    private void refreshSwitchCandidates() {
        final List<TrainerBugemon> bugemons = this.combatService.getSwitchCandidates();
        final java.util.Map<TrainerBugemon, int[]> hpData = bugemons.stream()
            .collect(java.util.stream.Collectors.toMap(
                b -> b,
                b -> new int[]{b.getCurrentHP(), b.getMaxHp()}
            ));
        this.view.setSwitchCandidatesWithHp(bugemons, hpData);
    }

    private void handleForcedSwitch() {
        final List<TrainerBugemon> candidates = this.combatService.getSwitchCandidates();
        this.refreshSwitchCandidates();
        this.view.showForcedSwitchDialog(candidates);
    }

    /**
     * Handles the outcome of a combat encounter.
     *
     * <p>This method determines what happens after combat ends:</p>
     * <ul>
     *     <li><strong>If player won:</strong>
     *         <ul>
     *             <li>Distributes XP to all Bugemons that participated</li>
     *             <li>If any Bugemon leveled up, initiates the level-up reward flow
     *                 (sequential bonus selection screens)</li>
     *             <li>After all level-ups are handled, navigates to the win screen</li>
     *         </ul>
     *     </li>
     *     <li><strong>If player lost:</strong> Navigates directly to the loss screen</li>
     * </ul>
     *
     * <p><strong>Level-Up Reward Flow:</strong></p>
     * <ol>
     *     <li>{@link #handleLevelUpRewards(List, int)} is called with the level-up events</li>
     *     <li>For each Bugemon that leveled up, the player selects a bonus from 3 options</li>
     *     <li>The selected bonus is applied permanently to the Bugemon's stats</li>
     *     <li>After all selections, the win screen is displayed</li>
     * </ol>
     *
     * @param currentState the final combat state after the encounter ended
     *
     * @see #handleLevelUpRewards(List, int)
     * @see CombatService#distributeXP(int, boolean)
     * @see MetaController#switchToWin(Team, Optional)
     * @see MetaController#switchToLose(Team, Optional)
     */
    private void navigateCombatOutcome(final CombatState currentState) {
        if (currentState.isPlayerWon()) {
            if (this.towerState.isPresent()) {
                final boolean bossFight = this.towerState.get().getCurrentStepType() == StepType.BOSS;
                final List<LevelUpEvent> levelUps = this.combatService.distributeXP(
                        this.towerState.get().getCurrentFloorNumber(),
                        bossFight
                );
                if (!levelUps.isEmpty()) {
                    this.handleLevelUpRewards(levelUps, 0);
                    return;
                }
            }
            this.metaController.switchToWin(this.playerTeam, this.towerState);

        } else {
            this.metaController.switchToLose(this.playerTeam, this.towerState);
        }
    }

    /**
     * Handles the level-up reward selection for each Bugemon that leveled up.
     *
     * <p>This method processes level-up events sequentially, displaying a reward
     * selection screen for each Bugemon. When the player selects a reward, it is
     * applied to the Bugemon and the next level-up is processed.</p>
     *
     * @param levelUps the list of level-up events to process
     * @param index the current index in the levelUps list
     */
    private void handleLevelUpRewards(final List<LevelUpEvent> levelUps, final int index) {
        if (index >= levelUps.size()) {
            this.metaController.switchToWin(this.playerTeam, this.towerState);
            return;
        }

        final LevelUpEvent event = levelUps.get(index);
        final TrainerBugemon bugemon = event.bugemon();
        final int oldLevel = event.oldLevel();
        final int newLevel = event.newLevel();

        try {
            final LevelUpRewardController rewardController = new LevelUpRewardController(this.metaController);

                final List<Statistics> bonusOptions = new XPGainBonusFactory().generateBonus();
                final List<String> bonusDescriptions = bonusOptions.stream()
                .map(this::formatBonusDescription)
                .toList();

            final int nextIndex = index + 1;
            rewardController.showBonusSelection(
                bugemon.getDisplayName(),
                oldLevel,
                newLevel,
                bonusDescriptions,
                selectedIndex -> {
                    this.xpService.applyLevelUpBonus(bugemon, bonusOptions.get(selectedIndex));
                    this.handleLevelUpRewards(levelUps, nextIndex);
                }
            );

            rewardController.showView(this.metaController.getPrimaryStage());
        } catch (final IOException e) {
            this.LOGGER.log(Level.SEVERE, MessageConstants.ERROR_REWARD_CONTROLLER, e);
            this.handleLevelUpRewards(levelUps, index + 1);
        }
    }

    /**
     * Formats a Statistics object as a readable bonus description.
     *
     * @param stats the statistics to format
     * @return a human-readable bonus description
     */
    private String formatBonusDescription(final Statistics stats) {
        final StringBuilder sb = new StringBuilder();
        if (stats.getHp() > 0) sb.append(String.format(MessageConstants.BONUS_HP, stats.getHp()));
        if (stats.getAttack() > 0) sb.append(String.format(MessageConstants.BONUS_ATK, stats.getAttack()));
        if (stats.getDefense() > 0) sb.append(String.format(MessageConstants.BONUS_DEF, stats.getDefense()));
        if (stats.getInitiative() > 0) sb.append(String.format(MessageConstants.BONUS_INIT, stats.getInitiative()));

        return sb.toString().trim().isEmpty() ? LabelConstants.LABEL_NO_BONUS : sb.toString().trim();
    }


    /**
     * Handles attack choice triggered by the player's selection from the attack menu.
     *
     * <p>This method:</p>
     * <ol>
     * <li>Finds the attack by name in the player's active Bugemon's move set</li>
     * <li>Sets it as the player's action for this turn</li>
     * <li>Hides the attack menu and returns to the main menu</li>
     * <li>Calls {@link #updateView()} to process the turn</li>
     * </ol>
     *
     * @param attackName the name of the attack to use
     * @throws IllegalArgumentException if the attack name doesn't exist in the player's moves
     *
     * @see CombatView.Listener#onAttackChoice(String)
     */
    @Override
    public void onAttackChoice(final String attackName) {
        final Attack selectedAttack = this.combatService.getAvailablePlayerAttacks()
                .stream()
                .filter(attack -> attack.getName().equals(attackName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(MessageConstants.ERROR_UNKNOWN_ATTACK, attackName)));
        this.combatService.attack(selectedAttack);
        this.view.showMainMenu();
        this.updateView();
    }

    /**
     * Delegates the inventory button click to the dedicated inventory manager.
     */
    @Override
    public void onItemClicked()  {
        this.inventoryManager.openInventory();
    }

    /**
     * Initialises the encounter header fields in the combat state.
     *
     * <p>In tower mode the floor label and step label come directly from {@link TowerState},
     * which is the single source of truth for tower progression labels.
     * In free combat, only the floor label is set to {@link LabelConstants#LABEL_FREE_COMBAT};
     * the step label is left empty.</p>
     *
     * @param combatState the combat state to configure
     */
    private void configureEncounterHeader(final CombatState combatState) {
        if (this.towerState.isEmpty()) {
            combatState.setFloorLabel(LabelConstants.LABEL_FREE_COMBAT);
            return;
        }
        final TowerState state = this.towerState.get();
        final boolean bossFight = state.getCurrentStepType() == StepType.BOSS;
        combatState.setFloorLabel(LabelConstants.LABEL_FLOOR_PREFIX + state.getFloorLabel());
        combatState.setStepLabel(state.getStepLabel());
        combatState.setBossFight(bossFight);
    }


    @Override
    public void showView(Stage stage) throws FileNotFoundException {
        this.showView(stage, 800, 600);
    }
}
