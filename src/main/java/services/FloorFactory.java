package services;

import models.tower.CombatStep;
import models.tower.Floor;
import models.tower.FloorStep;
import models.tower.RewardStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating floors with predefined step sequences.
 */
public class FloorFactory {

    /**
     * Creates a floor with a fixed sequence of combat and reward steps.
     *
     * @param floorNumber the floor identifier
     * @return the created floor
     */
    public Floor createFloor(final int floorNumber){
        final List<FloorStep> steps = new ArrayList<>();

        steps.add(new CombatStep(false));
        steps.add(new RewardStep());
        steps.add(new CombatStep(false));
        steps.add(new CombatStep(false));
        steps.add(new RewardStep());
        steps.add(new CombatStep(true));

        return new Floor(floorNumber, steps);
    }
}
