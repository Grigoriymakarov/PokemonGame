package repositories;

import models.shared.Attack;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link AttackRepository} class.
 *
 * <p>This test class verifies database loading,
 * and attack lookup by identifier.</p>
 */
public class AttackRepositoryTest {

    private AttackRepository repository;

    /**
     * Builds the attack repository data before each test.
     *
     * @throws IOException if attack data cannot be loaded
     */
    @Before
    public void setUp() throws IOException {
        this.repository = new AttackRepository();
        this.repository.buildDataBase();
    }

    /**
     * Verifies that building the database loads at least one attack.
     */
    @Test
    public void buildDataBaseShouldLoadAtLeastOneAttack() {
        List<Attack> attacks = this.repository.getList();
        assertFalse("The attack list should not be empty", attacks.isEmpty());
    }

    /**
     * Verifies that the attack map returns an attack matching a known identifier.
     */
    @Test
    public void getAttackMapShouldReturnAttackById() {
        Map<String, Attack> map = this.repository.getAttackMap();

        // Le reste ne change pas et fonctionnera parfaitement
        String firstId = map.keySet().iterator().next();

        Attack attack = map.get(firstId);
        assertNotNull(attack);
        assertEquals(firstId, attack.id());
    }

    /**
     * Verifies that the attack map returns null for an unknown identifier.
     */
    @Test
    public void getAttackMapShouldReturnNullForUnknownId() {
        Map<String, Attack> map = this.repository.getAttackMap();
        assertNull(map.get("id_qui_nexiste_pas_12345"));
    }
}
