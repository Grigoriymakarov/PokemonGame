package dto;

import models.shared.Type;
import models.shared.bugemon.BugemonSpecie;
import models.shared.statistics.Statistics;

/**
 * Data Transfer Object interface for exposing Bugemon information without allowing modifications.
 *
 * <p>This interface provides a read-only view of a Bugemon belonging to a trainer.
 * It exposes information such as the Bugemon's species, level, statistics, name,
 * and experience points. Implementations should not provide methods to modify these values.</p>
 *
 * @see models.shared.bugemon.TrainerBugemon
 */
public interface TrainerBugemonDTO {


    /**
     * Gets the display name for this Bugemon.
     *
     * @return the display name (custom name if set, otherwise species name)
     */
    String getDisplayName();

    /**
     * Gets the current level of this Bugemon.
     *
     * @return the level (typically 1-100)
     */
    int getLevel();

    /**
     * Gets the species this Bugemon belongs to.
     *
     * @return the BugemonSpecie
     */
    BugemonSpecie getSpecie();

    /**
     * Gets the primary type of this Bugemon's species.
     *
     * @return the species type
     */
    Type getSpecieType();

    /**
     * Gets the maximum HP for this Bugemon.
     *
     * @return the maximum HP value
     */
    int getMaxHp();

    /**
     * Gets the value of a specific stat for this Bugemon.
     *
     * @param stat the stat to retrieve
     * @return the stat value
     */
    int getStat(Statistics.Stat stat);

    /**
     * Gets the current experience points of this Bugemon.
     *
     * @return the experience points
     */
    int getXP();

    /**
     * Gets the experience points needed to reach the next level.
     *
     * @return the XP required for the next level
     */
    int getXPToNextLevel();

    /**
     * Gets the custom name of this Bugemon (if set by the trainer).
     *
     * @return the custom name, or null if no custom name is set
     */
    String getName();

    /**
     * Gets the current HP of this Bugemon.
     *
     * @return the current HP
     */
    int getCurrentHP();
}
