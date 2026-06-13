package constants;

/**
 * UI Constants for layout, sizes, and shared parameters.
 */
public final class UIConstants {
    private UIConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Header for error pop-ups
    public static final String ERROR_HEADER_DEFAULT = "Erreur";

    // Bugemon stats preview in team selection
    public static final String BUGEMON_TOOLTIP_FORMAT =
            "Type: %s\n"
            + "Stats %s:\n"
            + " - HP %d\n"
            + " - Attaque %d\n"
            + " - Défense %d\n"
            + " - Initiative %d";
    public static final String BUGEMON_TOOLTIP_ACTUAL = "actuelles";
    public static final String BUGEMON_TOOLTIP_BASE = "de base";
    public static final String BUGEMON_TOOLTIP_LEVEL_HEADER = "Niveau: %d";
    public static final String BUGEMON_TOOLTIP_XP_FOOTER = "XP: %d / %d pour niveau suivant";

    // Attack choice hint
    public static final String HINT_SUPER_EFFECTIVE = "(super efficace)";
    public static final String HINT_NOT_VERY_EFFECTIVE = "(peu efficace)";
    public static final String HINT_NEUTRAL = "";

    // Other UI related values could go here
}
