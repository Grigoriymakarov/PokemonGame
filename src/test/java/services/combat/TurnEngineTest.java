package services.combat;

import models.combat.CombatSide;
import models.combat.CombatState;
import services.combat.strategies.FirstAliveStrategy;
import services.combat.strategies.ReplacementStrategy;
import models.exceptions.ActionExecutionException;
import models.exceptions.BugemonNotFoundException;
import models.shared.Attack;
import models.shared.bugemon.TrainerBugemon;
import models.shared.trainer.Enemy;
import models.shared.trainer.Player;
import models.team.Team;
import org.junit.Before;
import org.junit.Test;
import repositories.AttackRepository;
import repositories.BugemonRepository;
import services.BugemonService;
import services.combat.actions.ActionFactory;
import services.combat.actions.ActionContext;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link TurnEngine} class.
 *
 * <p>This test class verifies turn processing with attacks, knock-outs,
 * and player forfeits.</p>
 */
public class TurnEngineTest {

    private TurnEngine turnEngine;
    private CombatState combatState;
    private TrainerBugemon playerBugemon;
    private TrainerBugemon enemyBugemon;
    private Player player;
    private Enemy enemy;

    /**
     * Builds repository data and creates a combat state before each test.
     *
     * @throws IOException if repository data cannot be loaded
     */
    @Before
    public void setUp() throws IOException, BugemonNotFoundException {
        BugemonRepository.getInstance().buildDataBase();

        turnEngine = new TurnEngine();

        BugemonService service = new BugemonService();
        playerBugemon = service.createTrainerBugemon("florachu");
        enemyBugemon  = service.createTrainerBugemon("pyricore");

        Team playerTeam = new Team();
        playerTeam.add(playerBugemon);

        Team enemyTeam = new Team();
        enemyTeam.add(enemyBugemon);

        player = new Player("Joueur");
        player.setTeam(playerTeam);

        ReplacementStrategy replacementStrategy = new FirstAliveStrategy();
        enemy = new Enemy("Ennemi", new services.combat.strategies.RandomOffenseStrat(), replacementStrategy);
        enemy.setTeam(enemyTeam);

        combatState = new CombatState(player, enemy);
    }

    /**
     * Builds turn data with player and enemy attack actions.
     *
     * @param playerAttack the attack chosen by the player
     * @param enemyAttack the attack chosen by the enemy
     * @return turn data containing both attack actions and contexts
     */
    private TurnData buildTurnData(Attack playerAttack, Attack enemyAttack) {
        ActionContext player1 = new ActionContext(combatState, player, enemy, CombatSide.PLAYER);
        ActionContext enemy1 = new ActionContext(combatState, enemy, player, CombatSide.ENEMY);
        return new TurnData(
                ActionFactory.attack(playerAttack), player1,
                ActionFactory.attack(enemyAttack),  enemy1
        );
    }

    /**
     * Verifies that both Bugemon lose HP after a turn where both attack.
     */
    @Test
    public void bothBugemonsAlive_bothLoseHP_AfterOneTurn() throws ActionExecutionException {
        int playerHpBefore = playerBugemon.getCurrentHP();
        int enemyHpBefore  = enemyBugemon.getCurrentHP();

        Attack playerAttack = playerBugemon.getCurrentAttacks().get(0);
        Attack enemyAttack  = enemyBugemon.getCurrentAttacks().get(0);

        this.turnEngine.processTurn(buildTurnData(playerAttack, enemyAttack));

        assertTrue("The enemy should lose HP", enemyBugemon.getCurrentHP() < enemyHpBefore);
        assertTrue("The player should lose HP", playerBugemon.getCurrentHP() < playerHpBefore);
    }

    /**
     * Verifies that combat ends when the enemy is knocked out during turn processing.
     */
    @Test
    public void processTurn_enemyKOdDuringTurn_combatEnds() throws ActionExecutionException {
        enemyBugemon.applyDamage(enemyBugemon.getCurrentHP() - 1);

        Attack playerAttack = playerBugemon.getCurrentAttacks().get(0);
        Attack enemyAttack  = enemyBugemon.getCurrentAttacks().get(0);

        this.turnEngine.processTurn(buildTurnData(playerAttack, enemyAttack));

        assertTrue("Combat should be over if the enemy is knocked out",
                combatState.isCombatOver());
        assertTrue("The player should have won", combatState.isPlayerWon());
    }


    /**
     * Verifies that a player forfeit ends combat and marks the player as losing.
     */
    @Test
    public void playerForfeits_combatIsOver() throws ActionExecutionException {
        ActionContext player1 = new ActionContext(combatState, player, enemy, CombatSide.PLAYER);
        ActionContext enemy1 = new ActionContext(combatState, enemy, player, CombatSide.ENEMY);

        TurnData turnData = new TurnData(
                ActionFactory.forfeit(), player1,
                ActionFactory.attack(enemyBugemon.getCurrentAttacks().get(0)), enemy1
        );

        this.turnEngine.processTurn(turnData);

        assertTrue("Forfeiting should end combat", combatState.isCombatOver());
        assertFalse("Forfeiting should mark the player as losing", combatState.isPlayerWon());
    }
}
