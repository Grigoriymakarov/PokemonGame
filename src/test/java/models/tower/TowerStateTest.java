package models.tower;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link TowerState} class.
 *
 * <p>This test class verifies initialization, floor and step tracking,
 * navigation, and tower completion flags.</p>
 */
public class TowerStateTest {

    private TowerState towerState;
    private Floor startFloor;
    private FloorStep firstStep;
    private FloorStep secondStep;

    /**
     * Sets up a tower state with a starting floor containing several steps.
     */
    @Before
    public void setUp() {
        firstStep  = new CombatStep(false);
        secondStep = new RewardStep();
        startFloor = new Floor(2, List.of(firstStep, secondStep, new CombatStep(true)));
        towerState = new TowerState(startFloor);
    }

    /**
     * Verifies that the current floor is initialized to the starting floor.
     */
    @Test
    public void firstFloorShouldBeTheStartingFloor() {
        assertSame("The initial floor should be the starting floor",
                startFloor, towerState.getCurrentFloor());
    }

    /**
     * Verifies that the initial floor number matches the starting floor number.
     */
    @Test
    public void firstFloorNumberShouldBeTwo() {
        assertEquals("The initial floor number should be 2",
                2, towerState.getCurrentFloorNumber());
    }

    /**
     * Verifies that the current step is initialized to the first step of the floor.
     */
    @Test
    public void firstStepShouldBeTheStartingStep() {
        assertSame("The initial step should be the first step of the floor",
                firstStep, towerState.getCurrentStep());
    }

    /**
     * Verifies that the initial step index is zero.
     */
    @Test
    public void firstStepIndexShouldBeZero() {
        assertEquals("The initial step index should be 0",
                0, towerState.getCurrentStepIndex());
    }

    /**
     * Verifies that a floor is not complete while the current step is not past the last step.
     */
    @Test
    public void isFloorCompleteShouldBeFalseIfNotOnLastStep() {
        assertFalse("The floor should not be complete at the start",
                towerState.isFloorComplete());
    }

    /**
     * Verifies that a floor is complete after advancing past all its steps.
     */
    @Test
    public void isFloorCompleteShouldBeTrueAfterAllSteps() {
        for (int i = 0; i < startFloor.steps().size(); i++) {
            towerState.nextStep();
        }
        assertTrue("The floor should be complete after all steps",
                towerState.isFloorComplete());
    }

    /**
     * Verifies that moving to the next step increments the current step index.
     */
    @Test
    public void nextStepShouldIncrementIndex() {
        towerState.nextStep();
        assertEquals("The step index should be 1 after nextStep()",
                1, towerState.getCurrentStepIndex());
    }

    /**
     * Verifies that moving to the next step updates the current step.
     */
    @Test
    public void nextStepShouldUpdateCurrentStep() {
        towerState.nextStep();
        assertSame("The current step should be the second step after nextStep()",
                secondStep, towerState.getCurrentStep());
    }

    /**
     * Verifies that moving to the next floor updates the current floor number.
     */
    @Test
    public void advanceToNextFloorShouldIncrementFloorNumber() {
        Floor nextFloor = new Floor(3, List.of(new CombatStep(false)));
        towerState.advanceToNextFloor(nextFloor);
        assertEquals("The floor number should be 3 after nextFloor()",
                3, towerState.getCurrentFloorNumber());
    }

    /**
     * Verifies that moving to the next floor resets the current step index to zero.
     */
    @Test
    public void advanceToNextFloorShouldResetStepIndexToZero() {
        towerState.nextStep();
        Floor nextFloor = new Floor(3, List.of(new CombatStep(false)));
        towerState.advanceToNextFloor(nextFloor);
        assertEquals("The step index should be reset to 0 after nextFloor()",
                0, towerState.getCurrentStepIndex());
    }

    /**
     * Verifies that markAsWon marks the tower as over.
     */
    @Test
    public void markAsWonShouldSetIsOverTrue() {
        towerState.markAsWon();
        assertTrue("isOver should be true after markAsWon()",
                towerState.isOver());
    }

    /**
     * Verifies that markAsWon marks the tower as won.
     */
    @Test
    public void markAsWonShouldSetIsWonTrue() {
        towerState.markAsWon();
        assertTrue("isWon should be true after markAsWon()",
                towerState.isWon());
    }

    /**
     * Verifies that marking the tower as lost also marks it as over.
     */
    @Test
    public void markAsLostShouldSetIsOverToTrue() {
        towerState.markAsLost();
        assertTrue("isOver should be true after markAsLost()",
                towerState.isOver());
    }

    /**
     * Verifies that marking the tower as lost leaves the won flag false.
     */
    @Test
    public void markAsLostShouldSetIsWonToFalse() {
        towerState.markAsLost();
        assertFalse("isWon should be false after markAsLost()",
                towerState.isWon());
    }
}
