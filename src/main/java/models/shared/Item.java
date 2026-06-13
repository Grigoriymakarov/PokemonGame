package models.shared;

import com.google.gson.annotations.SerializedName;

/**
 * Represents an item that can be stored in an inventory and used in battle.
 *
 * <p>An item has a unique identifier, a display name, a category, an associated
 * effect, and a sprite file name.</p>
 *
 * @param id       Unique technical identifier for the item.
 * @param name     Human-readable name displayed in the UI.
 * @param category The classification of the item (see {@link Category}).
 * @param effects  The specific effect ({@link Effect}) applied on use.
 * @param sprite   Filename or path for the item's visual representation.
 * @param description the textual description of the item
 */
public record Item(String id, @SerializedName("nom") String name, @SerializedName("categorie") Category category,
                   @SerializedName("effet") Effect effects, @SerializedName("description") String description, String sprite) implements Identifiable {

    /**
     * Item category used to group behavior and presentation.
     */
    public enum Category {
        /**
         * Healing items that restore HP.
         */
        @SerializedName("soin") HEAL,
        /**
         * Buff items that modify statistics temporarily.
         */
        @SerializedName("boost") BOOST
    }

    /**
     * Returns the unique identifier of this item.
     *
     * @return the item id
     */
    @Override
    public String id() {
        return this.id;
    }

    /**
     * Returns the display name of this item.
     *
     * @return the item name
     */
    @Override
    public String name() {
        return this.name;
    }


    /**
     * Returns the effect associated with this item.
     *
     * @return the item effect
     */
    @Override
    public Effect effects() {
        return this.effects;
    }

    /**
     * Returns the description of this item.
     *
     * @return the item description
     */
    @Override
    public String description() {
        return this.description;
    }


    /**
     * Returns the sprite file name of this item.
     *
     * @return the sprite path segment or file name
     */
    @Override
    public String sprite() {
        return this.sprite;
    }
}
