package constants;

/**
 * Global path constants for resources, FXML, and JSON files.
 * Provides a single source of truth for resource locations.
 */
public final class PathConstants {
    private PathConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // FXML Files
    public static final String FXML_COMBAT = "/fxml/CombatView.fxml";
    public static final String FXML_HOME = "/fxml/HomeScreen.fxml";
    public static final String FXML_INVENTORY = "/fxml/InventoryView.fxml";
    public static final String FXML_TEAM_SELECTION = "/fxml/TeamSelection.fxml";
    public static final String FXML_TEAMS_MANAGE = "/fxml/TeamsManage.fxml";
    public static final String FXML_LEVEL_UP_REWARD = "/fxml/LevelUpRewardView.fxml";
    public static final String FXML_INTERMEDIATE_WIN = "/fxml/IntermediateWinView.fxml";
    public static final String FXML_INTERMEDIATE_LOSE = "/fxml/IntermediateLoseView.fxml";
    public static final String FXML_REWARD = "/fxml/RewardView.fxml";
    public static final String FXML_FLOOR_UNLOCK = "/fxml/FloorUnlockView.fxml";
    public static final String FXML_TOWER_WIN = "/fxml/TowerWinView.fxml";

    // JSON Data
    public static final String JSON_ATTACKS = "/json/attaques.json";
    public static final String JSON_BUGEMONS = "/json/bugemons.json";
    public static final String JSON_ITEMS = "/json/objets.json";


    // Local Database
    public static final String DB_DIR = ".bugemondb/";
    public static final String DB_SAVED_TEAMS = PathConstants.DB_DIR + "savedteams.json";

    // Assets
    public static final String PNG_PATH = "/png/";
    public static final String TYPE_ICONS_PATH = "/png/typeIcons/";
}
