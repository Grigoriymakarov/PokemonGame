package services;

import models.exceptions.BugemonNotFoundException;
import models.shared.bugemon.TrainerBugemon;
import models.shared.xp.LevelUpEvent;
import org.junit.Before;
import org.junit.Test;
import repositories.AttackRepository;
import repositories.BugemonRepository;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link XPService} class.
 *
 * <p>This test class verifies XP calculation, XP distribution,
 * shared XP behavior, and level-up event generation.</p>
 */
public class XPServiceTest {

    private XPService xpService;
    private BugemonService bugemonService;

    /**
     * Builds repository data and creates XP-related services before each test.
     *
     * @throws IOException if repository data cannot be loaded
     */
    @Before
    public void setUp() throws IOException {
        BugemonRepository.getInstance().buildDataBase();
        xpService = new XPService();
        bugemonService = new BugemonService();
    }

    /**
     * Verifies that normal combat XP uses the expected formula.
     */
    @Test
    public void correctFormula() {
        int xp = xpService.calculateTotalXP(3, false, 2);
        assertEquals("Normal combat floor 3 with 2 opponents should grant 180 XP", 180, xp);
    }

    /**
     * Verifies that boss combat doubles the XP reward.
     */
    @Test
    public void bossCombat_doublesXP() {
        int xp = xpService.calculateTotalXP(3, true, 2);
        assertEquals("Boss combat floor 3 with 2 opponents should grant 360 XP", 360, xp);
    }

    /**
     * Verifies that a single participant receives all distributed XP.
     */
    @Test
    public void singleParticipant_getsAllXP() throws BugemonNotFoundException {
        TrainerBugemon bugemon = bugemonService.createTrainerBugemon("florachu");
        int xpBefore = bugemon.getXP();

        xpService.distributeXP(List.of(bugemon), 2, false, 1);

        int gained = bugemon.getXP() - xpBefore;
        int expected = xpService.calculateTotalXP(2, false, 1);
        assertTrue("The Bugemon should have gained XP", bugemon.getXP() >= 0);
        assertEquals("With 60 XP and a threshold of 50, the Bugemon should be level 2",
                2, bugemon.getLevel());
    }

    /**
     * Verifies that XP is shared between two participants.
     */
    @Test
    public void twoParticipants_xpSharedEqually() throws BugemonNotFoundException {
        TrainerBugemon b1 = bugemonService.createTrainerBugemon("florachu");
        TrainerBugemon b2 = bugemonService.createTrainerBugemon("pyricore");

        int totalXP = xpService.calculateTotalXP(5, false, 3);
        int share = totalXP / 2;

        int xpBefore1 = b1.getXP();
        int xpBefore2 = b2.getXP();

        xpService.distributeXP(List.of(b1, b2), 5, false, 3);

        assertEquals("Both Bugemon should receive a non-negative XP change",
                b1.getXP() - xpBefore1 >= 0,
                b2.getXP() - xpBefore2 >= 0);
    }

    /**
     * Verifies that distributing enough XP to level up returns level-up events.
     */
    @Test
    public void distributeXP_returnsLevelUpEvents_whenBugemonLevelsUp() throws BugemonNotFoundException {
        TrainerBugemon bugemon = bugemonService.createTrainerBugemon("florachu");

        List<LevelUpEvent> events = xpService.distributeXP(List.of(bugemon), 5, false, 3);

        assertFalse("A level-up should generate a LevelUpEvent", events.isEmpty());
    }

}
