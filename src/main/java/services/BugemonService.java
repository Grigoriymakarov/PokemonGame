package services;

import models.exceptions.BugemonNotFoundException;
import models.exceptions.ElementNotFoundException;
import models.shared.bugemon.BugemonSpecie;
import models.shared.bugemon.TrainerBugemon;
import repositories.AttackRepository;
import repositories.BugemonRepository;

import java.util.List;

/**
 * Service managing business operations on Bugemons.
 *
 * <p>This service acts as a bridge between repositories (raw data)
 * and controllers (application logic). It provides two main functionalities:</p>
 * <ol>
 *   <li>Access to the complete list of available Bugemons</li>
 *   <li>Creation of independent Bugemon instances for battles,
 *       using a deep copy (deep clone)</li>
 * </ol>
 *
 * <p>The method {@link #createTrainerBugemon(String)} is central:
 * it ensures that each Bugemon in combat has its own
 * statistics and attacks, independent of the template stored in the repository.</p>
 *
 * @see BugemonRepository
 * @see AttackRepository
 * @see TrainerBugemon
 * @see BugemonSpecie
 */
public class BugemonService {

    /** Repository providing Bugemon data. */
    private final BugemonRepository repository;

    /**
     * Creates a {@code BugemonService} connected to the Singleton repository.
     */
    public BugemonService() {
        this.repository = BugemonRepository.getInstance();
    }

    /**
     * Returns the list of all available Bugemons in the game.
     *
     * @return a list of all Bugemons
     */
    public List<BugemonSpecie> getAllBugemons() {
        return this.repository.getBugemonsList();
    }

    /**
     * Creates an independent copy of a Bugemon from its ID.
     *
     * <p>This method searches for the template in the repository and then
     * performs a deep clone: statistics and attacks are copied into new objects,
     * so that modifications during battle do not affect the original template.</p>
     *
     * @param id the ID of the Bugemon to instantiate (e.g., "florachu")
     * @return a new {@link TrainerBugemon} independent instance with its own stats and attacks
     * @throws BugemonNotFoundException if no Bugemon exists with the given ID
     */
    public TrainerBugemon createTrainerBugemon(final String id) throws BugemonNotFoundException {
        final BugemonSpecie template;
        try {
            template = this.repository.findById(id);
        } catch (ElementNotFoundException e) {
            throw new BugemonNotFoundException(id, e);
        }

        return new TrainerBugemon.Builder().withSpecie(template).build();
    }

    /**
     * Retrieves a Bugemon specie by its ID without creating a new instance.
     *
     * <p>This method is useful for accessing the template data of a Bugemon
     * without the need for an independent instance. It returns the original
     * {@link BugemonSpecie} from the repository, so modifications to it will affect all instances created from it.</p>
     *
     * @param id the ID of the Bugemon specie to retrieve (e.g., "florachu")
     * @return the {@link BugemonSpecie} corresponding to the given ID
     * @throws BugemonNotFoundException if no Bugemon exists with the given ID
     */
    public BugemonSpecie getSpecieFromId(final String id) throws BugemonNotFoundException {
        try {
            return this.repository.findById(id);
        } catch (ElementNotFoundException e) {
            throw new BugemonNotFoundException(id, e);
        }
    }
}
