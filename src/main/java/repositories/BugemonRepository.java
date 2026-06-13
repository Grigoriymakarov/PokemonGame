package repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import constants.PathConstants;
import models.shared.Attack;
import models.shared.bugemon.BugemonSpecie;
import services.BugemonService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


/**
 * Repository responsible for loading and accessing Bugemon data.
 *
 * <p>This class implements the <b>Singleton</b> pattern: a single instance is created
 * upon the first call to {@link #getInstance()} and then reused. This ensures that
 * the JSON file is read only once during the application's lifetime.</p>
 *
 * <p>The data is read from the file {@code src/main/json/bugemons.json}
 * at application startup. Two data structures are maintained in memory:</p>
 * <ul>
 *   <li>A {@code List} for ordered access </li>
 *   <li>A {@code HashMap} for O(1) access by ID</li>
 * </ul>
 *
 * @see BugemonService
 * @see AttackRepository
 */
public class BugemonRepository extends Repository<BugemonSpecie> {

    /** Singleton instance. */
    private static BugemonRepository instance;

    /**
     * Returns the singleton instance of the repository.
     *
     * <p>If the instance does not exist yet, it is created and the JSON file is read.</p>
     *
     * @return the unique instance of {@code BugemonRepository}
     */
    public static BugemonRepository getInstance() {
        if (BugemonRepository.instance == null) {
            BugemonRepository.instance = new BugemonRepository();
        }
        return BugemonRepository.instance;
    }
    /**
     * Private constructor.
     */
    private BugemonRepository()  {}

     /**
      * Loads data from the JSON resource file and builds the ID index.
      *
      * @throws IOException if the JSON resource file cannot be read or parsed
      */

     @Override
     public void buildDataBase() throws IOException {
         final AttackRepository attackRepository = new AttackRepository();
         attackRepository.buildDataBase();
         this.buildDataBase(attackRepository);
     }

    /**
     * Loads data using the provided attack repository for attack deserialization.
     *
     * @param attackRepository repository providing attacks by id
     * @throws IOException if JSON resources cannot be loaded
     */
    public void buildDataBase(final AttackRepository attackRepository) throws IOException {
         final GsonBuilder builder = new GsonBuilder();
         final Type attackListType = new TypeToken<List<Attack>>() {}.getType();
         // Registers TypeAdapter that uses the attack repository to read the attacks in the json file
         builder.registerTypeAdapter(attackListType, new AttackListTypeAdapter(attackRepository.getAttackMap()));
         final Gson gson = builder.create();
         this.jsonResourceReader(PathConstants.JSON_BUGEMONS, "bugemons", BugemonSpecie.class, gson);
     }

    /**
     * Returns a copy of the list of all available Bugemons.
     * @return a new list containing all Bugemons
     */
    public List<BugemonSpecie> getBugemonsList(){
        return this.getList();
    }

}
