package models.shared;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Represents an attack that a Bugemon can use in combat.
 *
 * <p>An attack is defined by its base power, elemental type,
 * description, and possible secondary effects. Once created, an attack
 * is immutable to ensure consistency during combat calculations.</p>
 *
 * <p>Attacks are loaded from {@code attaques.json} by
 * {@link repositories.AttackRepository}.</p>
 *
 * @see Effect
 * @see Type
 */
public class Attack implements Identifiable {

    /** Unique identifier of the attack. */
    private String id ;

    /** Display name of the attack. */
    @SerializedName("nom")
    private String name;

    /** Elemental type of the attack: flora, pyro, aqua or litho. */
    private Type type;

    /** Textual description of the attack’s effect. */
    private String description;

    /** Base power of the attack, used in the damage calculation. */
    @SerializedName("puissance")
    private int power;

    /** List of secondary effects applied when this attack is used. */
    @SerializedName("effets")
    private List<Effect> effects;

    /**
     * No-argument constructor required by Gson for JSON deserialization.
     */
    public Attack(){
        this.effects = new ArrayList<>();
    }
    /**
     * Private constructor used by the {@link Builder}.
     * @param builder The builder containing attack parameters.
     */
    private Attack(final Builder builder) {
        this.id          = builder.id;
        this.name        = builder.name;
        this.type        = builder.type;
        this.description = builder.description;
        this.power       = builder.power;
        this.effects     = builder.effects != null ? builder.effects : new ArrayList<>();
    }
    /**
     * Builder class for creating {@link Attack} instances.
     */
    public static class Builder {
        private String id;
        private String name;
        private Type type;
        private String description;
        private int power;
        private List<Effect> effects;

        /** @param id Unique identifier. @return this builder. */
        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        /** @param name Display name. @return this builder. */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /** @param type Elemental type. @return this builder. */
        public Builder type(final Type type) {
            this.type = type;
            return this;
        }

        /** @param description Textual description. @return this builder. */
        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        /** @param power Base damage power. @return this builder. */
        public Builder power(final int power) {
            this.power = power;
            return this;
        }

        /** @param effects List of secondary effects. @return this builder. */
        public Builder effects(final List<Effect> effects) {
            this.effects = effects != null ? new ArrayList<>(effects) : new ArrayList<>();
            return this;
        }

        /**
         * Validates and creates a new {@link Attack}.
         * @return a new Attack instance.
         * @throws NullPointerException if mandatory fields (id, name, type) are missing.
         */
        public Attack build() {
            if (this.id == null)   throw new NullPointerException("id is required");
            if (this.name == null) throw new NullPointerException("name is required");
            if (this.type == null) throw new NullPointerException("type is required");
            return new Attack(this);
        }
    }
    // GETTERS

    /**
     * @return the unique identifier of the attack.
     */
    @Override
    public String id() { return this.id; }

    /**
     * @return the display name of the attack.
     */
    public String getName() { return this.name; }

    /**
     * @return the elemental type of the attack.
     */
    public Type getType() { return this.type; }

    /**
     * @return the textual description of the attack’s effect.
     */
    public String getDescription() { return this.description; }

    /**
     * @return the base power of the attack.
     */
    public int getPower() { return this.power; }

    /**
     * @return an unmodifiable view of the secondary effects.
     */
    public List<Effect> getEffects() {
        return Collections.unmodifiableList(this.effects);
    }
    /**
     * Calculates the effectiveness of this attack against a specific type.
     *
     * @param specieType The type of the target Bugemon.
     * @return the effectiveness multiplier (WEAK, NORMAL, STRONG).
     */
    public TypeEffectiveness getEffectivenessAgainst(final Type specieType) {
        return this.type.getEffectivenessAgainst(specieType);
    }
}

