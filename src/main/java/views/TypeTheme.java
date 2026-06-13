package views;

import constants.ColorConstants;
import constants.PathConstants;
import models.shared.Type;

import java.util.EnumMap;

/**
 * Represents a visual theme for a Bugemon type, consisting of a color and an icon.
 * <p>
 * This class manages the visual representation of different Bugemon types in the UI,
 * including their hexadecimal color codes and icon file paths. It provides a centralized
 * registry of themes for all Bugemon types (PYRO, AQUA, FLORA, LITHO).
 * </p>
 *
 * @param hexColor the hexadecimal color code (e.g., "#FF4C0D")
 * @param iconPath the file path to the icon image (e.g., "typeIcons/pyro.png")
 */
public record TypeTheme(String hexColor, String iconPath) {

    /**
     * Registry mapping Bugemon types to their visual themes.
     */
    private static final EnumMap<Type, TypeTheme> themes = new EnumMap<>(Type.class);

    static {
        TypeTheme.themes.put(Type.PYRO, new TypeTheme(ColorConstants.TYPE_PYRO, PathConstants.TYPE_ICONS_PATH + "pyro.png"));
        TypeTheme.themes.put(Type.AQUA, new TypeTheme(ColorConstants.TYPE_AQUA, PathConstants.TYPE_ICONS_PATH + "aqua.png"));
        TypeTheme.themes.put(Type.FLORA, new TypeTheme(ColorConstants.TYPE_FLORA, PathConstants.TYPE_ICONS_PATH + "flora.png"));
        TypeTheme.themes.put(Type.LITHO, new TypeTheme(ColorConstants.TYPE_LITHO, PathConstants.TYPE_ICONS_PATH + "litho.png"));
    }

    /**
     * Retrieves the theme associated with the specified Bugemon type.
     *
     * @param type the Bugemon type for which to retrieve the theme
     * @return the TypeTheme associated with the given type
     * @throws IllegalArgumentException if no theme is registered for the specified type
     */
    public static TypeTheme of(final Type type) {
        if (!TypeTheme.themes.containsKey(type)) throw new IllegalArgumentException("No theme registered for: " + type);
        return TypeTheme.themes.get(type);
    }

    /**
     * Returns the hexadecimal color code for this theme.
     *
     * @return the hex color string
     */
    @Override
    public String hexColor() {
        return this.hexColor;
    }

    /**
     * Returns the icon file path for this theme.
     *
     * @return the path to the icon image file
     */
    @Override
    public String iconPath() {
        return this.iconPath;
    }

}

