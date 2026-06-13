package constants;

/**
 * Centralized color constants used across the application UI.
 * Follows the standard hex color format for JavaFX and CSS.
 */
public final class ColorConstants {
    /**
     * Private constructor to prevent instantiation of this utility class.
     * @throws UnsupportedOperationException if called.
     */
    private ColorConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Type Colors
    public static final String TYPE_PYRO = "#FF4C0D";
    public static final String TYPE_AQUA = "#1A8FE3";
    public static final String TYPE_FLORA = "#2EAD46";
    public static final String TYPE_LITHO = "#8B6F47";

    // HP Bar Status Colors
    public static final String HP_HIGH = "#4caf50";   // Green
    public static final String HP_MEDIUM = "#ff9800"; // Orange
    public static final String HP_LOW = "#f44336";    // Red

    // UI Feedback Colors
    public static final String WARNING = "#ffc107";
    public static final String INFO = "#17a2b8";
}
