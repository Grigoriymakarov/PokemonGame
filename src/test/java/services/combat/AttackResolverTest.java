package services.combat;

import models.combat.CombatState;
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
import services.combat.strategies.RandomOffenseStrat;
import services.combat.strategies.FirstAliveStrategy;
import services.combat.strategies.ReplacementStrategy;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link AttackResolver} class.
 *
 * <p>This test class verifies damage application and knock-out detection
 * when resolving attacks.</p>
 */
public class AttackResolverTest {

    private AttackResolver attackResolver;
    private CombatState combatState;
    private TrainerBugemon author;
    private TrainerBugemon target;
    private Attack validAttack;

    /**
     * Builds repository data and creates a combat state before each test.
     *
     * @throws IOException if repository data cannot be loaded
     */
    @Before
    public void setUp () throws IOException, BugemonNotFoundException {
        BugemonRepository.getInstance().buildDataBase();

        BugemonService bugemonService = new BugemonService();

        author = bugemonService.createTrainerBugemon("florachu");
        target = bugemonService.createTrainerBugemon("pyricore");

        validAttack = author.getCurrentAttacks().get(0);

        Team playerTeam = new Team();
        playerTeam.add(author);

        Team enemyTeam = new Team();
        enemyTeam.add(target);

        Player player = new Player("Joueur");
        player.setTeam(playerTeam);

        ReplacementStrategy replacementStrategy = new FirstAliveStrategy();
        Enemy enemy = new Enemy("Ennemi", new RandomOffenseStrat(), replacementStrategy);
        enemy.setTeam(enemyTeam);

        combatState = new CombatState(player, enemy);
        attackResolver = new AttackResolver(combatState, author, target, validAttack);
    }


    /**
     * Verifies that resolving an attack makes the target lose HP.
     */
    @Test
    public void targetLosesHP() throws ActionExecutionException {
        int hpBefore = target.getCurrentHP();
        attackResolver.resolve();
        assertTrue("The target should lose HP", target.getCurrentHP() < hpBefore);
    }

    /**
     * Verifies that resolving an attack that knocks out the target returns true.
     */
    @Test
    public void resolve_attackThatKOs_returnsTrue() throws ActionExecutionException {
        target.applyDamage(target.getCurrentHP() - 1);
        attackResolver.resolve();

        assertTrue("The attack should knock out the target", attackResolver.isTargetFainted());
        assertFalse("The target should no longer be alive", target.isAlive());
    }

    /**
     * Verifies that resolving an attack that does not knock out the target returns false.
     */
    @Test
    public void resolve_attackThatDoesNotKO_returnsFalse() throws ActionExecutionException {
        attackResolver.resolve();

        assertFalse("The target should not be knocked out from full HP in one hit", attackResolver.isTargetFainted());
    }
}
