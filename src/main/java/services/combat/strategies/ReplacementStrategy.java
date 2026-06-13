package services.combat.strategies;

import dto.TeamDTO;
import models.exceptions.NoAvailableReplacerException;
import models.shared.bugemon.TrainerBugemon;

/**
 * Strategy interface used to choose a replacement Bugemon after a switch or knock-out.
 *
 * <p>Implementations receive the team members and decide which Bugemon should
 * become active next.</p>
 *
 * @see TrainerBugemon
 */
public interface ReplacementStrategy {

    /**
     * Chooses the Bugemon that should replace the current active one.
     *
     * @param team the team from which a replacement must be chosen
     * @return the chosen replacement Bugemon
     * @throws NoAvailableReplacerException if the team has no alive Bugemon to choose from
     */
    TrainerBugemon chooseBugemon(TeamDTO team) throws NoAvailableReplacerException;
}
