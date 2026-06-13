package constants;

/**
 * UI Label constants for titles, messages, and static text.
 */
public final class LabelConstants {
    private LabelConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Window Titles
    public static final String TITLE_COMBAT = "Combat";
    // Common Labels
    public static final String LABEL_FREE_COMBAT = "Combat libre";
    public static final String LABEL_FLOOR_PREFIX = "Etage ";
    public static final String LABEL_NO_BONUS = "Aucun bonus";

    // Tower display labels
    public static final String FLOOR_LABEL_PREFIX = "NO";
    public static final String LABEL_STEP_COMBAT = "Combat %d";
    public static final String LABEL_STEP_BOSS   = "Boss";
    public static final String LABEL_STEP_REWARD = "Récompense %d";

    // Combat Outcomes
    public static final String FORFEIT_MESSAGE = "Vous avez abandonné le combat !";

    // Default Names
    public static final String DEFAULT_PLAYER_NAME = "Joueur";
}
