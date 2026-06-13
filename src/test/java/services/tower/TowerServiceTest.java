package services.tower;

import constants.LogicConstants;
import models.tower.TowerState;
import org.junit.Before;
import org.junit.Test;
import repositories.AttackRepository;
import repositories.BugemonRepository;
import services.TowerService;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link TowerService} class.
 *
 * <p>This test class verifies initial tower state creation, step and floor
 * progression, loss handling, and team reset behavior.</p>
 */
public class TowerServiceTest {

    private TowerService towerService;

    /**
     * Builds repository data and creates tower-related services before each test.
     *
     * @throws IOException if repository data cannot be loaded
     */
    @Before
    public void setUp() throws IOException {
        BugemonRepository.getInstance().buildDataBase();
        towerService = new TowerService();
    }

    /**
     * Verifies that the initial tower state starts at the minimum floor.
     */
    @Test
    public void createInitialState_startsAtFloor1() {
        TowerState state = towerService.createInitialState();
        assertEquals("The initial state should start at the minimum floor",
                LogicConstants.TOWER_MIN_FLOOR, state.getCurrentFloorNumber());
    }

    /**
     * Verifies that advancing to the next floor returns true when the tower is not complete.
     */
    @Test
    public void advanceToNextFloor_notComplete_returnsTrue() {
        TowerState state = towerService.createInitialState();
        boolean advanced = towerService.advanceToNextFloor(state);
        assertTrue("Advancing to the next floor should return true when the tower is not over",
                advanced);
    }

    /**
     * Verifies that advancing to the next floor increases the floor number.
     */
    @Test
    public void advanceToNextFloor_notComplete_floorNumberIncreases() {
        TowerState state = towerService.createInitialState();
        int floorBefore = state.getCurrentFloorNumber();
        towerService.advanceToNextFloor(state);
        assertEquals("The floor number should increase by 1",
                floorBefore + 1, state.getCurrentFloorNumber());
    }

    /**
     * Verifies that advancing from the maximum floor returns false.
     */
    @Test
    public void advanceToNextFloor_atMaxFloor_returnsFalse() {
        TowerState state = towerService.createInitialState();
        while (!state.isTowerComplete()) {
            towerService.advanceToNextFloor(state);
        }

        boolean advanced = towerService.advanceToNextFloor(state);
        assertFalse("Advancing from the last floor should return false", advanced);
    }

    /**
     * Verifies that advancing after the maximum floor marks the tower as over.
     */
    @Test
    public void advanceToNextFloor_atMaxFloor_towerIsOver() {
        TowerState state = towerService.createInitialState();
        while (!state.isTowerComplete()) {
            towerService.advanceToNextFloor(state);
        }
        towerService.advanceToNextFloor(state);

        assertTrue("The tower should be over after the last floor", state.isOver());
    }

}
