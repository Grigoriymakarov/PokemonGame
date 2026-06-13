package repositories;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import models.shared.Attack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom {@link TypeAdapter} for deserializing a JSON array of attack IDs
 * into a {@link List} of {@link Attack} objects using a provided dictionary.
 * This adapter bridges the gap between JSON string IDs and their corresponding
 * {@link Attack} objects during GSON deserialization.
 */
public class AttackListTypeAdapter extends TypeAdapter<List<Attack>> {

    /** Dictionary mapping attack IDs to their corresponding {@link Attack} objects. */
    private final Map<String, Attack> attackDictionary;

    /**
     * Constructs an {@code AttackListTypeAdapter} with the given attack dictionary.
     *
     * @param attackDictionary A map where keys are attack IDs (as {@link String})
     *                         and values are the corresponding {@link Attack} objects.
     * @throws IllegalArgumentException if the provided dictionary is {@code null}.
     */
    public AttackListTypeAdapter(final Map<String, Attack> attackDictionary) {
        if (attackDictionary == null) {
            throw new IllegalArgumentException("The attacks dictionary cannot be null.");
        }
        this.attackDictionary = new HashMap<>(attackDictionary);
    }

    /**
     * Writes a list of {@link Attack} objects to JSON.
     * This method serializes each attack's ID into a JSON array of strings.
     *
     * @param out   The {@link JsonWriter} to write the JSON to.
     * @param value The list of {@link Attack} objects to serialize.
     * @throws IOException if an I/O error occurs during writing.
     */
    @Override
    public void write(final JsonWriter out, final List<Attack> value) throws IOException {
        out.beginArray();
        for (final Attack attack : value) {
            out.value(attack.id()); // Write the attack's ID as a string
        }
        out.endArray();
    }

    /**
     * Reads a JSON array of attack IDs and converts it into a list of {@link Attack} objects.
     * Each ID is looked up in the provided dictionary to retrieve the corresponding attack.
     *
     * @param in The {@link JsonReader} to read the JSON from.
     * @return A list of {@link Attack} objects corresponding to the IDs in the JSON array.
     * @throws IOException if an I/O error occurs during reading.
     * @throws JsonParseException if an attack ID is not found in the dictionary.
     */
    @Override
    public List<Attack> read(final JsonReader in) throws IOException {
        final List<Attack> attacks = new ArrayList<>();
        in.beginArray();
        while (in.hasNext()) {
            final String attackId = in.nextString();
            if (!this.attackDictionary.containsKey(attackId)) {
                throw new JsonParseException("Attack ID not found in the dictionary: " + attackId);
            }
            final Attack attack = this.attackDictionary.get(attackId);
            attacks.add(attack);
        }
        in.endArray();
        return attacks;
    }
}
