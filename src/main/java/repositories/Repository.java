package repositories;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import models.exceptions.ElementNotFoundException;
import models.shared.Identifiable;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for repositories that handle data loading and access.
 *
 * <p>Provides common functionality for reading and deserializing JSON data
 * using Gson, as well as building hash maps for O(1) entity access by ID.</p>
 *
 * <p><b>Two loading methods are available:</b></p>
 * <ul>
 *   <li>{@link #jsonResourceReader(String, String, Class, Gson)} - Load from classpath resources (JAR-compatible)</li>
 *   <li>{@link #jsonFileReader(String, String, Class, Gson)} - Load from filesystem (for external files)</li>
 * </ul>
 *
 * @param <T> the type of identifiable entities handled by this repository
 * @see Identifiable
 * @see AttackRepository
 * @see BugemonRepository
 */
public abstract class Repository<T extends Identifiable> {

    protected List<T> items;
    protected Map<String, T> itemsMap;

    /**
     * Reads and deserializes a JSON file from the classpath as a resource.
     *
     * <p>This method loads a JSON file as a classpath resource and deserializes it
     * into a list of objects. This approach works correctly both in development mode
     * and when the application is packaged as a JAR file.</p>
     *
     * <p>The JSON structure is expected to have a root property matching the {@code rootKey}
     * that contains an array of entities.</p>
     *
     * <p><b>Example:</b> {@code jsonResourceReader(PathConstants.JSON_ATTACKS, "attaques", Attack.class, gson)}
     * will read the resource and deserialize the {@code "attaques"} array.</p>
     *
     * @param resourcePath the full path to the resource (e.g. from PathConstants)
     * @param rootKey the root property key containing the array in the JSON
     * @param elementType the class type of the entities to deserialize
     * @param gson the Gson instance to use for deserialization
     * @throws IOException if an error occurs while reading the JSON resource
     *
     * @see #jsonFileReader(String, String, Class, Gson)
     * @see #getList()
     */
    protected void jsonResourceReader(final String resourcePath, final String rootKey, final Class<T> elementType, final Gson gson) throws IOException {
        final var inputStream = this.getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        try (final InputStreamReader reader = new InputStreamReader(inputStream)) {
            final JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            final JsonArray list = jsonObject.getAsJsonArray(rootKey);
            final Type listType = TypeToken.getParameterized(List.class, elementType).getType();

            this.items = gson.fromJson(list, listType);
            if (this.items == null) {throw new IllegalStateException("Could not reconstruct list from resource: " + resourcePath);}
            this.buildMap();
        }
    }



    /**
     * Reads and deserializes a JSON file directly from the filesystem.
     *
     * <p>This method loads a JSON file from a specific path on the filesystem
     * and deserializes it into a list of objects. Use this method when you need to load
     * JSON files from outside the classpath (e.g., user-generated or configuration files).</p>
     *
     * <p>The JSON structure is expected to have a root property matching the {@code rootKey}
     * that contains an array of entities.</p>
     *
     * @param filePath the full filesystem path to the JSON file
     * @param rootKey the root property key containing the array in the JSON
     * @param elementType the class type of the entities to deserialize
     * @param gson the Gson instance to use for deserialization
     * @throws IOException if an error occurs while reading the JSON file
     *
     * @see #jsonResourceReader(String, String, Class, Gson)
     * @see #getList()
     */
    protected void jsonFileReader(final String filePath, final String rootKey, final Class<T> elementType, final Gson gson) throws IOException {
        try (final FileReader fileReader = new FileReader(filePath)) {
            final JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);
            final JsonArray list = jsonObject.getAsJsonArray(rootKey);
            final Type listType = TypeToken.getParameterized(List.class, elementType).getType();

            this.items = gson.fromJson(list, listType);
            if (this.items == null) {throw new IllegalStateException("Could not reconstruct list from file: " + filePath);}
            this.buildMap();
        }
    }

    /**
     * Reads and deserializes a JSON file from the filesystem using the default Gson instance.
     *
     * <p>This is a convenience method that uses a default {@link Gson} instance
     * for deserialization. For custom deserialization (e.g., with type adapters),
     * use {@link #jsonFileReader(String, String, Class, Gson)} instead.</p>
     *
     * @param filePath the full filesystem path to the JSON file
     * @param rootKey the root property key containing the array in the JSON
     * @param elementType the class type of the entities to deserialize
     * @throws IOException if an error occurs while reading the JSON file
     *
     * @see #jsonFileReader(String, String, Class, Gson)
     */
    protected void jsonFileReader(final String filePath, final String rootKey, final Class<T> elementType) throws IOException {
        this.jsonFileReader(filePath, rootKey, elementType, new Gson());
    }

      /**
       * Builds a Map from the constructed internal list of identifiable entities.
       *
       * <p>This helper method converts a list of entities into a Map indexed by their unique ID,
       * providing O(1) access time for entity lookups. Each entity's ID is obtained via
       * {@link Identifiable#id()}.</p>
       *
       * <p>This method is automatically called by {@link #jsonResourceReader(String, String, Class, Gson)}
       * or {@link #jsonFileReader(String, String, Class, Gson)} after the entities have been deserialized.</p>
       */
    private void buildMap() {
        final Map<String, T> entityMap = new HashMap<>();
        for (final T entity : this.items) {
            entityMap.put(entity.id(), entity);
        }

        this.itemsMap = entityMap;
    }


    /**
     * Retrieves the internal list of deserialized entities.
     *
     * @return the list of entities loaded from the JSON file or resource
     * @throws IllegalStateException if the repository has not been initialized (jsonResourceReader or jsonFileReader not called)
     */
    public List<T> getList() {
        if (this.items == null) { throw new IllegalStateException("Repository's item list is not initialized. Please check that jsonResourceReader or jsonFileReader is properly called before this method."); }
        return Collections.unmodifiableList(this.items);
    }


    /**
     * Finds an item in the Map by its unique ID.
     *
     * <p>This method provides O(1) access time by using the internal Map.</p>
     *
     * @param id the item's unique ID
     * @return the corresponding entity
     * @throws ElementNotFoundException if no entity with the given ID exists in the Map
     */
    public T findById(final String id) throws ElementNotFoundException {
        if (!this.itemsMap.containsKey(id)) {
            throw new ElementNotFoundException("No element found with ID: " + id);
        }
        return this.itemsMap.get(id);
    }

    /**
     * Builds the database by reading and processing data from JSON files.
     *
     * <p>Subclasses must implement this method to load their specific entity data
     * and populate the necessary data structures. This method is typically called during
     * repository initialization.</p>
     *
     * <p><b>Implementation template for loading from classpath resources:</b></p>
     * <pre>
     * public void buildDataBase() throws IOException {
     *     jsonResourceReader(PathConstants.JSON_ATTACKS, "attaques", Attack.class);
     * }
     * </pre>
     *
     * <p><b>Implementation template for loading from filesystem files:</b></p>
     * <pre>
     * public void buildDataBase() throws IOException {
     *     jsonFileReader(PathConstants.DB_SAVED_TEAMS, "savedteams", SavedTeam.class);
     * }
     * </pre>
     *
     * <p><b>Implementation template for complex types with custom deserialization:</b></p>
     * <pre>
     * {@code
     * public void buildDataBase() throws IOException {
     *     GsonBuilder builder = new GsonBuilder();
     *     Type attackListType = new TypeToken<List<Attack>>() {}.getType();
     *     builder.registerTypeAdapter(attackListType, new AttackListTypeAdapter(...));
     *     Gson gson = builder.create();
     *     jsonResourceReader(PathConstants.JSON_BUGEMONS, "bugemons", BugemonSpecie.class, gson);
     * }
     * }
     * </pre>
     *
     * @throws IOException if an error occurs while reading the JSON file
     *
     * @see #jsonResourceReader(String, String, Class, Gson)
     * @see #jsonFileReader(String, String, Class)
     * @see #buildMap()
     */
    public abstract void buildDataBase() throws IOException;
}
