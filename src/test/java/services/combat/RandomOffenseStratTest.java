package services.combat;

import models.combat.CombatState;
import models.exceptions.CouldNotDetermineActionException;
import models.shared.Attack;
import models.shared.Type;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link RandomOffenseStrat} class.
 *
 * <p>Tests action selection, attack validation, and error handling.</p>
 */
public class RandomOffenseStratTest {

    private CombatState combatState;
    private RandomOffenseStrat strat;
    private List<Attack> availableAttacks;

    /**
     * Sets up the test fixtures before each test.
     */
    @Before
    public void setUp() {
        strat = new RandomOffenseStrat();

        Attack attack1 = new Attack.Builder()
                .id("att1")
                .name("1")
                .type(Type.FLORA)
                .description("")
                .power(40)
                .effects(new ArrayList<>())
                .build();

        Attack attack2 = new Attack.Builder()
                .id("att2")
                .name("2")
                .type(Type.FLORA)
                .description("")
                .power(35)
                .effects(new ArrayList<>())
                .build();

        Attack attack3 = new Attack.Builder()
                .id("att3")
                .name("3")
                .type(Type.FLORA)
                .description("")
                .power(50)
                .effects(new ArrayList<>())
                .build();
        availableAttacks = new ArrayList<>(List.of(attack1, attack2, attack3));

        BugemonSpecie specie = new BugemonSpecie.Builder()
                .id("florachu")
                .name("Florachu")
                .type(Type.FLORA)
                .baseStats(new Statistics(100, 50, 30, 10))
                .attacks(availableAttacks)
                .sprite("florachu.png")
                .starter(true)
                .build();

        TrainerBugemon enemy = new TrainerBugemon.Builder()
                .withSpecie(specie)
                .build();

        TrainerBugemon player = new TrainerBugemon.Builder()
                .withSpecie(specie)
                .build();

        Player playerTrainer = new Player("player");
        ReplacementStrategy replacementStrategy = new FirstAliveStrategy();
        Enemy enemyTrainer = new Enemy("enemy", new RandomOffenseStrat(), replacementStrategy);
        Team playerTrainerTeam = new Team();
        Team enemyTrainerTeam = new Team();
        playerTrainerTeam.add(player);
        enemyTrainerTeam.add(enemy);
        playerTrainer.setTeam(playerTrainerTeam);
        enemyTrainer.setTeam(enemyTrainerTeam);

        combatState = new CombatState(playerTrainer, enemyTrainer);
    }


    /**
     * Verifies that chooseAttack returns an Attack instance.
     */
    @Test
    public void chooseAttackShouldReturnAttack() throws CouldNotDetermineActionException {
        Attack attack = strat.chooseAttack(combatState);
        assertNotNull(attack);
    }


    /**
     * Verifies that chooseAttack returns an attack from the available attacks.
     */
    @Test
    public void chooseAttackShouldReturnAttackFromAvailableAttacks() throws CouldNotDetermineActionException {
        Attack attack = strat.chooseAttack(combatState);
        assertTrue(availableAttacks.contains(attack));
    }


    /**
     * Verifies that chooseAttack throws an exception when no attacks are available.
     */
    @Test(expected = CouldNotDetermineActionException.class)
    public void chooseAttackWithNoAttacksShouldThrowException() throws CouldNotDetermineActionException {
        BugemonSpecie specieNoAttacks = new BugemonSpecie.Builder()
                .id("florachu")
                .name("Florachu")
                .type(Type.FLORA)
                .baseStats(new Statistics(100, 50, 30, 10))
                .attacks(new ArrayList<>())
                .sprite("florachu.png")
                .starter(true)
                .build();

        TrainerBugemon enemyNoAttacks = new TrainerBugemon.Builder()
                .withSpecie(specieNoAttacks)
                .build();

        Player playerTrainer = new Player("player");
        ReplacementStrategy replacementStrategy = new FirstAliveStrategy();
        Enemy enemyTrainer = new Enemy("enemy", new RandomOffenseStrat(), replacementStrategy);
        Team playerTrainerTeam = new Team();
        Team enemyTrainerTeam = new Team();
        playerTrainerTeam.add(enemyNoAttacks);
        enemyTrainerTeam.add(enemyNoAttacks);
        playerTrainer.setTeam(playerTrainerTeam);
        enemyTrainer.setTeam(enemyTrainerTeam);
        CombatState stateNoAttacks = new CombatState(playerTrainer, enemyTrainer);

        strat.chooseAttack(stateNoAttacks);
    }
}
