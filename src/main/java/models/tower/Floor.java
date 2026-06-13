package models.tower;

import constants.LabelConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a floor in the tower, consisting of a floor number and an ordered list of steps.
 *
 * <p>A {@code Floor} is defined by its unique floor number and a sequence of {@link FloorStep}
 * objects that represent the challenges or encounters on that floor. The class provides methods
 * to access the floor number, retrieve the steps, and generate display labels for the floor.</p>
 *
 * @see FloorStep
 */
public record Floor(int floorNumber, List<FloorStep> steps) {

    /**
     * Creates a floor with its number and step sequence.
     *
     * @param floorNumber the floor identifier
     * @param steps       the ordered list of steps for this floor
     */
    public Floor(final int floorNumber, final List<FloorStep> steps) {
        this.floorNumber = floorNumber;
        this.steps = new ArrayList<>(steps);
    }

    /**
     * Returns the display label for the given floor number (e.g. {@code "NO2"}).
     *
     * @param floorNumber the floor number
     * @return the floor label
     */
    public static String labelForNumber(final int floorNumber) {
        return LabelConstants.FLOOR_LABEL_PREFIX + floorNumber;
    }

    /**
     * Returns the display label of this floor (e.g. {@code "NO2"}).
     *
     * @return the floor label
     */
    public String getLabel() {
        return Floor.labelForNumber(this.floorNumber);
    }

    /**
     * Returns the step at the given index.
     *
     * @param index the index of the step
     * @return the step at the given index
     */
    public FloorStep getStep(final int index) {
        return this.steps.get(index);
    }


    /**
     * Returns the number of steps in this floor.
     *
     * @return the number of steps
     */
    public int size() {
        return this.steps.size();
    }
}
