package controllers.shared;

import models.team.Team;
import models.tower.TowerState;
import services.TeamService;

import java.util.ArrayDeque;
import java.util.Optional;

/**
 * Manages the global state of the game.
 *
 * <p>Contains all mutable state and data and info related to the current game session,
 * including the player's team, tower progression, and navigation history.</p>
 */
public class GameStateManager {
    /** The team currently selected or being used by the player. */
    private Team currentPlayerTeam;

    /** The state of the ongoing tower progression, if any. */
    private Optional<TowerState> currentTowerState = Optional.empty();

    /** Service for managing team loading, saving, and validation. */
    private TeamService currentTeamService;

    /** Navigation history stack (LIFO). Stores previous windows to allow "Back" functionality. */
    private final ArrayDeque<Windows> history = new ArrayDeque<>();

    /** The currently displayed window. */
    private Windows currentWindow;

    /**
     * Creates a new GameStateManager with initial window set to HOMESCREEN.
     */
    public GameStateManager() {
        this.currentWindow = Windows.HOMESCREEN;
    }

    // ============ GETTERS ============

    public Team getCurrentPlayerTeam() {
        return this.currentPlayerTeam;
    }

    public Optional<TowerState> getCurrentTowerState() {
        return this.currentTowerState;
    }

    public TeamService getCurrentTeamService() {
        return this.currentTeamService;
    }

    public Windows getCurrentWindow() {
        return this.currentWindow;
    }

    public Optional<Windows> getPreviousWindow() {
        return this.history.isEmpty() ? Optional.empty() : Optional.of(this.history.peek());
    }

    // ============ SETTERS ============

    public void setCurrentPlayerTeam(final Team team) {
        this.currentPlayerTeam = team;
    }

    public void setCurrentTowerState(final Optional<TowerState> towerState) {
        this.currentTowerState = towerState;
    }

    public void setCurrentTeamService(final TeamService teamService) {
        this.currentTeamService = teamService;
    }

    // ============ NAVIGATION MANAGEMENT ============

    /**
     * Updates the current window and pushes the previous one to history.
     * Skips history push if navigating from TEAM_MANAGEMENT.
     *
     * @param window the new current window
     */
    public void navigateTo(final Windows window) {
        if (this.currentWindow != Windows.TEAM_MANAGEMENT) {
            this.history.push(this.currentWindow);
        }
        this.currentWindow = window;
    }


    /**
     * Stores combat end state (team and tower state) for win/loss screens.
     *
     * @param playerTeam the player's team
     * @param towerState the tower state (if in tower mode)
     */
    public void storeCombatEndState(final Team playerTeam, final Optional<TowerState> towerState) {
        this.currentPlayerTeam = playerTeam;
        this.currentTowerState = towerState;
    }
}

