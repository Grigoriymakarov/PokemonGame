package services.combat;

import models.combat.CombatState;
import models.exceptions.EffectApplicationException;
import models.shared.Effect;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the {@link EffectResolver} class.
 *
 * <p>This test class verifies effect application and expiration behavior
 * during combat.</p>
 */
public class EffectResolverTest {
    private TrainerBugemon player;
    private TrainerBugemon enemy;
    private CombatState state;
    private EffectResolver resolver;

    /**
     * Sets up a combat state with one player Bugemon and one enemy Bugemon before each test.
     */
    @Before
    public void setUp() {
        BugemonSpecie playerSpecie = new BugemonSpecie.Builder()
                .id("florachu")
                .name("Florachu")
                .type(Type.FLORA)
                .baseStats(new Statistics(100, 50, 40, 30))
                .attacks(new ArrayList<>())
                .sprite("florachu.png")
                .starter(true)
                .build();
        BugemonSpecie enemySpecie = new BugemonSpecie.Builder()
                .id("pyricore")
                .name("Pyricore")
                .type(Type.PYRO)
                .baseStats(new Statistics(90, 45, 35, 25))
                .attacks(new ArrayList<>())
                .sprite("pyricore.png")
                .starter(false)
                .build();

        player = new TrainerBugemon.Builder().withSpecie(playerSpecie).build();
        enemy = new TrainerBugemon.Builder().withSpecie(enemySpecie).build();
        Player playerTrainer = new Player("player");
        ReplacementStrategy replacementStrategy = new FirstAliveStrategy();
        Enemy enemyTrainer = new Enemy("enemy", new RandomOffenseStrat(), replacementStrategy);
        Team playerTrainerTeam = new Team();
        Team enemyTrainerTeam = new Team();
        playerTrainerTeam.add(player);
        enemyTrainerTeam.add(enemy);
        playerTrainer.setTeam(playerTrainerTeam);
        enemyTrainer.setTeam(enemyTrainerTeam);
        state = new CombatState(playerTrainer, enemyTrainer);
        resolver = new EffectResolver(null, state, null, null);
    }

    /**
     * Verifies that a single-turn stat modifier applies immediately and expires at end of turn.
     */
    @Test
    public void singleTurnStatModifierShouldApplyThenExpireAtEndOfTurn() throws EffectApplicationException {
        Effect effect = new Effect(
            Effect.EffectType.STAT_MODIFIER,
            Effect.TargetType.USER,
            Statistics.Stat.ATTACK,
            Effect.DurationType.SINGLE_TURN,
            Optional.of(5),
            Optional.empty()
        );

        EffectResolver effectResolver = new EffectResolver(java.util.List.of(effect), state, player, enemy);
        effectResolver.apply();

        assertEquals(55, player.getStatistics().getAttack());

        state.applyEndOfTurnTicks();

        assertEquals(50, player.getStatistics().getAttack());
    }
}
