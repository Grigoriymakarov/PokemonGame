package models.tower;

/**
 * Base class for all steps that can appear on a floor.
 */
public abstract class FloorStep {
    private final StepType stepType;

    /**
     * Creates a floor step of the given type.
     *
     * @param stepType the step type
     */
    public FloorStep(final StepType stepType){
        this.stepType = stepType;
    }

    /**
     * Returns the type of this step.
     *
     * @return the step type
     */
    public StepType getStepType(){
        return this.stepType;
    }

}

