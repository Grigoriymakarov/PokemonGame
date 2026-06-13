package services.tower;

import models.tower.Floor;
import models.tower.FloorStep;
import models.tower.RewardStep;
import models.tower.StepType;
import org.junit.Test;
import services.FloorFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the {@link FloorFactory} class.
 *
 * <p>This test class verifies the sequence of steps created for a floor.</p>
 */
public class FloorFactoryTest {

    /**
     * Verifies that a created floor contains the expected step sequence.
     */
    @Test
    public void createFloor_shouldHaveCorrectSequence() {
        FloorFactory factory = new FloorFactory();
        Floor floor = factory.createFloor(1);

        List<FloorStep> steps = floor.steps();

        assertEquals("The floor should have 6 steps", 6, steps.size());
        assertTrue("The last step should be a boss combat",
                steps.get(5).getStepType() == StepType.BOSS);
        assertTrue("The second step should be a reward",
                steps.get(1) instanceof RewardStep);
    }
}
