package models.combat;

import models.combat.event.CombatEvent;
import models.exceptions.NoActiveBugemonException;
import models.shared.Attack;
import models.shared.Type;
import models.shared.bugemon.TrainerBugemon;
import models.shared.trainer.Trainer;
import dto.CombatStateDTO;
import models.combat.event.KoEvent ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
/**
 * Mutable snapshot of an ongoing combat encounter.
 *
 * <p>{@code CombatState} centralises the domain data that evolves during battle:
 * the two trainers involved in the encounter, their currently active Bugemon,
 * the turn messages produced by combat resolution, encounter metadata used by the UI,
 * and the flags describing whether the combat is over.</p>
 *
 * <p>The state is written by the combat services and action resolvers, then read by
 * the controller and view layers through the {@link CombatStateDTO} interface.
 * It does not decide which action should be executed; instead, it provides the data and
 * helper methods needed to observe and update the fight consistently.</p>
 *
 * Several methods on this class encode combat invariants and expose the current combat
 * state to the controller and view layers.
 *
 * @see services.combat.CombatService
 * @see controllers.combat.CombatController
 * @see CombatStateDTO
 */
public class CombatState implements CombatStateDTO {

    private final Trainer playerTrainer;
    private final Trainer enemyTrainer;


    private final List<CombatEvent> events;


    /** Whether the combat has ended. */
    private boolean combatOver;

    /** Whether the player has won. Only meaningful if {@link #combatOver} is true. */
    private boolean playerWon;

    /** Floor label shown on the first line of the combat header (e.g. {@code "Etage NO2"}). */
    private String floorLabel = constants.LabelConstants.LABEL_FREE_COMBAT;

    /** Step label shown on the second line of the combat header (e.g. {@code "Combat 1"}, {@code "Boss"}). */
    private String stepLabel = "";

    /** Whether the current encounter should be highlighted as a boss fight. */
    private boolean bossFight;

    private boolean awaitingPlayerSwitch = false;

    /**
     * Creates a combat state bound to the two trainers taking part in the encounter.
     *
     * <p>The active Bugemon are not created here; they are expected to be managed by the
     * supplied trainers. This constructor initializes the transient message buffer and keeps
     * references to both sides so the rest of the combat system can operate on shared state.</p>
     *
     * @param playerTrainer the trainer on the player side
     * @param enemyTrainer the trainer on the enemy side
     */
    public CombatState(final Trainer playerTrainer, final Trainer enemyTrainer) {
        this.playerTrainer= playerTrainer;
        this.enemyTrainer = enemyTrainer;
        this.events = new ArrayList<>();

    }



    // Active Bugemons
    /**
     * Returns the player's currently active Bugemon.
     *
     * @return the active Bugemon on the player side
     * @throws NoActiveBugemonException if the player trainer has no active Bugemon
     */
    public TrainerBugemon getPlayerActiveBugemon() throws NoActiveBugemonException {
        return this.playerTrainer.getActiveBugemon();
    }

    /**
     * Returns the enemy's currently active Bugemon.
     *
     * @return the active Bugemon on the enemy side
     */
    public TrainerBugemon getEnemyActiveBugemon() {
        return this.enemyTrainer.getActiveBugemon();
    }


    /**
     * Replaces the player's active Bugemon.
     *
     * @param playerActiveBugemon the Bugemon that becomes active for the player side
     */
    public void setPlayerActiveBugemon(final TrainerBugemon playerActiveBugemon) {
        this.playerTrainer.setActiveBugemon(playerActiveBugemon);
    }

    /**
     * Replaces the enemy's active Bugemon.
     *
     * @param enemyActiveBugemon the Bugemon that becomes active for the enemy side
     */
    public void setEnemyActiveBugemon(final TrainerBugemon enemyActiveBugemon) {
        this.enemyTrainer.setActiveBugemon(enemyActiveBugemon);
    }


    // Teams
    /**
     * Returns the player's team for this combat.
     *
     * @return the player's current team
     */
    public Collection<TrainerBugemon> getTeamPlayer() {
        return this.playerTrainer.getTeamAsList();
    }

    /**
     * Returns the enemy's team for this combat.
     *
     * @return the enemy's current team
     */
    public Collection<TrainerBugemon> getTeamEnemy() {
        return this.enemyTrainer.getTeamAsList();
    }

    /**
     * Returns the team to which the supplied Bugemon belongs in this combat.
     *
     * @param bugemon the Bugemon whose allies should be retrieved
     * @return the allied team containing the Bugemon
     * @throws IllegalArgumentException if the Bugemon does not belong to either side
     */
    public List<TrainerBugemon> getAlliesOf(final TrainerBugemon bugemon) {
        if (this.belongsTo(this.playerTrainer,bugemon)) {return this.playerTrainer.getTeamAsList();}
        if (this.belongsTo(this.enemyTrainer,bugemon)) {return this.enemyTrainer.getTeamAsList();}
        throw new IllegalArgumentException("Bugemon does not belong to this combat");
    }

    /**
     * Checks whether the supplied Bugemon belongs to the given trainer.
     *
     * @param trainer the trainer whose team is inspected
     * @param bugemon the Bugemon to search for
     * @return {@code true} if the Bugemon belongs to that trainer
     */
    private boolean belongsTo(final Trainer trainer, final TrainerBugemon bugemon) {
        return trainer.getTeamAsList().contains(bugemon);
    }


    // End of turn
    /**
     * Applies the end-of-turn tick to both currently active Bugemon.
     *
     * <p>This method is typically called once both sides have acted and the combat is
     * still ongoing. It allows temporary effects and durations to advance by one step.</p>
     */
    public void applyEndOfTurnTicks() {
        this.getPlayerActiveBugemon().registerTimeTick();
        this.getEnemyActiveBugemon().registerTimeTick();
    }


    public void addEvent(final CombatEvent event) {
        this.events.add(event);
    }

    public void clearEvents() {
        this.events.clear();
    }

    // Battle end flags
    /**
     * Returns whether the player has won the combat.
     *
     * <p>This flag is only meaningful once the combat is over.</p>
     *
     * @return {@code true} if the combat ended in a player victory
     */
    public boolean isPlayerWon() {
        return this.playerWon;
    }

    /**
     * Indicates whether the combat has ended.
     *
     * @return {@code true} if no further turn should be processed
     */
    public boolean isCombatOver() {
        return this.combatOver;
    }

    /**
     * Updates the combat-over flag.
     *
     * @param over {@code true} to mark the combat as finished
     */
    public void setCombatOver(final boolean over) {
        this.combatOver = over;
    }

    /**
     * Updates the victory flag for the player side.
     *
     * @param playerWon {@code true} if the player won the encounter
     */
    public void setPlayerWon(final boolean playerWon) {
        this.playerWon = playerWon;
    }

    /**
     * Marks the combat as a defeat for the player and records the provided message.
     *
     * @param message the message that should be added to the combat log
     */
    public void markPlayerDefeat(final String message) {
        this.addEvent(new KoEvent(message));
        this.setPlayerWon(false);
        this.setCombatOver(true);
    }


    // Player switch after K.O.
    /**
     * Indicates whether the combat is waiting for the player to choose a replacement Bugemon.
     *
     * <p>This flag is typically raised after a knock-out when the player still has
     * available Bugemon but must explicitly choose the next active one.</p>
     *
     * @return {@code true} if a player-side forced switch is pending
     */
    public boolean isAwaitingPlayerSwitch() {
        return this.awaitingPlayerSwitch;
    }

    /**
     * Updates the forced-switch flag for the player side.
     *
     * @param status {@code true} if the combat should wait for a replacement choice
     */
    public void setAwaitingPlayerSwitch(final boolean status) {
        this.awaitingPlayerSwitch = status;
    }


    // Header configuration
    /**
     * Sets the floor label shown on the first line of the combat header.
     *
     * @param floorLabel the floor label (e.g. {@code "Etage NO2"})
     */
    public void setFloorLabel(final String floorLabel) {
        this.floorLabel = floorLabel;
    }

    /**
     * Sets the step label shown on the second line of the combat header.
     *
     * @param stepLabel the step label (e.g. {@code "Combat 1"}, {@code "Boss"})
     */
    public void setStepLabel(final String stepLabel) {
        this.stepLabel = stepLabel;
    }

    /**
     * Marks whether the current encounter should be presented as a boss fight.
     *
     * @param bossFight {@code true} if the current encounter is a boss fight
     */
    public void setBossFight(final boolean bossFight) {
        this.bossFight = bossFight;
    }


    /**
     * Returns the attacks currently available to the player's active Bugemon.
     *
     * @return the current move set of the player's active Bugemon
     */
    public List<Attack> getPlayerAttacks() {
        return this.getPlayerActiveBugemon().getCurrentAttacks();
    }


    // CombatStateDTO - interface read by the view
    @Override
    public String getPlayerName() {
        return this.getPlayerActiveBugemon().getName();
    }

    /**
     * Returns the display name of the enemy active Bugemon.
     *
     * @return the enemy Bugemon name
     */
    @Override
    public String getEnemyName() {return this.getEnemyActiveBugemon().getName();}

    /**
     * Returns the current HP of the player's active Bugemon.
     *
     * @return the player's current HP value
     */
    @Override
    public int getPlayerHp() {
        return this.getPlayerActiveBugemon().getCurrentHP();
    }

    /**
     * Returns the current HP of the enemy's active Bugemon.
     *
     * @return the enemy's current HP value
     */
    @Override
    public int getEnemyHp() {
        return this.getEnemyActiveBugemon().getCurrentHP();
    }

    /**
     * Returns the maximum HP of the enemy's active Bugemon.
     *
     * @return the enemy maximum HP
     */
    @Override
    public int getEnemyHpMax() {
        return this.getEnemyActiveBugemon().getMaxHp();
    }

    /**
     * Returns the combat events accumulated for the current turn.
     *
     * @return the current combat events
     */
    @Override
    public List<CombatEvent> getEvents() {
        return Collections.unmodifiableList(this.events);
    }
    /**
     * Returns the maximum HP of the player's active Bugemon.
     *
     * @return the player maximum HP
     */
    @Override
    public int getPlayerHpMax() {
        return this.getPlayerActiveBugemon().getMaxHp();
    }

    /**
     * Returns the sprite path of the enemy's active Bugemon.
     *
     * @return the enemy sprite resource path
     */
    @Override
    public String getEnemySpritePath() {
        return this.getEnemyActiveBugemon().getSprite();
    }

    /**
     * Returns the sprite path of the player's active Bugemon.
     *
     * @return the player sprite resource path
     */
    @Override
    public String getPlayerSpritePath() {
        return this.getPlayerActiveBugemon().getSprite();
    }

    /**
     * Returns the floor label shown on the first line of the combat header.
     *
     * @return the floor label (e.g. {@code "Etage NO2"})
     */
    @Override
    public String getFloorLabel() {
        return this.floorLabel;
    }

    /**
     * Returns the step label shown on the second line of the combat header.
     *
     * @return the step label (e.g. {@code "Combat 1"}, {@code "Boss"})
     */
    @Override
    public String getStepLabel() {
        return this.stepLabel;
    }

    /**
     * Indicates whether the current encounter is flagged as a boss fight.
     *
     * @return {@code true} if the encounter is a boss fight
     */
    @Override
    public boolean isBossFight() {
        return this.bossFight;
    }

    /**
     * Returns the level of the player's active Bugemon.
     *
     * @return the player's Bugemon level
     */
    @Override
    public int getPlayerLevel() {
        return this.getPlayerActiveBugemon().getLevel();
    }

    /**
     * Returns the level of the enemy's active Bugemon.
     *
     * @return the enemy's Bugemon level
     */
    @Override
    public int getEnemyLevel() {
        return this.getEnemyActiveBugemon().getLevel();
    }

    @Override
    public Type getPlayerType() {
        return this.playerTrainer.getActiveBugemonType();
    }


    @Override
    public Type getEnemyType() {
        return this.enemyTrainer.getActiveBugemonType();
    }


    /**
     * Returns the current attacks of the enemy's active Bugemon.
     *
     * @return the enemy's active Bugemon's current attacks
     */
    public List<Attack> getEnemyBugemonCurrentAttacks() {
        return this.enemyTrainer.getActiveBugemonAttacks();
    }

    /**
     * Returns the size of the enemy's team
     *
     * @return the enemy's team size
     */
    public int getEnemyTeamSize() {
        return this.enemyTrainer.getTeamSize();
    }
}
