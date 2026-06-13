package models.tower;

import constants.LabelConstants;
import constants.LogicConstants;
import java.util.Objects;

/**
 * Holds the mutable state of a tower run: current floor, current step, and end-of-game flags.
 */
public class TowerState {

    private Floor currentFloor;
    private int currentStepIndex;
    private boolean isOver;
    private boolean isWon;


    /**
     * Creates a new tower state starting at the given floor.
     *
     * @param currentfloor the initial floor (step index starts at 0)
     */
    public TowerState(final Floor currentfloor){
        this.currentFloor = Objects.requireNonNull(currentfloor, "currentFloor cannot be null");
        this.currentStepIndex = 0;
        this.isOver = false;
        this.isWon = false;
    }

    /**
     * Returns the current floor instance.
     *
     * @return the current floor
     */
    public Floor getCurrentFloor(){
        return this.currentFloor;
    }

    /**
     * Returns the current floor number shown in the UI.
     *
     * @return the current floor number
     */
    public int getCurrentFloorNumber(){
        return this.currentFloor.floorNumber();
    }

    /**
     * Returns the current step on the current floor.
     *
     * @return the current step
     */
    public FloorStep getCurrentStep(){
        return this.currentFloor.getStep(this.currentStepIndex);
    }
    /**
     * Returns the index of the current step.
     *
     * @return the current step index
     */
    public int getCurrentStepIndex(){
        return this.currentStepIndex;
    }

    /**
     * Indicates whether all steps on the current floor have been completed.
     *
     * @return true if the current floor is complete
     */
    public boolean isFloorComplete(){
        return this.currentStepIndex >= this.currentFloor.size();
    }

    /**
     * Indicates whether the tower has been completed (current floor is MAX_FLOOR ({@value constants.LogicConstants#TOWER_MAX_FLOOR})).
     *
     * @return true if the tower is complete
     */
    public boolean isTowerComplete(){
        return this.currentFloor.floorNumber()== LogicConstants.TOWER_MAX_FLOOR;
    }


    /**
     * Indicates whether the run is over (won or lost).
     *
     * @return true if the run is over
     */
    public boolean isOver() {
        return this.isOver;
    }

    /**
     * Indicates whether the run was won.
     *
     * @return true if the run is won
     */
    public boolean isWon() {
        return this.isWon;
    }

    /**
     * Advances to the next step on the current floor.
     */
    public void nextStep(){
        this.currentStepIndex++;
    }


    /**
     * Advances to the next floor and resets the step index.
     *
     * @param nextFloor the next floor to move to
     */
    public void advanceToNextFloor(final Floor nextFloor){
        this.currentFloor = nextFloor;
        this.currentStepIndex = 0;
    }

    /**
     * Marks the run as won and over.
     */
    public void markAsWon(){
        this.isOver = true;
        this.isWon = true;
    }

    /**
     * Marks the run as lost and over.
     */
    public void markAsLost(){
        this.isOver = true;
        this.isWon = false;
    }

    /**
     * Returns the type of the current step.
     *
     * @return the current step type
     */
    public StepType getCurrentStepType() {
        return this.getCurrentStep().getStepType();
    }

    /**
     * Returns the display label of the current floor (e.g. {@code "NO2"}).
     *
     * @return the floor label
     */
    public String getFloorLabel() {
        return this.currentFloor.getLabel();
    }

    /**
     * Returns the display label for the current step.
     *
     * <p>Examples: {@code "Combat 1"}, {@code "Combat 3"}, {@code "Boss"}, {@code "Récompense 1"}.</p>
     *
     * @return the step label shown in tower UI headers
     */
    public String getStepLabel() {
        return switch (this.getCurrentStepType()) {
            case COMBAT -> String.format(LabelConstants.LABEL_STEP_COMBAT, this.getStepNumber());
            case BOSS   -> LabelConstants.LABEL_STEP_BOSS;
            case REWARD -> String.format(LabelConstants.LABEL_STEP_REWARD, this.getStepNumber());
        };
    }

    /**
     * Returns the label of the floor that follows the current one (e.g. {@code "NO5"}).
     *
     * <p>Only meaningful when {@link #isTowerComplete()} returns {@code false}.</p>
     *
     * @return the display label of the next floor
     */
    public String getNextFloorLabel() {
        return Floor.labelForNumber(this.currentFloor.floorNumber() + 1);
    }

    /**
     * Returns the ordinal of the current step among steps of the same type on this floor.
     *
     * <p>For example, the second COMBAT step returns {@code 2}.</p>
     */
    private int getStepNumber() {
        final StepType type = this.getCurrentStepType();
        int count = 0;
        for (int i = 0; i <= this.currentStepIndex; i++) {
            if (this.currentFloor.getStep(i).getStepType() == type) {
                count++;
            }
        }
        return count;
    }
}
