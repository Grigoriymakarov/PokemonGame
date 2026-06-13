package models.shared;

import models.shared.bugemon.BugemonSpecie;
import models.shared.statistics.Statistics;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link BugemonSpecie} class.
 *
 * <p>Tests proper initialization, field assignment, and starter flag handling.</p>
 */
public class BugemonSpecieTest {

    /**
     * Verifies that the constructor correctly assigns all fields to the instance.
     */
    @Test
    public void constructorShouldAssignAllFields() {

        Statistics stats = new Statistics( 50,20,30,10) ;
        List<Attack> attacks = new ArrayList<>() ;

        BugemonSpecie bugemon = new BugemonSpecie.Builder()
                .id("001")
                .name("Florachu")
                .type(Type.FLORA)
                .baseStats(stats)
                .attacks(attacks)
                .sprite("florachu.png")
                .starter(true)
                .build();

        assertEquals("001", bugemon.id());
        assertEquals("Florachu", bugemon.getName());
        assertEquals(Type.FLORA, bugemon.getType());
        assertEquals(stats, bugemon.getBaseStats());
        assertTrue(bugemon.getStarter());
        assertEquals(attacks, bugemon.getPossibleAttacks());
    }

    /**
     * Verifies that the starter flag is correctly stored when set to {@code true}.
     */
    @Test
    public void starterFlagTrueShouldBeStoredCorrectly() {
        BugemonSpecie bugemon = new BugemonSpecie.Builder()
                .id("001")
                .name("Florachu")
                .type(Type.FLORA)
                .baseStats(new Statistics(1, 1, 1, 1))
                .attacks(null)
                .sprite("florachu.png")
                .starter(true)
                .build();

        assertTrue(bugemon.getStarter());
    }

    /**
     * Verifies that the starter flag is correctly stored when set to {@code false}.
     */
    @Test
    public void starterFlagFalseShouldBeStoredCorrectly() {
        BugemonSpecie bugemon = new BugemonSpecie.Builder()
                .id("001")
                .name("Florachu")
                .type(Type.FLORA)
                .baseStats(new Statistics(1, 1, 1, 1))
                .attacks(null)
                .sprite("florachu.png")
                .starter(false)
                .build();

        assertFalse(bugemon.getStarter());
    }

    /**
     * Verifies that two different BugemonSpecie instances are independent.
     */
    @Test
    public void twoInstancesShouldBeIndependent() {
        BugemonSpecie b1 = new BugemonSpecie.Builder()
                .id("001")
                .name("Florachu")
                .type(Type.FLORA)
                .baseStats(new Statistics(1, 1, 1, 1))
                .attacks(null)
                .sprite("florachu.png")
                .starter(false)
                .build();

        BugemonSpecie b2 = new BugemonSpecie.Builder()
                .id("002")
                .name("Exceflam")
                .type(Type.PYRO)
                .baseStats(new Statistics(1, 1, 1, 1))
                .attacks(null)
                .sprite("florachu.png") // Attention si c'est un copier-coller du sprite de Florachu ;)
                .starter(true)
                .build();
        assertNotEquals(b1.id(), b2.id());
        assertNotEquals(b1.getName(), b2.getName());
    }

}
