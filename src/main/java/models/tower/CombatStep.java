package models.tower;

/**
 * Represents a combat encounter step in a tower floor.
 *
 * <p>This class extends {@link FloorStep} and represents a specific combat encounter
 * that the player must complete. A combat step can be either a regular combat
 * encounter or a boss encounter, depending on the {@code isBoss} flag.</p>
 *
 * <p>Boss encounters are typically harder and may have special properties or rewards
 * compared to regular combat steps.</p>
 *
 * @see FloorStep
 * @see StepType
 */
public class CombatStep extends FloorStep {

    /**
     * Creates a combat step, configured as a boss encounter when requested.
     *
     * <p>The step type is automatically determined based on the {@code isBoss} flag:
     * if {@code true}, the step type is set to {@link StepType#BOSS};
     * otherwise, it is set to {@link StepType#COMBAT}.</p>
     *
     * @param isBoss {@code true} for a boss step; {@code false} for a regular combat step
     */
    public CombatStep(final boolean isBoss){
        super(isBoss ? StepType.BOSS : StepType.COMBAT);
    }

}
