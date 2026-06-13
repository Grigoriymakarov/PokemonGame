package repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import constants.PathConstants;
import models.shared.Attack;
import services.BugemonService;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Repository responsible for loading and accessing attack data.
 *
 * <p>The data is read from the file {@code src/main/json/attaques.json}.
 * Attacks are stored in a {@code HashMap} indexed by ID
 * for O(1) access.</p>
 *
 * @see BugemonService
 * @see BugemonRepository
 * @see Attack
 */
public class AttackRepository extends Repository<Attack> {

    /**
     * Default constructor.
     */
    public AttackRepository() {}

     /**
      * Loads data from the json resource file and build the attackMap.
      *
      * @throws IOException if the JSON resource file cannot be read or parsed
      */
     @Override
     public void buildDataBase() throws IOException {
         // Add type adapter for Optional (needed for attack effects deserialization)
         final GsonBuilder builder = new GsonBuilder();
         builder.registerTypeAdapter(Optional.class, new OptionalTypeAdapter<>());
         final Gson gson = builder.create();

         this.jsonResourceReader(PathConstants.JSON_ATTACKS, "attaques", Attack.class, gson);
     }

    /**
     * Returns an unmodifiable view of the attacks map indexed by ID.
     *
     * @return an unmodifiable map of attacks
     */
    public Map<String, Attack> getAttackMap() {
        return Collections.unmodifiableMap(this.itemsMap);
    }
}
