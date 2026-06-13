package models.shared;

import org.junit.Test;

import static models.shared.Type.*;
import static models.shared.TypeEffectiveness.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for type effectiveness relationships.
 */
public class TypeEffectivenessTest {

    /**
     * Tests that FLORA is super effective against AQUA.
     * flora > aqua
     */
    @Test
    public void floraIsSuperEffectiveAgainstAqua() {
        assertEquals(SUPER_EFFECTIVE, FLORA.getEffectivenessAgainst(AQUA));
    }

    /**
     * Tests that FLORA is not very effective against LITHO.
     * litho > flora
     */
    @Test
    public void floraIsNotVeryEffectiveAgainstLitho() {
        assertEquals(NOT_VERY_EFFECTIVE, FLORA.getEffectivenessAgainst(LITHO));
    }

    /**
     * Tests that FLORA is neutral against PYRO.
     * flora = pyro
     */
    @Test
    public void floraIsNeutralAgainstPyro() {
        assertEquals(NEUTRAL, FLORA.getEffectivenessAgainst(PYRO));
    }

    /**
     * Tests that FLORA is neutral against itself.
     * flora = flora
     */
    @Test
    public void floraIsNeutralAgainstFlora() {
        assertEquals(NEUTRAL, FLORA.getEffectivenessAgainst(FLORA));
    }

    /**
     * Tests that AQUA is super effective against PYRO.
     * aqua > pyro
     */
    @Test
    public void aquaIsSuperEffectiveAgainstPyro() {
        assertEquals(SUPER_EFFECTIVE, AQUA.getEffectivenessAgainst(PYRO));
    }

    /**
     * Tests that AQUA is not very effective against FLORA.
     * flora > aqua
     */
    @Test
    public void aquaIsNotVeryEffectiveAgainstFlora() {
        assertEquals(NOT_VERY_EFFECTIVE, AQUA.getEffectivenessAgainst(FLORA));
    }

    /**
     * Tests that AQUA is neutral against LITHO.
     * aqua = litho
     */
    @Test
    public void aquaIsNeutralAgainstLitho() {
        assertEquals(NEUTRAL, AQUA.getEffectivenessAgainst(LITHO));
    }

    /**
     * Tests that AQUA is neutral against itself.
     * aqua = aqua
     */
    @Test
    public void aquaIsNeutralAgainstAqua() {
        assertEquals(NEUTRAL, AQUA.getEffectivenessAgainst(AQUA));
    }

    /**
     * Tests that PYRO is super effective against LITHO.
     * pyro > litho
     */
    @Test
    public void pyroIsSuperEffectiveAgainstLitho() {
        assertEquals(SUPER_EFFECTIVE, PYRO.getEffectivenessAgainst(LITHO));
    }

    /**
     * Tests that PYRO is not very effective against AQUA.
     * aqua > pyro
     */
    @Test
    public void pyroIsNotVeryEffectiveAgainstAqua() {
        assertEquals(NOT_VERY_EFFECTIVE, PYRO.getEffectivenessAgainst(AQUA));
    }

    /**
     * Tests that PYRO is neutral against FLORA.
     * flora = pyro
     */
    @Test
    public void pyroIsNeutralAgainstFlora() {
        assertEquals(NEUTRAL, PYRO.getEffectivenessAgainst(FLORA));
    }

    /**
     * Tests that PYRO is neutral against itself.
     * pyro = pyro
     */
    @Test
    public void pyroIsNeutralAgainstPyro() {
        assertEquals(NEUTRAL, PYRO.getEffectivenessAgainst(PYRO));
    }

    /**
     * Tests that LITHO is super effective against FLORA.
     * litho > flora
     */
    @Test
    public void lithoIsSuperEffectiveAgainstFlora() {
        assertEquals(SUPER_EFFECTIVE, LITHO.getEffectivenessAgainst(FLORA));
    }

    /**
     * Tests that LITHO is not very effective against PYRO.
     * pyro > litho
     */
    @Test
    public void lithoIsNotVeryEffectiveAgainstPyro() {
        assertEquals(NOT_VERY_EFFECTIVE, LITHO.getEffectivenessAgainst(PYRO));
    }

    /**
     * Tests that LITHO is neutral against AQUA.
     * aqua = litho
     */
    @Test
    public void lithoIsNeutralAgainstAqua() {
        assertEquals(NEUTRAL, LITHO.getEffectivenessAgainst(AQUA));
    }

    /**
     * Tests that LITHO is neutral against itself.
     * litho = litho
     */
    @Test
    public void lithoIsNeutralAgainstLitho() {
        assertEquals(NEUTRAL, LITHO.getEffectivenessAgainst(LITHO));
    }
}

