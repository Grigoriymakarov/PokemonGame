package dto;

import models.combat.event.CombatEvent;
import models.shared.Attack;
import models.shared.Type;

import java.util.List;

/**
 * Data Transfer Object interface for exposing combat state information without allowing modifications.
 *
 * <p>This interface provides a read-only view of the current state of a combat, including
 * information about the player's Bugemon, the enemy's Bugemon, their statistics, and the
 * combat history. Implementations should not provide methods to modify these values.</p>
 *
 * @see models.combat.CombatState
 */
public interface CombatStateDTO {
    //Getters
    /**
     * Returns the player Bugemon name.
     * @return the player Bugemon name*/
    String getPlayerName();

    /**
     * Returns the enemy Bugemon name.
     * @return the enemy Bugemon name*/
    String getEnemyName();

    /**
     * Returns current HP of the player's Bugemon.
     * @return current HP of the player's Bugemon*/
    int getPlayerHp();

    /**
     * Returns current HP of the enemy Bugemon.
     * @return current HP of the enemy Bugemon*/
    int getEnemyHp();

    /**
     * Returns maximum HP of the enemy Bugemon.
     * @return maximum HP of the enemy Bugemon*/
    int getEnemyHpMax();

    List<CombatEvent> getEvents();

    /**
     * Returns maximum HP of the player's Bugemon.
     * @return maximum HP of the player's Bugemon*/
    int getPlayerHpMax();

    /**
     * Returns sprite path of the player's Bugemon.
     * @return sprite path of the player's Bugemon*/
    String getPlayerSpritePath();

    /**
     * Returns sprite path of the enemy's Bugemon.
     * @return sprite path of the enemy's  Bugemon*/
    String getEnemySpritePath();

    /**
     * Returns the floor label shown in the first line of the combat header (e.g. {@code "Etage NO2"}).
     * Returns {@link constants.LabelConstants#LABEL_FREE_COMBAT} for free combat.
     *
     * @return the floor label
     */
    String getFloorLabel();

    /**
     * Returns the step label shown in the second line of the combat header
     * (e.g. {@code "Combat 1"}, {@code "Boss"}). Empty for free combat.
     *
     * @return the step label
     */
    String getStepLabel();

    /**
     * Checks if the current fight is a boss encounter.
     *
     * @return {@code true} if the current step is a boss fight
     */
    boolean isBossFight();

    /**
     * Returns the level of the player's Bugemon.
     * @return the level of the player's Bugemon*/
    int getPlayerLevel();

    /**
     * Returns the level of the enemy Bugemon.
     * @return the level of the enemy Bugemon*/
    int getEnemyLevel();

    /**
     * Returns the type of the player's Bugemon.
     * @return the type of the player's Bugemon.
     */
    Type getPlayerType();

    /**
     * Returns the type of the enemy's Bugemon.
     * @return the type of the enemy's Bugemon.
     */
    Type getEnemyType();

    /**
     * Returns the list of the current attacks available to the enemy's active Bugemon.
     * @return a list of the current attacks available to the enemy's active Bugemon.
     */
    List<Attack> getEnemyBugemonCurrentAttacks();
}
