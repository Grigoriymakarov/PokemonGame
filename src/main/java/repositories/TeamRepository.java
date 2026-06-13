package repositories;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import constants.PathConstants;
import models.exceptions.ElementNotFoundException;
import models.exceptions.TeamNotFoundException;
import models.team.SavedTeam;
import services.TeamService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repository for persisting saved teams.
 *
 * <p>This class manages reading and writing saved teams from/to the JSON file
 * {@code savedteams.json}.</p>
 *
 * <p>It implements the <b>Singleton</b> pattern: a single instance is used throughout
 * the application via {@link #getInstance()}.</p>
 *
 * <p>Teams are stored in memory in two structures:</p>
 * <ul>
 *   <li>the inherited {@code items} list for ordered access</li>
 *   <li>the inherited {@code itemsMap} map for fast O(1) access by name</li>
 * </ul>
 *
 * <p>Each read/write operation synchronizes the in-memory state with the disk file.</p>
 *
 * @see SavedTeam
 * @see TeamService
 */
public class TeamRepository extends Repository<SavedTeam> {

    private final Gson gson = new Gson();

    private static TeamRepository instance;

    /**
     * Returns the unique instance of {@code TeamRepository}.
     *
     * <p>Creates the instance on the first call (lazy initialization).</p>
     *
     * @return the singleton instance of {@code TeamRepository}
     */
    public static TeamRepository getInstance() {
        if (TeamRepository.instance == null) {
            TeamRepository.instance = new TeamRepository();
        }
        return TeamRepository.instance;
    }


    /**
     * Loads saved teams from the JSON file.
     *
     * <p>This method:</p>
     * <ul>
     *   <li>Creates the file if it does not exist with valid empty content</li>
     *   <li>Reads and deserializes teams from {@code savedteams.json}</li>
     *   <li>Rebuilds the map index by name for fast access</li>
     * </ul>
     *
     * <p>If a JSON parsing error occurs, a message is printed and the database remains empty.</p>
     *
     * @throws IOException if a disk read/creation error occurs
     */
    @Override
    public void buildDataBase() throws IOException {// reads JSON file
        final File file = new File(PathConstants.DB_SAVED_TEAMS);
        if (!file.exists()) {
            // Create parent directories if they don't exist
            final File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            file.createNewFile();
            try (final FileWriter writer = new FileWriter(file)) {
                writer.write("{\"savedteams\": []}");
            }
        }
        this.jsonFileReader(PathConstants.DB_SAVED_TEAMS, "savedteams", SavedTeam.class);
    }

    /**
     * Persists the in-memory teams to the JSON file.
     *
     * <p>Writes the current state of the inherited {@code items} list to {@code savedteams.json}
     * in JSON format with the key {@code "savedteams"}.</p>
     *
     * <p>If a write error occurs, a message is printed.</p>
     */
    public void save() throws IOException {
        final JsonObject root = new JsonObject();
        root.add("savedteams", this.gson.toJsonTree(this.items));

        try (final FileWriter writer = new FileWriter(PathConstants.DB_SAVED_TEAMS)) {
            this.gson.toJson(root, writer);
        }
    }

    /**
     * Adds a new saved team.
     *
     * <p>If a team with the same name already exists, nothing is done
     * (duplicates are silently ignored).</p>
     *
     * <p>The team is added to memory AND persisted to disk via {@link #save()}.</p>
     *
     * @param team the team to add (must not be {@code null})
     * @throws IOException if a persistence error occurs
     */
    public void addTeam(final SavedTeam team) throws IOException {
        this.buildDataBase();
        if (this.itemsMap.containsKey(team.name())){return;}
        this.items.add(team);
        this.itemsMap.put(team.name(), team);
        this.save();
    }

    /**
     * Deletes a saved team by its name.
     *
     * <p>If the team does not exist, nothing is done.</p>
     *
     * <p>The team is removed from memory AND from the persisted file via {@link #save()}.</p>
     *
     * @param name the name of the team to delete
     * @throws IOException if a persistence error occurs
     */
    public void deleteTeam(final String name) throws IOException, TeamNotFoundException {
        try {
            final SavedTeam teamToRemove = this.findByName(name);
            this.items.remove(teamToRemove);
            this.itemsMap.remove(name);
            this.save();
        } catch (final ElementNotFoundException e) {
            throw new TeamNotFoundException("Cannot remove team not in repository: " + name, e);
        }

    }

    /**
     * Gets the complete list of saved teams.
     *
     * <p>Reloads data from the file first via {@link #buildDataBase()}
     * to ensure consistency with disk.</p>
     *
     * <p>Returns a copy of the list to prevent external modifications.</p>
     *
     * @return a new list containing all saved teams
     * @throws IOException if a disk read error occurs
     */
    public List<SavedTeam> getSavedTeams() throws IOException {
        this.buildDataBase();
        return this.getList();
    }

    /**
     * Searches for a saved team by its name.
     *
     * <p>Reloads data from the file first via {@link #buildDataBase()}
     * to ensure consistency with disk.</p>
     *
     * <p>Uses the map index for O(1) access.</p>
     *
     * @param name the name of the team to search for
     * @return the corresponding team
     * @throws IOException if a disk read error occurs
     * @throws ElementNotFoundException if no team with the given name exists
     */
    public SavedTeam findByName(final String name) throws IOException, ElementNotFoundException {
        this.buildDataBase();
        return this.findById(name);
    }

    /**
     * Checks if a team with the given name exists.
     *
     * <p>Reloads data from the file first via {@link #buildDataBase()}
     * to ensure consistency with disk.</p>
     *
     * @param name the name to check
     * @return {@code true} if a team with this name exists, {@code false} otherwise
     * @throws IOException if a disk read error occurs
     */
    public boolean checkNameOfTeam(final String name) throws IOException {
        this.buildDataBase();
        return this.itemsMap.containsKey(name);
    }

    /**
     * Gets the list of names of all saved teams.
     *
     * <p>Reloads data from the file first via {@link #buildDataBase()}
     * to ensure consistency with disk.</p>
     *
     * @return a new list containing the names of all saved teams
     * @throws IOException if a disk read error occurs
     */
    public List<String> getAllTeamNames() throws IOException {
        this.buildDataBase();
        final List<String> names = new ArrayList<>();
        for (final SavedTeam team : this.items) {
            names.add(team.name());
        }
        return Collections.unmodifiableList(names);
    }
    
}
