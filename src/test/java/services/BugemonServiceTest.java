package services;

import models.exceptions.BugemonNotFoundException;
import models.shared.bugemon.TrainerBugemon;
import org.junit.Before;
import org.junit.Test;
import repositories.AttackRepository;
import repositories.BugemonRepository;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * Unit tests for the {@link BugemonService} class.
 *
 * <p>This test class verifies TrainerBugemon creation from repository data.</p>
 */
public class BugemonServiceTest {

    private BugemonService bugemonService;

    private static final String ID = "florachu";

    /**
     * Builds repository data and creates the service before each test.
     *
     * @throws IOException if repository data cannot be loaded
     */
    @Before
    public void setUp() throws IOException {
        BugemonRepository.getInstance().buildDataBase();

        bugemonService = new BugemonService();
    }

    /**
     * Verifies that creating a TrainerBugemon with a valid id returns a non-null object.
     */
    @Test
    public void createTrainerBugemon_validId_returnsNonNullBugemon() throws BugemonNotFoundException {
        TrainerBugemon bugemon = bugemonService.createTrainerBugemon(ID);
        assertNotNull("The created Bugemon should not be null", bugemon);
    }

    /**
     * Verifies that two creation calls return distinct TrainerBugemon instances.
     */
    @Test
    public void createTrainerBugemon_twoCalls_returnDifferentInstances() throws BugemonNotFoundException {
        TrainerBugemon first = bugemonService.createTrainerBugemon(ID);
        TrainerBugemon second = bugemonService.createTrainerBugemon(ID);

        assertNotSame("Two calls should return different instances", first, second);
    }
}
