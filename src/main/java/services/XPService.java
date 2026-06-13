package services;

import constants.LogicConstants;
import models.shared.bugemon.TrainerBugemon;
import models.shared.statistics.Statistics;
import models.shared.xp.LevelUpEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Service responsible for calculating and distributing experience points (XP) after combat.
 *
 * <p>{@code XPService} manages the post-battle progression by:
 * <ul>
 *   <li>Calculating total XP rewards based on tower floor, boss status, and opponent count</li>
 *   <li>Evenly distributing XP among all participating Bugemon</li>
 *   <li>Collecting and returning level-up events for UI feedback</li>
 * </ul>
 *
 * <p>XP is typically distributed at the end of a combat step in the tower.</p>
 *
 * @see TrainerBugemon#gainXP(int)
 * @see LevelUpEvent
 */
public class XPService {

    /**
     * Distributes XP among a collection of participants and returns any level-up events.
     *
     * <p>Total XP is calculated via {@link #calculateTotalXP(int, boolean, int)} and
     * divided equally among all unique participants. Each participant then processes
     * its share, potentially triggering multiple level-ups.</p>
     *
     * <p>If the participants collection is empty, this method returns an empty list without
     * performing any calculations.</p>
     *
     * @param participants the collection of Bugemon that participated in the combat
     * @param floorNumber the current tower floor number (serves as base multiplier)
     * @param isBoss {@code true} if the combat was against a floor boss
     * @param opponentCount the number of opponents defeated
     * @return a list of level-up events generated during distribution (empty if no participants or no level-ups)
     */
    public List<LevelUpEvent> distributeXP(final Collection<TrainerBugemon> participants,
                                           final int floorNumber,
                                           final boolean isBoss,
                                           final int opponentCount) {
        final List<LevelUpEvent> levelUps = new ArrayList<>();

        if (participants.isEmpty()) return levelUps;

        final int totalXP = this.calculateTotalXP(floorNumber, isBoss, opponentCount);
        final int share = totalXP / participants.size();

        for (final TrainerBugemon bugemon : participants) {
            final int levelBefore = bugemon.getLevel();
            bugemon.gainXP(share);
            final int levelAfter = bugemon.getLevel();

            for (int level = levelBefore + 1; level <= levelAfter; level++) {
                levelUps.add(new LevelUpEvent(bugemon, level - 1, level));
            }
        }
        return levelUps;
    }

    /**
     * Calculates the total XP reward for a combat victory.
     *
     * <p>The formula follows: {@code 30 * floor * bossMultiplier * opponentCount}.
     * The boss multiplier is 2.0x if {@code isBoss} is true, otherwise 1.0x.</p>
     *
     * @param floorNumber the current tower floor
     * @param isBoss whether the fight was a boss encounter
     * @param opponentCount the number of defeated enemies
     * @return the total XP reward
     */
    public int calculateTotalXP(final int floorNumber, final boolean isBoss, final int opponentCount) {
        final int multiplier = isBoss ? 2 : 1;
        return LogicConstants.BASE_XP_REWARD * floorNumber * multiplier * opponentCount;
    }

    /**
     * Applies a level-up bonus to a Bugemon after it levels up.
     *
     * <p>This method centralizes the logic for applying stat bonuses when a Bugemon
     * levels up during level-up reward selection.</p>
     *
     * @param bugemon the Bugemon receiving the bonus
     * @param bonusStats the stat increases to apply
     * @throws NullPointerException if bugemon or bonusStats is null
     */
    public void applyLevelUpBonus(final TrainerBugemon bugemon, final Statistics bonusStats) {
        Objects.requireNonNull(bugemon, "Bugemon cannot be null");
        Objects.requireNonNull(bonusStats, "Bonus statistics cannot be null");
        bugemon.addLevelStats(bonusStats);
    }
}
