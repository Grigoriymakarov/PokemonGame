package services.combat.actions;

import models.combat.event.CombatEvent;
import models.exceptions.EffectApplicationException;
import models.shared.Item;
import services.combat.EffectResolver;
import models.combat.event.ItemUsedEvent;
import java.util.Objects;

/**
 * Action representing the use of a consumable item during combat.
 *
 * <p>The item action wraps a single item that the player intends to use. Its execution
 * applies all effects associated with the item through the effect resolver.</p>
 */
public final class UseItemAction implements Action {

    private final Item item;


    /**
     * Creates an item-use action for the supplied item.
     *
     * @param item the item that will be used during execute
     * @throws NullPointerException if item is null
     */
    public UseItemAction(final Item item) {
        this.item = Objects.requireNonNull(item, "Item cannot be null");
    }

    @Override
    public Identifier identifier() {
        return Identifier.USE_ITEM;
    }

    /**
     * Executes the item effect in the provided combat context.
     *
     * @param context the combat context
     * @throws NullPointerException if context is null
     */
    @Override
    public void execute(final ActionContext context) {
        if (context == null) {
            throw new NullPointerException("ActionContext cannot be null");
        }

        context.combatState().addEvent(
                new ItemUsedEvent(context.getActor().getName(), item.name())
        );

        try {
            EffectResolver resolver = new EffectResolver(
                    java.util.List.of(this.item.effects()),
                    context.combatState(),
                    context.getActor(),
                    context.getActor()
            );
            resolver.apply();

        } catch (EffectApplicationException e) {
            throw new IllegalStateException("Failed to apply the effect of item " + item.name(), e);
        }
    }
}
