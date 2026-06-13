package services.combat.strategies;

import dto.TeamDTO;
import models.exceptions.NoAvailableReplacerException;
import models.shared.bugemon.TrainerBugemon;

import java.util.List;

/**
 * Replacement strategy that selects the first alive Bugemon in a team.
 *
 * <p>This implementation filters the team to keep only alive Bugemon and returns the
 * first one in list order. It is deterministic and therefore suitable for simple
 * automatic replacements on the enemy side.</p>
 *
 * @see ReplacementStrategy
 */
public class FirstAliveStrategy implements ReplacementStrategy{

    /**
     * Returns the first alive Bugemon found in the supplied team.
     *
     * @param team the team from which a replacement must be chosen
     * @return the first alive Bugemon in the list
     */
    @Override
    public TrainerBugemon chooseBugemon(final TeamDTO team) throws NoAvailableReplacerException {
        List<TrainerBugemon> aliveMembers = team.getAliveMembers();
        return aliveMembers.stream().findFirst().orElseThrow(NoAvailableReplacerException::new);
    }

}
