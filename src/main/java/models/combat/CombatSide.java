package models.combat;

/**
 * Side of the combat for an acting or targeted trainer.
 */
public enum CombatSide {
    PLAYER,
    ENEMY;

    public CombatSide opponent() {
        return this == CombatSide.PLAYER ? CombatSide.ENEMY : CombatSide.PLAYER;
    }
}


