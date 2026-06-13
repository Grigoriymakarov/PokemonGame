package services;

import repositories.AttackRepository;
import repositories.BugemonRepository;
import repositories.ItemRepository;
import repositories.TeamRepository;

import java.io.IOException;

/**
 * Service responsible for the initial loading of all game data.
 * <p>
 * This service coordinates the different repositories to ensure that all
 * JSON data (attacks, Bugemon, items, and saved teams) are parsed and
 * available in memory before the application starts its main logic.
 * </p>
 */
public class DataInitializationService {

    private final AttackRepository attackRepository;
    private final ItemRepository itemRepository;

    public DataInitializationService(final AttackRepository attackRepository,
                                     final ItemRepository itemRepository) {
        this.attackRepository = attackRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Triggers the data loading process for all application repositories.
     * <p>
     * This method calls the {@code buildDataBase()} method on each repository.
     * It is typically called once during the application startup
     * (splash screen or main initialization).
     * </p>
     *
     * @throws IOException if any JSON file is missing or corrupted during the
     * initialization of any repository.
     */
    public void initializeAllRepositories() throws IOException {
        this.attackRepository.buildDataBase();
        BugemonRepository.getInstance().buildDataBase(this.attackRepository);
        TeamRepository.getInstance().buildDataBase();
        this.itemRepository.buildDataBase();
    }

}
