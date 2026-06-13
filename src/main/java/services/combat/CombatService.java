package services.combat;

import constants.LabelConstants;
import models.combat.event.SwitchEvent;
import models.combat.CombatSide;
import models.combat.CombatState;
import models.exceptions.ActionExecutionException;
import models.exceptions.CouldNotDetermineActionException;
import models.exceptions.NoActiveBugemonException;
import services.combat.strategies.FirstAliveStrategy;
import services.combat.strategies.RandomOffenseStrat;
import models.shared.Attack;
import models.shared.Item;
import models.shared.Type;
import models.shared.bugemon.TrainerBugemon;
import models.shared.trainer.Enemy;
import models.shared.trainer.Player;
import models.shared.trainer.Trainer;
import models.shared.xp.LevelUpEvent;
import models.team.Team;
import services.XPService;
import services.combat.actions.*;

import java.util.*;

/**
 * Service class responsible for managing combat actions and exposing the current combat state.
 *
 * <p>{@code CombatService} acts as the primary facade for all combat operations. It:
 * <ul>
 *   <li>Maintains the shared {@link CombatState} and all combatant references</li>
 *   <li>Allows controllers to set player actions (attack, item, switch, forfeit)</li>
 *   <li>Orchestrates turn resolution by coordinating the {@link TurnEngine}
 *       and specialized resolvers</li>
 *   <li>Provides utility methods for UI data (available attacks, switch candidates, HP info)</li>
 *   <li>Tracks all participants for post-combat XP distribution</li>
 * </ul>
 *
 * <p><strong>Action Flow:</strong>
 * <ol>
 *   <li>Controller calls one of the action methods (e.g., {@link #attack(Attack)}, {@link #forfeit()})</li>
 *   <li>Controller calls {@link #processTurn()} when ready to execute the turn</li>
 *   <li>Service validates that a player action was set</li>
 *   <li>Service creates an enemy action using the configured strategy</li>
 *   <li>Service delegates turn processing to {@link TurnEngine}</li>
 *   <li>Service returns the updated combat state</li>
 * </ol>
 *
 * <p><strong>Layered Responsibility Separation:</strong>
 * <ul>
 *   <li><strong>CombatService:</strong> high-level action and state management</li>
 *   <li><strong>TurnEngine:</strong> turn sequencing and initiative</li>
 *   <li><strong>Action implementations:</strong> individual action execution</li>
 *   <li><strong>Resolvers (Attack, Effect, KO):</strong> specialized damage/effect resolution</li>
 * </ul>
 *
 * @see CombatState
 * @see TurnEngine
 * @see AttackResolver
 * @see EffectResolver
 * @see KOResolver
 * @see Action
 */
public class CombatService {
    /** The current state of the combat. */
    private final CombatState state;

    /** Engine responsible for sequencing a single combat turn. */
    private final TurnEngine turnEngine;

    /** Trainer abstraction for the player side. */
    private final Player player;

    /** Trainer abstraction for the enemy side. */
    private final Enemy enemy;

    /** Bugémons from the player's team that were sent to the field at least once. */
    private final Set<TrainerBugemon> participants = new LinkedHashSet<>();

    /** Service responsible for XP calculation and distribution. */
    private final XPService xpService;

    /**
     * Constructs a CombatService with the specified player and enemy teams.
     *
     * <p>The constructor initializes:
     * <ul>
     *   <li>The combat state with both trainers (player and enemy AI)</li>
     *   <li>All specialized resolvers (attacks, effects, knock-outs)</li>
     *   <li>The turn engine for sequencing actions</li>
     *   <li>The enemy strategy for autonomous action selection</li>
     *   <li>The participant tracker for post-combat XP distribution</li>
     * </ul>
     *
     * <p>The player trainer is stored as a field to allow later retrieval of player-specific data.</p>
     *
     * @param playerTeam the team assembled by the player
     * @param enemyTeam the team of the opponent
     * @param player the player trainer
     */
    public CombatService(final Team playerTeam, final Team enemyTeam, final Player player) {
        this.player = player;
        this.enemy = new Enemy("Enemy", new RandomOffenseStrat(), new FirstAliveStrategy());
        this.player.setTeam(playerTeam);
        this.enemy.setTeam(enemyTeam);
        this.state = new CombatState(player, this.enemy);
        this.participants.add(this.state.getPlayerActiveBugemon());

        this.turnEngine = new TurnEngine();
        this.xpService = new XPService();
    }

    /**
     * Sets the player's action for the current turn to forfeit.
     *
     * <p>After calling this method, the next call to {@link #processTurn()} will execute
     * a forfeit action, immediately ending the combat with the player as the loser.</p>
     *
     * <p>This method is safe to call multiple times; each call overwrites the previous pending action.</p>
     */
    public void forfeit() {
        this.player.setChosenAction(ActionFactory.forfeit());
    }

    /**
     * Marks the player as defeated immediately without processing a turn.
     *
     * <p>This method is used when the player chooses to forfeit to ensure the forfeit
     * is respected even if the player's Bugemon would be knocked out in the same turn.</p>
     */
    public void markPlayerDefeatImmediate() {
        this.state.markPlayerDefeat(LabelConstants.FORFEIT_MESSAGE);
    }

    /**
     * Sets the player's action for the current turn to perform an attack.
     *
     * <p>After calling this method, the next call to {@link #processTurn()} will execute
     * the supplied attack. If the attack was not available to the player's active Bugemon,
     * the error will be caught during action execution.</p>
     *
     * <p>This method is safe to call multiple times; each call overwrites the previous pending action.</p>
     *
     * @param attack the attack to be performed by the player's active Bugemon
     * @throws NullPointerException if attack is null
     */
    public void attack(final Attack attack) {
        Objects.requireNonNull(attack, "Attack cannot be null");
        this.player.setChosenAction(ActionFactory.attack(attack));
    }

    /**
     * Sets the player's action for the current turn to use a consumable item.
     *
     * <p>After calling this method, the next call to {@link #processTurn()} will apply
     * all effects associated with the item. Item usage is immediately resolved and does
     * not depend on Bugemon availability (items are generally global inventory items).</p>
     *
     * <p>This method is safe to call multiple times; each call overwrites the previous pending action.</p>
     *
     * @param item the item to be used by the player
     */
    public void useItem(final Item item) {
        this.player.setChosenAction(ActionFactory.useItem(item));
    }

    /**
     * Sets the player's action for the current turn to switch out their active Bugemon.
     *
     * <p>After calling this method, the next call to {@link #processTurn()} will replace
     * the player's active Bugemon with the specified replacement. The previous active
     * Bugemon's temporary modifiers are reset when the switch is executed.</p>
     *
     * <p>This method is safe to call multiple times; each call overwrites the previous pending action.</p>
     *
     * @param replacer the Bugemon to switch in as the new active Bugemon
     * @throws NullPointerException if replacer is null
     */
    public void switchBugemon(final TrainerBugemon replacer) {
        Objects.requireNonNull(replacer, "Replacement Bugemon cannot be null");
        this.participants.add(replacer);
        this.player.setChosenAction(ActionFactory.switchBugemon(replacer));
    }

    /** @return an unmodifiable view of Bugémons that entered the field this combat */
    public Set<TrainerBugemon> getParticipants() {
        return Collections.unmodifiableSet(this.participants);
    }

    /**
     * Distributes experience points to all player Bugemons that participated in the combat.
     *
     * @param floorNumber the current tower floor number used to scale XP
     * @param isBoss true if the defeated enemy was a floor boss
     * @return a list of level-up events triggered during distribution
     */
    public List<LevelUpEvent> distributeXP(final int floorNumber, final boolean isBoss) {
        return this.xpService.distributeXP(
                new ArrayList<>(this.participants),
                floorNumber,
                isBoss,
                this.state.getEnemyTeamSize()
        );
    }
    /**
     * Returns the current combat state.
     *
     * <p>The combat state provides a consistent view of all combat data:
     * the active Bugemon on both sides, messages to display, status information, etc.</p>
     *
     * @return the shared combat state
     */
    public CombatState getState() {
        return this.state;
    }
    /**
     * Checks if the combat logic is currently waiting for the player to select a
     * replacement for a fainted Bugemon.
     *
     * @return true if a forced switch is required
     */
    public boolean isAwaitingPlayerSwitch() {
        return this.state.isAwaitingPlayerSwitch();
    }
    /**
     * Retrieves the elemental type of the enemy's active Bugemon.
     *
     * @return the enemy's type
     */
    public Type getEnemyType() {
        return this.state.getEnemyType();
    }
    /**
     * Removes all messages currently stored in the combat state log.
     */
    public void clearCombatMessages() {
        this.state.clearEvents();
    }


    /**
     * Processes a complete combat turn.
     *
     * <p>This method orchestrates the entire flow of a single combat turn:
     * <ol>
     *   <li>Validates that the player has set an action</li>
     *   <li>Determines the enemy's action using the configured strategy</li>
     *   <li>Creates action contexts with all necessary resolvers and trainers</li>
     *   <li>Delegates turn sequencing to {@link TurnEngine}</li>
     *   <li>Clears pending actions for the next turn</li>
     *   <li>Returns the updated combat state</li>
     * </ol>
     *
     * <p>The turn engine handles action order, priority, and knock-out checks during execution.</p>
     *
     * @throws CouldNotDetermineActionException if the player or enemy trainer cannot determine an action
     */
    public void processTurn() throws CouldNotDetermineActionException {
        try {
            final Action playerChosenAction = this.player.chooseAction();
            final Attack enemyChosenAttack = this.enemy.chooseAttack(this.state);
            final Action enemyChosenAction = ActionFactory.attack(enemyChosenAttack);

            //action context indicates the state of the combat now as well as the target an source of the action
            final ActionContext playerContext = this.createActionContext(CombatSide.PLAYER);
            final ActionContext enemyContext  = this.createActionContext(CombatSide.ENEMY);

            // let the turnEngine apply action(aply damage, or switch, or forfeit ) from player-> enemy and from enemy->player  based on the actionContexts of player and enemy
            // as well as on the action chosen. If damage, then it uses DamageCalculator+effectResolver+KoHendler. Same logic for othe actions.
            this.turnEngine.processTurn(new TurnData(
                    playerChosenAction, playerContext,
                    enemyChosenAction,  enemyContext
            ));

            // Do not ask enemy to clear because it update every time before turnEngine.processTurn
            this.clearPendingPlayerActions();
        } catch (final ActionExecutionException e) {
            throw new IllegalStateException("Failed to execute an action during processTurn", e);
        }
    }


    /**
     * Clears all pending actions after a turn has been processed.
     *
     * <p>This ensures that the service is in a clean state and prevents accidentally
     * reusing actions from a previous turn. The next call to {@link #processTurn()}
     * will require a new action to be set.</p>
     */
    void clearPendingPlayerActions() {
        this.player.clearChosenAction();
    }

    /**
     * Creates an action context for the given side.
     *
     * <p>An action context aggregates the data needed for an action to execute:
     * the shared combat state, the acting and target trainers, and the acting side.</p>
     *
     * @param side the side that will be acting (PLAYER or ENEMY)
     * @return a fully initialized action context for the given side
     */
    private ActionContext createActionContext(final CombatSide side) {
        final boolean playerActs = side == CombatSide.PLAYER;
        final Trainer actorTrainer = playerActs ? this.player : this.enemy;
        final Trainer targetTrainer = playerActs ? this.enemy : this.player;
        return new ActionContext(this.state, actorTrainer, targetTrainer, side);
    }

    /**
     * Returns the list of attacks available to the player's active Bugemon.
     *
     * @return a list of available attacks that can be performed by the player's active Bugemon
     */
    public List<Attack> getAvailablePlayerAttacks() {
        return this.state.getPlayerAttacks();
    }

    /**
     * Finds a specific alive Bugemon in the player's team by name.
     *
     * <p>This utility method is commonly used when the player selects a Bugemon to
     * switch in during a forced switch scenario (post K.O.).</p>
     *
     * @param name the name of the Bugemon to find
     * @return the {@link TrainerBugemon} with the matching name
     * @throws IllegalArgumentException if no alive Bugemon with the given name exists
     */
    public TrainerBugemon findAliveBugemon(final String name) {
        return this.state.getTeamPlayer().stream()
            .filter(b -> b.getName().equals(name) && b.isAlive())
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Bugemon inconnu ou K.O. : " + name));
    }

    /**
     * Validates and executes the player's forced switch after a K.O.
     *
     * <p>Handles all updates related to a forced switch:
     * <ol>
     *   <li>Adds the new Bugemon to the participant list (for XP tracking)</li>
     *   <li>Updates the player trainer's active Bugemon</li>
     *   <li>Synchronizes the shared combat state</li>
     *   <li>Clears the waiting flag</li>
     *   <li>Logs the entry to the combat log</li>
     * </ol>
     *
     * <p>By centralizing this logic, we avoid having the controller directly access and modify
     * the combat state, ensuring consistency and making future refactoring easier.</p>
     *
     * @param chosen the Bugemon that the player has selected to replace the fainted one
     * @throws NullPointerException if chosen is null
     */
    public void confirmForcedSwitch(final TrainerBugemon chosen) {
        Objects.requireNonNull(chosen, "Chosen Bugemon cannot be null");
        this.participants.add(chosen);
        this.state.setPlayerActiveBugemon(chosen);
        this.state.setAwaitingPlayerSwitch(false);
        this.state.addEvent(new SwitchEvent(null, chosen.getName()));
    }


    /**
     * Returns the list of Bugemon eligible for switching.
     * <p>The active Bugemon, if there is any, is excluded from the candidates </p>
     *
     * @return a list of TrainerBugemon eligible for switching
     */
    public List<TrainerBugemon> getSwitchCandidates() {
        try {
            final TrainerBugemon active = this.state.getPlayerActiveBugemon();
            return this.state.getTeamPlayer().stream()
                    .filter(TrainerBugemon::isAlive)
                    .filter(b -> !b.equals(active))
                    .toList();
        } catch (NoActiveBugemonException e) {
            // If no active Bugemon, we are likely in a forced switch scenario (player bugemon has fainted).
            // In this case, we should not exclude any candidates.
            return this.state.getTeamPlayer().stream()
                .filter(TrainerBugemon::isAlive)
                .toList();
        }
    }

    /**
     * Resets temporary modifiers when a Bugemon switches out.
     *
     * <p>Temporary stat modifiers (e.g., one-turn boosts) are cleared when a Bugemon leaves the field.
     * This prevents modifiers from persisting to its replacement.</p>
     *
     * @param isPlayer whether the switching Bugemon is on the player side
     */
    public void switchOut(final boolean isPlayer) {
        if (isPlayer) {
            this.player.getActiveBugemon().resetTemporaryModifiers();
        } else {
            this.enemy.getActiveBugemon().resetTemporaryModifiers();

        }
    }
}
