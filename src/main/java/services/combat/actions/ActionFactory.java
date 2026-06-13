package services.combat.actions;

import models.shared.Attack;
import models.shared.Item;
import models.shared.bugemon.TrainerBugemon;

/**
 * Centralizes action creation and keeps resolver dependencies out of callers.
 */
public final class ActionFactory {

    /**
     * Creates an action factory.
     */
    private ActionFactory() {
    }

    public static Action attack(final Attack attack) {
        return new AttackAction(attack);
    }

    public static Action useItem(final Item item) {
        return new UseItemAction(item);
    }

    public static Action forfeit() {
        return new ForfeitAction();
    }

    public static Action switchBugemon(final TrainerBugemon replacer) {
        return new SwitchAction(replacer);
    }
}
