package models.team;

import models.exceptions.BugemonNotFoundException;
import models.shared.bugemon.BugemonSpecie;
import services.BugemonService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory that creates a team of randomly selected Bugemons.
 *
 * <p>This concrete {@link TeamFactory} shuffles the complete list
 * of available Bugemons and selects the first {@code bugemonNumber}.
 * This ensures there are never duplicates in the generated team.</p>
 *
 * <p>It is used by {@link controllers.shared.MetaController} to automatically
 * create the opposing team before each battle.</p>
 *
 * @see TeamFactory
 * @see controllers.shared.MetaController
 */
public final class RandomTeamFactory extends TeamFactory{

    private final BugemonService bugemonService;

    public RandomTeamFactory(final BugemonService bugemonService) {
        this.bugemonService = bugemonService;
    }

    /**
     * Creates a random team of {@code bugemonNumber} Bugemons.
     *
     * <p>Bugemons are randomly selected from the full list of available Bugemons.</p>
     *
     * @param bugemonNumber the number of Bugemons to include in the team
     * @return a {@link Team} containing {@code bugemonNumber} distinct Bugemons
     */
    @Override
    public Team createTeam(final int bugemonNumber) {
        final List<BugemonSpecie> bugemons = new ArrayList<>(bugemonService.getAllBugemons());
        Collections.shuffle(bugemons);

        final Team team = new Team();
        try {
            for (final BugemonSpecie specie : bugemons.subList(0, Math.min(bugemonNumber, bugemons.size()))) {
                team.add(this.bugemonService.createTrainerBugemon(specie.id()));
            }
        } catch (final BugemonNotFoundException e) {
            // This should never happen since we are using valid IDs from the repository
            throw new IllegalStateException("Unexpected error: Bugemon not found during random team creation", e);
        }
        return team;
    }
}
