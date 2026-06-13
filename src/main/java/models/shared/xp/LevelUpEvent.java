package models.shared.xp;

import models.shared.bugemon.TrainerBugemon;

/**
 * Event triggered when a Bugemon gains enough experience to reach a new level.
 * <p>
 * This record captures the state change of a Bugemon, storing its previous
 * and new level to allow UI components to display level-up notifications.
 * </p>
 * @param bugemon  The Bugemon that leveled up.
 * @param oldLevel The level of the Bugemon before gaining XP.
 * @param newLevel The level reached after gaining XP.
 */
public record LevelUpEvent(TrainerBugemon bugemon, int oldLevel, int newLevel) {
}
