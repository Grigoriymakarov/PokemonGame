package models.tower;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for floor step type and boss flag behavior.
 */
public class FloorStepTest {

    /**
     * Verifies that a normal combat step uses the COMBAT type.
     */
    @Test
    public void normalCombatShouldHaveTypeCombat(){
        CombatStep combat = new CombatStep(false);
        assertEquals(StepType.COMBAT, combat.getStepType());
    }

    /**
     * Verifies that a boss combat step uses the BOSS type.
     */
    @Test
    public void bossCombatShouldHaveTypeBoss(){
        CombatStep boss = new CombatStep(true);
        assertEquals(StepType.BOSS, boss.getStepType());
    }

    /**
     * Verifies that a reward step uses the REWARD type.
     */
    @Test
    public void rewardStepShouldHaveTypeReward(){
        RewardStep reward = new RewardStep();
        assertEquals(StepType.REWARD, reward.getStepType());
    }

    /**
     * Verifies that a normal combat step does not have the BOSS type.
     */
    @Test
    public void normalCombatShouldNotBeBoss(){
        CombatStep combat = new CombatStep(false);
        assertFalse(combat.getStepType() == StepType.BOSS);
    }

    /**
     * Verifies that a boss combat step has the BOSS type.
     */
    @Test
    public void bossCombatShouldNotBeNormal(){
        CombatStep boss = new CombatStep(true);
        assertTrue(boss.getStepType() == StepType.BOSS);
    }
}
