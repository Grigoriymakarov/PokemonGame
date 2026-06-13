package views.combat;

import constants.MessageConstants;
import dto.CombatStateDTO;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import views.TypeTheme;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static constants.ColorConstants.*;

/**
 * Renders a {@link CombatStateDTO} onto the combat screen widgets.
 *
 * <p>This class owns no FXML fields directly; it receives references to the widgets
 * it needs to update and refreshes them on each call to {@link #render(CombatStateDTO)}.</p>
 *
 * <p>All widget references are supplied at construction time and are expected to remain
 * non-null for the lifetime of this instance. {@link #render(CombatStateDTO)} is the
 * single entry point for updating the UI; it is idempotent and safe to call repeatedly.</p>
 *
 * <p><b>Must be used on the JavaFX Application Thread.</b></p>
 */
final class CombatRenderer {

    private static final Logger LOGGER = Logger.getLogger(CombatRenderer.class.getName());

    /** Sprite image view for the player's active Bugémon. */
    private final ImageView playerSprite;

    /** Sprite image view for the enemy's active Bugémon. */
    private final ImageView enemySprite;

    /** Label displaying the player Bugémon's name and level. */
    private final Label playerNameLabel;

    /** Label displaying the enemy Bugémon's name and level. */
    private final Label enemyNameLabel;

    /** Icon representing the player Bugémon's type. */
    private final ImageView playerTypeIcon;

    /** Icon representing the enemy Bugémon's type. */
    private final ImageView enemyTypeIcon;

    /** HP progress bar for the player's Bugémon. */
    private final ProgressBar playerHpBar;

    /** HP progress bar for the enemy's Bugémon. */
    private final ProgressBar enemyHpBar;

    /** Label showing the player Bugémon's current and maximum HP. */
    private final Label playerHpLabel;

    /** Label showing the enemy Bugémon's current and maximum HP. */
    private final Label enemyHpLabel;

    /** List view displaying the combat event log. */
    private final ListView<String> battleLogList;

    /** Label showing the current dungeon floor name. */
    private final Label floorNameLabel;

    /** Label showing the current step name, styled differently for boss fights. */
    private final Label stepNameLabel;

    /**
     * Creates a {@code CombatRenderer} bound to the given UI widgets.
     *
     * <p>All parameters are stored by reference; no defensive copy is made.
     * Passing {@code null} for any widget is permitted but may cause
     * {@link NullPointerException} during {@link #render(CombatStateDTO)},
     * except for {@link ImageView} parameters which are guarded by
     * {@link #setImage(ImageView, String)}.</p>
     *
     * @param playerSprite    image view for the player's Bugémon sprite
     * @param enemySprite     image view for the enemy's Bugémon sprite
     * @param playerNameLabel label for the player Bugémon's name and level
     * @param enemyNameLabel  label for the enemy Bugémon's name and level
     * @param playerTypeIcon  image view for the player Bugémon's type icon
     * @param enemyTypeIcon   image view for the enemy Bugémon's type icon
     * @param playerHpBar     HP progress bar for the player's Bugémon
     * @param enemyHpBar      HP progress bar for the enemy's Bugémon
     * @param playerHpLabel   label showing the player Bugémon's HP as {@code current / max}
     * @param enemyHpLabel    label showing the enemy Bugémon's HP as {@code current / max}
     * @param battleLogList   list view displaying combat event messages
     * @param floorNameLabel  label showing the current dungeon floor name
     * @param stepNameLabel   label showing the current step name
     */
    CombatRenderer(
            final ImageView playerSprite,
            final ImageView enemySprite,
            final Label playerNameLabel,
            final Label enemyNameLabel,
            final ImageView playerTypeIcon,
            final ImageView enemyTypeIcon,
            final ProgressBar playerHpBar,
            final ProgressBar enemyHpBar,
            final Label playerHpLabel,
            final Label enemyHpLabel,
            final ListView<String> battleLogList,
            final Label floorNameLabel,
            final Label stepNameLabel
    ) {
        this.playerSprite    = playerSprite;
        this.enemySprite     = enemySprite;
        this.playerNameLabel = playerNameLabel;
        this.enemyNameLabel  = enemyNameLabel;
        this.playerTypeIcon  = playerTypeIcon;
        this.enemyTypeIcon   = enemyTypeIcon;
        this.playerHpBar     = playerHpBar;
        this.enemyHpBar      = enemyHpBar;
        this.playerHpLabel   = playerHpLabel;
        this.enemyHpLabel    = enemyHpLabel;
        this.battleLogList   = battleLogList;
        this.floorNameLabel  = floorNameLabel;
        this.stepNameLabel   = stepNameLabel;
    }

    /**
     * Updates all combat screen widgets to reflect the given state.
     *
     * <p>The following widgets are refreshed on each call:</p>
     * <ul>
     *   <li>player and enemy name labels (name + level);</li>
     *   <li>player and enemy type icons;</li>
     *   <li>player and enemy HP bars (progress and colour);</li>
     *   <li>player and enemy HP labels ({@code current / max});</li>
     *   <li>floor and step labels (the step label CSS class switches between
     *       {@code step-label} and {@code step-label-boss} depending on
     *       {@link CombatStateDTO#isBossFight()});</li>
     *   <li>battle log list (new event messages are appended and the view
     *       scrolls to the last entry);</li>
     *   <li>player and enemy sprites.</li>
     * </ul>
     *
     * @param state the combat state to render; if {@code null}, the method
     *              returns immediately without modifying any widget
     */
    void render(final CombatStateDTO state) {
        if (state == null) return;

        this.playerNameLabel.setText(nullSafe(state.getPlayerName() + " (Lv." + state.getPlayerLevel() + ")"));
        this.enemyNameLabel.setText(nullSafe(state.getEnemyName() + " (Lv." + state.getEnemyLevel() + ")"));

        this.setImage(this.playerTypeIcon, TypeTheme.of(state.getPlayerType()).iconPath());
        this.setImage(this.enemyTypeIcon,  TypeTheme.of(state.getEnemyType()).iconPath());

        this.playerHpLabel.setText(state.getPlayerHp() + " / " + state.getPlayerHpMax());
        this.enemyHpLabel.setText(state.getEnemyHp()   + " / " + state.getEnemyHpMax());

        final double playerRatio = safeRatio(state.getPlayerHp(), state.getPlayerHpMax());
        final double enemyRatio  = safeRatio(state.getEnemyHp(),  state.getEnemyHpMax());

        this.playerHpBar.setProgress(playerRatio);
        CombatRenderer.applyHpBarColor(this.playerHpBar, playerRatio);

        this.enemyHpBar.setProgress(enemyRatio);
        CombatRenderer.applyHpBarColor(this.enemyHpBar, enemyRatio);

        this.floorNameLabel.setText(nullSafe(state.getFloorLabel()));
        this.stepNameLabel.setText(nullSafe(state.getStepLabel()));
        this.stepNameLabel.getStyleClass().setAll(state.isBossFight() ? "step-label-boss" : "step-label");

        final List<String> messages = state.getEvents().stream()
                .flatMap(event -> Arrays.stream(event.toString().split("\n")))
                .collect(Collectors.toList());
        if (!messages.isEmpty()) {
            this.battleLogList.setItems(FXCollections.observableArrayList(messages));
            this.battleLogList.scrollTo(messages.size() - 1);
        }

        this.setImage(this.playerSprite, state.getPlayerSpritePath());
        this.setImage(this.enemySprite,  state.getEnemySpritePath());
    }

    /**
     * Applies a colour to an HP progress bar based on the current HP ratio.
     *
     * <ul>
     *   <li>{@code ratio > 0.5}        → {@code HP_HIGH}   (green)</li>
     *   <li>{@code 0.25 < ratio ≤ 0.5} → {@code HP_MEDIUM} (orange)</li>
     *   <li>{@code ratio ≤ 0.25}       → {@code HP_LOW}    (red)</li>
     * </ul>
     *
     * @param bar   the progress bar to style
     * @param ratio current HP / max HP ratio, expected to be in {@code [0.0, 1.0]}
     */
    private static void applyHpBarColor(final ProgressBar bar, final double ratio) {
        final String color = ratio > 0.5 ? HP_HIGH : ratio > 0.25 ? HP_MEDIUM : HP_LOW;
        bar.setStyle("-fx-accent: " + color + ";");
    }

    /**
     * Loads an image from the classpath and assigns it to the given {@link ImageView}.
     *
     * <p>If {@code path} does not start with {@code /}, it is resolved relative to
     * {@code /png/}. If the resource cannot be found, the image view is cleared.
     * Any exception thrown during loading is caught, logged at {@link Level#WARNING},
     * and results in the image view being cleared.</p>
     *
     * @param view the image view to update; if {@code null}, the method returns immediately
     * @param path classpath path to the image resource; if {@code null} or blank,
     *             the image view is cleared
     */
    private void setImage(final ImageView view, final String path) {
        if (view == null) return;
        if (path == null || path.isBlank()) {
            view.setImage(null);
            return;
        }
        final String resourcePath = path.startsWith("/") ? path : "/png/" + path;
        try {
            final java.net.URL resource = this.getClass().getResource(resourcePath);
            view.setImage(resource != null ? new Image(resource.toExternalForm()) : null);
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, MessageConstants.ERROR_SPRITE_LOAD, e);
            view.setImage(null);
        }
    }

    /**
     * Computes a safe HP ratio clamped to {@code [0.0, 1.0]}.
     *
     * @param hp    current HP value
     * @param hpMax maximum HP value; if {@code ≤ 0}, returns {@code 0.0}
     * @return the ratio {@code hp / hpMax}, clamped to {@code [0.0, 1.0]}
     */
    private static double safeRatio(final int hp, final int hpMax) {
        if (hpMax <= 0) return 0.0;
        final double ratio = (double) hp / (double) hpMax;
        return Math.max(0.0, Math.min(1.0, ratio));
    }

    /**
     * Returns the given string, or an empty string if it is {@code null}.
     *
     * @param s the string to check
     * @return {@code s} if non-null, {@code ""} otherwise
     */
    private static String nullSafe(final String s) {
        return (s == null) ? "" : s;
    }
}