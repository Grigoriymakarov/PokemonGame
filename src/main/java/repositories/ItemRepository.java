package repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import constants.PathConstants;
import models.exceptions.ElementNotFoundException;
import models.shared.Item;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repository responsible for loading and providing access to game items.
 *
 * <p>{@code ItemRepository} reads item definitions from the {@code objets.json} resource file
 * and exposes them through the inherited {@link Repository} interface. It also loads the
 * default starting inventory ({@code inventaire_depart}) defined in the same file.</p>
 *
 * @see Item
 * @see Repository
 */
public class ItemRepository extends Repository<Item>{

    /** Starting inventory mapping item IDs to their default quantities. */
    private Map<String, Integer> loadOut;

    /** Default constructor. */
    public ItemRepository() {}


    /**
     * Loads all items and the default starting inventory from the {@code objets.json} resource file.
     *
     * <p>This method must be called before any other method on this repository.
     * It performs two operations:
     * <ol>
     *   <li>Deserializes all {@link Item} objects into the inherited item list and hash map</li>
     *   <li>Reads the {@code inventaire_depart} section to build the default starting loadout</li>
     * </ol>
     *
     * @throws IOException if the resource file cannot be found or read
     */
    @Override
    public void buildDataBase() throws IOException {
        // Add type adapter for Optional (needed for item effects deserialization)
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Optional.class, new OptionalTypeAdapter<>());
        final Gson gson = builder.create();

        // Load items
        this.jsonResourceReader(PathConstants.JSON_ITEMS, "objets", Item.class, gson);

        // Load default inventory
        final var inputStream = this.getClass().getResourceAsStream(PathConstants.JSON_ITEMS);
        if (inputStream == null) throw new IOException("Resource not found: " + PathConstants.JSON_ITEMS);

        this.loadOut = new HashMap<>();
        try (final InputStreamReader reader = new InputStreamReader(inputStream)) {
            final JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            final JsonObject loadOutJson = jsonObject.getAsJsonObject("inventaire_depart");
            for (final String key : loadOutJson.keySet()) {
                this.loadOut.put(key, loadOutJson.get(key).getAsInt());
            }
        }
    }

    /**
     * Returns the default starting inventory as a map of {@link Item} to quantities.
     *
     * <p>The returned map is built from the {@code inventaire_depart} section of
     * {@code objets.json}. Items whose ID cannot be resolved are silently ignored.</p>
     *
     * <p>Each call returns a new independent map — modifying it does not affect
     * the repository's internal state.</p>
     *
     * @return a new map associating each starting {@link Item} with its default quantity
     */
    public Map<Item, Integer> getFirstLoadOut() throws ElementNotFoundException {
        final Map<Item, Integer> firstLoadOut = new HashMap<>();

        for (final Map.Entry<String, Integer> entry : this.loadOut.entrySet()) {
            final Item item = this.findById(entry.getKey());
            firstLoadOut.put(item, entry.getValue());
        }

        return Collections.unmodifiableMap(firstLoadOut);
    }
}