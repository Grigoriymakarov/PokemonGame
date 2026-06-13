package models.combat;

import models.combat.event.AttackEvent;
import models.combat.event.KoEvent;
import models.shared.Type;
import models.shared.TypeEffectiveness;
import models.shared.bugemon.BugemonSpecie;
import models.shared.bugemon.TrainerBugemon;
import models.shared.statistics.Statistics;
import models.shared.trainer.Enemy;
import models.shared.trainer.Player;
import models.team.Team;
import org.junit.Before;
import org.junit.Test;
import services.combat.strategies.RandomOffenseStrat;
import services.combat.strategies.FirstAliveStrategy;
import services.combat.strategies.ReplacementStrategy;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link CombatState} class.
 *
 * <p>Tests message logging, combat state tracking, and active Bugemon management.</p>
 */
public class CombatStateTest {
    private TrainerBugemon player1;
    private TrainerBugemon player2;
    private TrainerBugemon enemy1;
    private TrainerBugemon enemy2;
    private CombatState state;
    private Player playerTrainer;
    private Enemy enemyTrainer;

    /**
     * Sets up the test fixtures before each test.
     */
    @Before
    public void setUp() {
        BugemonSpecie b1 = new BugemonSpecie.Builder()
                .id("florachu")
                .name("Florachu")
                .type(Type.FLORA)
                .baseStats(new Statistics(100, 50, 40, 30))
                .attacks(null)
                .sprite("florachu.png")
                .starter(true)
                .build();

        BugemonSpecie b2 = new BugemonSpecie.Builder()
                .id("exceflam")
                .name("Exceflam")
                .type(Type.PYRO)
                .baseStats(new Statistics(85, 65, 40, 50))
                .attacks(null)
                .sprite("exceflam.png")
                .starter(true)
                .build();

        BugemonSpecie b3 = new BugemonSpecie.Builder()
                .id("moussil")
                .name("Moussil")
                .type(Type.FLORA)
                .baseStats(new Statistics(80, 60, 45, 35))
                .attacks(null)
                .sprite("moussil.png")
                .starter(false)
                .build();

        BugemonSpecie b4 = new BugemonSpecie.Builder()
                .id("pyricore")
                .name("Pyricore")
                .type(Type.PYRO)
                .baseStats(new Statistics(75, 70, 35, 60))
                .attacks(null)
                .sprite("pyricore.png")
                .starter(false)
                .build();
        player1 = new TrainerBugemon.Builder().withSpecie(b1).build();
        player2 = new TrainerBugemon.Builder().withSpecie(b2).build();
        enemy1 = new TrainerBugemon.Builder().withSpecie(b3).build();
        enemy2 = new TrainerBugemon.Builder().withSpecie(b4).build();


        playerTrainer = new Player("player");
        ReplacementStrategy replacementStrategy = new FirstAliveStrategy();
        enemyTrainer = new Enemy("enemy", new RandomOffenseStrat(), replacementStrategy);
        Team playerTrainerTeam = new Team();
        Team enemyTrainerTeam = new Team();
        playerTrainerTeam.add(player1);
        playerTrainerTeam.add(player2);
        enemyTrainerTeam.add(enemy1);
        enemyTrainerTeam.add(enemy2);
        playerTrainer.setTeam(playerTrainerTeam);
        enemyTrainer.setTeam(enemyTrainerTeam);

        state = new CombatState(playerTrainer, enemyTrainer);
    }

    /**
     * Verifies that adding a message is stored correctly.
     */
    @Test
    public void addMessageShouldGenerateMessages() {
        state.addEvent(new AttackEvent("Florachu", "attaque", "cible", 10,
                TypeEffectiveness.NEUTRAL, false));
        assertEquals(1, state.getEvents().size());
        assertTrue(state.getEvents().get(0) instanceof AttackEvent);
    }

    /**
     * Verifies that multiple messages are kept in order.
     */
    @Test
    public void addMessagesShouldKeepOrder() {
        state.addEvent(new AttackEvent("Florachu", "attaque1", "cible", 10,
                TypeEffectiveness.NEUTRAL, false));
        state.addEvent(new KoEvent("Exceflam"));

        assertEquals(2, state.getEvents().size());
        assertTrue(state.getEvents().get(0) instanceof AttackEvent);
        assertTrue(state.getEvents().get(1) instanceof KoEvent);
    }

    /**
     * Verifies that clearing messages empties the message list.
     */
    @Test
    public void clearMessagesShouldEmptyListofMessages() {
        state.addEvent(new AttackEvent("Florachu", "attaque", "cible", 10,
                TypeEffectiveness.NEUTRAL, false));
        state.clearEvents();
        assertTrue(state.getEvents().isEmpty());
    }

    /**
     * Verifies that clearing an empty message list does nothing.
     */
    @Test
    public void clearMessagesOnEmptyListShouldDoNothing() {
        state.clearEvents();
        assertTrue(state.getEvents().isEmpty());
    }

    /**
     * Verifies that setting combat over to true marks the combat as finished.
     */
    @Test
    public void setCombatOverToTrueShouldMarkCombatAsFinished() {
        state.setCombatOver(true);
        assertTrue(state.isCombatOver());
    }

    /**
     * Verifies that setting combat over to false marks the combat as not finished.
     */
    @Test
    public void setCombatOverToTrueShouldMarkCombatAsNotFinished() {
        state.setCombatOver(false);
        assertFalse(state.isCombatOver());
    }

    /**
     * Verifies that setting player won to true marks the player as the winner.
     */
    @Test
    public void setPlayerWonToTrueShouldMarkPlayerAsWinner() {
        state.setCombatOver(true);
        state.setPlayerWon(true);
        assertTrue(state.isPlayerWon());
    }

    /**
     * Verifies that setting player won to false marks the player as the loser.
     */
    @Test
    public void setPlayerWonToFalseShouldMarkPlayerAsLoser() {
        state.setCombatOver(true);
        state.setPlayerWon(false);
        assertFalse(state.isPlayerWon());
    }

    /**
     * Verifies that changing the active player Bugemon works correctly.
     */
    @Test
    public void setPlayerShouldChangeActiveBugemon() {
        state.setPlayerActiveBugemon(player2);
        assertSame(player2, state.getPlayerActiveBugemon());
    }

    /**
     * Verifies that changing the active enemy Bugemon works correctly.
     */
    @Test
    public void setEnemyShouldChangeActiveBugemon() {
        state.setEnemyActiveBugemon(enemy2);
        assertSame(enemy2, state.getEnemyActiveBugemon());
    }
}
