package models.tower;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for floor numbering and label formatting.
 */
public class FloorTest {

    /**
     * Verifies that a floor stores its number.
     */
    @Test
    public void floorShouldStoreItsNumber(){
        List<FloorStep> steps = new ArrayList<>();
        Floor floor = new Floor(4, steps);
        assertEquals(4, floor.floorNumber());
    }

    /**
     * Verifies that the floor label is formatted as expected.
     */
    @Test
    public void floorLabelShouldBeCorrect(){
        List<FloorStep> steps = new ArrayList<>();
        Floor floor = new Floor(4, steps);
        assertEquals("NO4", floor.getLabel());
    }

}
