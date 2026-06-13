package services;

import constants.LogicConstants;
import models.tower.*;

/**
 * Service responsible for managing tower progression and preparing combat state.
 *
 * <p>This service creates the initial tower state and advances the player
 * through steps and floors.</p>
 */
public class TowerService {

    private final FloorFactory floorFactory;

    /**
     * Creates a tower service with the given floor factory.
     *
     * @param floorFactory the factory used to create floors
     */
    public TowerService(final FloorFactory floorFactory) {
        this.floorFactory = floorFactory;
    }

    /**
     * Creates a tower service backed by a default floor factory.
     */
    public TowerService() {
        this(new FloorFactory());
    }


    /**
     * Creates the initial state of the tower at the minimum floor.
     *
     * @return a new {@link TowerState} positioned on the first floor
     */
    public TowerState createInitialState() {
        final Floor startFloor = this.floorFactory.createFloor(LogicConstants.TOWER_MIN_FLOOR);
        return new TowerState(startFloor);
    }


    /**
     * Advances the tower state to the next floor.
     *
     * <p>If the tower is already complete, the state is advanced with no next floor
     * and the method returns {@code false}.</p>
     *
     * @param towerState the current tower state
     * @return {@code true} if a new floor was created, {@code false} if the tower is complete
     */
    public boolean advanceToNextFloor(final TowerState towerState) {
        if (towerState.isTowerComplete()) {
            towerState.markAsWon();
            return false;
        }
        final int nextFloorNumber = towerState.getCurrentFloorNumber() + 1;
        final Floor nextFloor = this.floorFactory.createFloor(nextFloorNumber);
        towerState.advanceToNextFloor(nextFloor);
        return true;
    }


}

