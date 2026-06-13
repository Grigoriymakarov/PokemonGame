package services.combat;

import models.combat.CombatSide;
import models.combat.CombatState;
import services.combat.strategies.FirstAliveStrategy;
import services.combat.strategies.ReplacementStrategy;
import models.exceptions.BugemonNotFoundException;
import models.shared.bugemon.TrainerBugemon;
import models.shared.trainer.Enemy;
import models.shared.trainer.Player;
import models.shared.trainer.Trainer;
import models.team.Team;
import org.junit.Before;
import org.junit.Test;
import services.BugemonService;
import services.combat.actions.ActionContext;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link KOResolver} class.
 *
 * <p>This test class verifies knock-out handling, combat end conditions,
 * player switch requests, and automatic enemy replacement.</p>
 */
public class KOResolverTest {

    private KOResolver koResolver;
    private CombatState combatState;
    private TrainerBugemon playerBugemon;
    private TrainerBugemon enemyBugemon;
    private Player player;
    private Enemy enemy;
    private ReplacementStrategy strategy;

    /**
     * Creates a combat state with one player Bugemon and one enemy Bugemon before each test.
     */
    @Before
    public void setUp() throws BugemonNotFoundException {
        strategy = new FirstAliveStrategy();

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

    private KOResolver createKOResolver(ActionContext context) {
        return new KOResolver(context);
    }

    /**
     * Builds an action context for the given actor side.
     *
     * @param actorSide the side that performed the action
     * @return an action context configured for the current combat state
     */
    private ActionContext buildContext(CombatSide actorSide) {
        Trainer actor  = actorSide == CombatSide.PLAYER ? player : enemy;
        Trainer target = actorSide == CombatSide.PLAYER ? enemy  : player;

        return new ActionContext(combatState, actor, target, actorSide);
    }


    /**
     * Verifies that processing a living target does not trigger knock-out behavior.
     */
    @Test
    public void targetAliveShouldNotTriggerKO() {
        ActionContext context = buildContext(CombatSide.PLAYER);
        KOResolver koResolver = createKOResolver(context);

        koResolver.processFainted();

        assertFalse("Combat should not be over", combatState.isCombatOver());
        assertTrue("The enemy should remain alive", enemyBugemon.isAlive());
    }

    /**
     * Verifies that knocking out the enemy with no replacement ends combat as a player win.
     */
    @Test
    public void enemyKO_playerWins() {
        enemyBugemon.applyDamage(enemyBugemon.getCurrentHP());
        enemyBugemon.setAlive(false);

        enemyBugemon.applyDamage(enemyBugemon.getCurrentHP());
        ActionContext context = buildContext(CombatSide.PLAYER);
        KOResolver koResolver = createKOResolver(context);

        koResolver.processFainted();

        assertTrue("Combat should be over", combatState.isCombatOver());
        assertTrue("The player should have won", combatState.isPlayerWon());
    }

    /**
     * Verifies that knocking out the player with no replacement ends combat as a player loss.
     */
    @Test
    public void playerKO_playerLoses() {
        playerBugemon.applyDamage(playerBugemon.getCurrentHP());
        ActionContext context = buildContext(CombatSide.ENEMY);
        KOResolver koResolver = createKOResolver(context);

        koResolver.processFainted();

        assertTrue("Combat should be over", combatState.isCombatOver());
        assertFalse("The player should not have won", combatState.isPlayerWon());
    }

    /**
     * Verifies that a player knock-out with a reserve Bugemon requests a player switch.
     */
    @Test
    public void BugemonPlayerKOAndhasRemainingBugemon_setsAwaitingSwitch() throws BugemonNotFoundException {
        BugemonService service = new BugemonService();

        TrainerBugemon reserve = service.createTrainerBugemon("moussil");
        Team playerTeam = new Team();
        playerTeam.add(playerBugemon);
        playerTeam.add(reserve);
        player.setTeam(playerTeam);

        combatState = new CombatState(player, enemy);

        playerBugemon.applyDamage(playerBugemon.getCurrentHP());
        ActionContext context = buildContext(CombatSide.ENEMY);
        KOResolver koResolver = createKOResolver(context);

        koResolver.processFainted();

        assertTrue("Combat should wait for a player switch",
                combatState.isAwaitingPlayerSwitch());
        assertFalse("Combat should not be over",
                combatState.isCombatOver());
    }

    /**
     * Verifies that an enemy knock-out with a reserve Bugemon performs an automatic replacement.
     */
    @Test
    public void BugemonEnemyKOAndHasRemainingBugemon_autoReplacement() throws BugemonNotFoundException {
        BugemonService service = new BugemonService();

        TrainerBugemon reserve = service.createTrainerBugemon("verdurion");
        Team enemyTeam = new Team();
        enemyTeam.add(enemyBugemon);
        enemyTeam.add(reserve);
        enemy.setTeam(enemyTeam);

        combatState = new CombatState(player, enemy);

        enemyBugemon.applyDamage(enemyBugemon.getCurrentHP());
        ActionContext context = buildContext(CombatSide.PLAYER);
        KOResolver koResolver = createKOResolver(context);

        koResolver.processFainted();

        assertFalse("Combat should not be over", combatState.isCombatOver());
        assertEquals("The replacement should become the new active enemy Bugemon",
                reserve, combatState.getEnemyActiveBugemon());
    }
}
