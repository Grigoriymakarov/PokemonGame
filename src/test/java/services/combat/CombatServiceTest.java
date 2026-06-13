package services.combat;

import models.exceptions.BugemonNotFoundException;
import models.exceptions.CouldNotDetermineActionException;
import models.shared.Attack;
import models.shared.bugemon.TrainerBugemon;
import models.shared.statistics.TemporaryStatsModifier;
import models.shared.trainer.Player;
import models.team.Team;
import org.junit.Before;
import org.junit.Test;
import repositories.AttackRepository;
import repositories.BugemonRepository;
import services.BugemonService;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link CombatService} class.
 *
 * <p>This test class verifies Bugemon switching, turn processing,
 * forfeiting, attack resolution, and forced switch confirmation.</p>
 */
public class CombatServiceTest {

    private CombatService combatService;
    private TrainerBugemon playerBugemon;
    private TrainerBugemon enemyBugemon;
    private Player playerTrainer;

    private static final String PLAYER_ID = "florachu";
    private static final String ENEMY_ID  = "pyricore";

    /**
     * Builds repository data and creates a combat service before each test.
     *
     * @throws IOException if repository data cannot be loaded
     */
    @Before
    public void setUp() throws IOException, BugemonNotFoundException {
        BugemonRepository.getInstance().buildDataBase();

        BugemonService bugemonService = new BugemonService();
        playerBugemon = bugemonService.createTrainerBugemon(PLAYER_ID);
        enemyBugemon  = bugemonService.createTrainerBugemon(ENEMY_ID);

        Team playerTeam = new Team();
        playerTeam.add(playerBugemon);

        Team enemyTeam = new Team();
        enemyTeam.add(enemyBugemon);

        playerTrainer = new Player("Joueur");
        combatService = new CombatService(playerTeam, enemyTeam, playerTrainer);
    }

    /**
     * Verifies that switching Bugemon adds the replacement to combat participants.
     *
     * @throws IOException if repository data cannot be loaded
     */
    @Test
    public void switchBugemon_addsToParticipants() throws IOException, BugemonNotFoundException {
        BugemonService service = new BugemonService();
        TrainerBugemon reserve = service.createTrainerBugemon(ENEMY_ID);

        combatService.switchBugemon(reserve);
        assertTrue("The replacement should be added to combat participants",
                combatService.getParticipants().contains(reserve));
    }

    /**
     * Verifies that processing a turn clears the pending player action.
     */
    @Test
    public void processTurn_clearsPendingActionsAfterTurn() throws CouldNotDetermineActionException {
        Attack attack = playerBugemon.getCurrentAttacks().get(0);
        combatService.attack(attack);
        combatService.processTurn();

        assertThrows(CouldNotDetermineActionException.class,
             () ->   playerTrainer.chooseAction());
    }

    /**
     * Verifies that forfeiting ends combat and does not mark the player as the winner.
     */
    @Test
    public void forfeit_endsCombat() throws CouldNotDetermineActionException {
        combatService.forfeit();
        combatService.processTurn();

        assertTrue("Combat should be over after forfeiting",
                combatService.getState().isCombatOver());
        assertFalse("The player should not be declared winner after forfeiting",
                combatService.getState().isPlayerWon());
    }

    /**
     * Verifies that processing a turn with an attack makes the enemy lose HP.
     */
    @Test
    public void processTurn_withAttack_enemyLosesHP() throws CouldNotDetermineActionException {
        int hpBefore = enemyBugemon.getCurrentHP();
        playerBugemon.addTemporaryModifier(new TemporaryStatsModifier(0, 0, 1000, 0, 5));
        Attack attack = playerBugemon.getCurrentAttacks().get(0);

        combatService.attack(attack);
        combatService.processTurn();

        assertTrue("The enemy should lose HP after an attack",
                enemyBugemon.getCurrentHP() < hpBefore);
    }

    /**
     * Verifies that confirming a forced switch clears the awaiting switch flag.
     *
     * @throws IOException if repository data cannot be loaded
     */
    @Test
    public void confirmForcedSwitch_clearsAwaitingSwitch() throws BugemonNotFoundException {
        BugemonService service = new BugemonService();
        TrainerBugemon reserve = service.createTrainerBugemon(ENEMY_ID);

        combatService.getState().setAwaitingPlayerSwitch(true);
        combatService.confirmForcedSwitch(reserve);

        assertFalse("awaitingPlayerSwitch should be false after confirmForcedSwitch",
                combatService.getState().isAwaitingPlayerSwitch());
    }
}
