package repositories;

import models.shared.bugemon.BugemonSpecie;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link BugemonRepository} class.
 *
 * <p>This test class verifies singleton access, database loading,
 * and defensive copying of the Bugemon list.</p>
 */
public class BugemonRepositoryTest {

    /**
     * Builds repository data before each test.
     *
     * <p>The attack repository must be loaded first because the Bugemon
     * repository depends on it for deserialization.</p>
     *
     * @throws IOException if repository data cannot be loaded
     */
    @Before
    public void setUp() throws IOException {
        BugemonRepository.getInstance().buildDataBase();
    }

    /**
     * Verifies that the repository always returns the same singleton instance.
     */
    @Test
    public void getInstanceShouldAlwaysReturnSameObject() {
        BugemonRepository first = BugemonRepository.getInstance();
        BugemonRepository second = BugemonRepository.getInstance();
        assertSame(first, second);
    }

    /**
     * Verifies that building the database loads at least one Bugemon specie.
     */
    @Test
    public void buildDataBaseShouldLoadAtLeastOneBugemon() {
        List<BugemonSpecie> bugemons = BugemonRepository.getInstance().getBugemonsList();
        assertFalse("The Bugemon list should not be empty", bugemons.isEmpty());
    }

    /**
     * Verifies that retrieving the Bugemon list returns a distinct list instance.
     */
    @Test
    public void getBugemonListShouldReturnACopy() {
        List<BugemonSpecie> list1 = BugemonRepository.getInstance().getBugemonsList();
        List<BugemonSpecie> list2 = BugemonRepository.getInstance().getBugemonsList();

        assertNotSame(list1, list2);
        assertEquals(list1.size(), list2.size());
    }

}
