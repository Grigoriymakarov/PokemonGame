package views.combat;

import constants.MessageConstants;
import dto.TrainerBugemonDTO;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static constants.ColorConstants.*;

/**
 * Handles voluntary and forced Bugémon switch dialogs.
 *
 * <p>Centralises all JavaFX dialog construction related to Bugémon switching,
 * keeping {@link CombatFXMLController} free of dialog-building code.</p>
 *
 * <p>Switch candidates and their HP data are stored internally between the call
 * to {@link #setSwitchCandidatesWithHp} and the next dialog opening.</p>
 *
 * <p>Selection results are propagated through {@link CombatView.Listener}; no direct
 * dependency on the main controller is required.</p>
 *
 * <p><b>Typical usage:</b></p>
 * <pre>{@code
 * SwitchDialogHandler handler = new SwitchDialogHandler();
 * handler.setListener(combatListener);
 * handler.setSwitchCandidatesWithHp(candidates, hpData);
 * handler.openSwitchDialog();               // voluntary switch
 * handler.openForcedSwitchDialog(candidates); // forced switch after K.O.
 * }</pre>
 *
 * <p><b>Thread safety:</b> all public methods must be called from the
 * <i>JavaFX Application Thread</i>. {@link #openForcedSwitchDialog} internally
 * handles deferral via {@link Platform#runLater}.</p>
 */
final class SwitchDialogHandler {

    private static final Logger LOGGER = Logger.getLogger(SwitchDialogHandler.class.getName());

    /** Bugémon available for the next switch. */
    private Collection<TrainerBugemonDTO> switchCandidates = new ArrayList<>();

    /**
     * HP data associated with each candidate.
     * Each {@code int[]} array contains {@code [currentHp, maxHp]}.
     */
    private Map<TrainerBugemonDTO, int[]> switchHpData = new HashMap<>();

    /** Listener that receives switch selection events. */
    private CombatView.Listener listener;

    /**
     * Sets the listener that will be notified of Bugémon switch events.
     *
     * @param listener the {@link CombatView.Listener} implementation to notify;
     *                 may be {@code null} to disable notifications
     */
    void setListener(final CombatView.Listener listener) {
        this.listener = listener;
    }

    /**
     * Stores the switch candidates and their HP data for the next dialog.
     *
     * <p>Both collections are defensively copied; subsequent modifications
     * to the originals have no effect.</p>
     *
     * @param candidates list of Bugémon that can replace the active one;
     *                   {@code null} is treated as an empty list
     * @param hpData     map associating each Bugémon with an {@code int[2]} array
     *                   of the form {@code {currentHp, maxHp}};
     *                   {@code null} is treated as an empty map
     */
    void setSwitchCandidatesWithHp(
            final List<? extends TrainerBugemonDTO> candidates,
            final Map<? extends TrainerBugemonDTO, int[]> hpData
    ) {
        this.switchCandidates = (candidates == null) ? new ArrayList<>() : new ArrayList<>(candidates);
        this.switchHpData     = (hpData == null)     ? new HashMap<>()   : new HashMap<>(hpData);
    }

    /**
     * Opens a voluntary switch dialog.
     *
     * <p>Displays the candidates previously registered via
     * {@link #setSwitchCandidatesWithHp}. If the player selects a Bugémon,
     * {@link CombatView.Listener#onSwitchClicked(String)} is called with its name.
     * If the player cancels or closes the dialog, no event is fired.</p>
     *
     * <p>If no candidates are available, an information alert is shown instead.</p>
     */
    void openSwitchDialog() {
        if (this.switchCandidates.isEmpty()) {
            showNoSwitchCandidatesMessage();
            return;
        }

        final Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Changer de Bugémon");
        dialog.setHeaderText("Choisissez un Bugémon");

        final ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(cancelButtonType);
        dialog.setOnCloseRequest(e -> dialog.setResult(cancelButtonType));

        final String[] selectedName = {null};
        final VBox content = buildCandidateList(this.switchCandidates, bugemon -> {
            selectedName[0] = bugemon.getName();
            dialog.setResult(ButtonType.OK);
            dialog.close();
        });

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();

        if (this.listener != null && selectedName[0] != null) {
            this.listener.onSwitchClicked(selectedName[0]);
        }
    }

    /**
     * Opens a forced switch dialog after the active Bugémon is knocked out.
     *
     * <p>Unlike {@link #openSwitchDialog()}, this dialog provides no cancel button:
     * the player must choose a replacement. If the window is closed without a
     * selection, the first candidate is used as the default.</p>
     *
     * <p><b>Implementation note — rendering on Linux (GTK pipeline):</b><br>
     * The dialog construction is intentionally deferred via {@link Platform#runLater},
     * even when this method is already called from the JavaFX Application Thread.
     * On Linux, opening this dialog immediately after certain UI interactions
     * (e.g. selecting an item in the inventory) causes a silent <em>renderless</em>
     * effect: the dialog is fully functional but its content is never painted.
     * The root cause is that the preceding interaction consumes the current JavaFX
     * render pulse, leaving no render pass scheduled for the new dialog.
     * Deferring via {@code runLater} places the dialog construction at the end of
     * the event queue, after the current pulse completes, allowing the graphics
     * engine to correctly trigger a new render pass.</p>
     *
     * <p><b>Must be called from the JavaFX Application Thread.</b></p>
     *
     * @param candidates alive Bugémon that can replace the knocked-out one;
     *                   if {@code null} or empty, an information alert is shown
     *                   and no event is fired
     */
    void openForcedSwitchDialog(final List<? extends TrainerBugemonDTO> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            showNoSwitchCandidatesMessage();
            return;
        }
        Platform.runLater(() -> this.buildAndShowDialog(candidates));
    }

    /**
     * Builds and displays the forced switch dialog.
     *
     * <p>Invoked internally from {@link Platform#runLater} by
     * {@link #openForcedSwitchDialog}. The selected name is forwarded to
     * {@link #listener} via {@link CombatView.Listener#onSwitchClicked(String)}.</p>
     *
     * @param candidates non-empty list of Bugémon available as replacements
     */
    private void buildAndShowDialog(final List<? extends TrainerBugemonDTO> candidates) {
        final Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Bugémon K.O.");
        dialog.setHeaderText("Choisissez un remplaçant");
        dialog.getDialogPane().getButtonTypes().clear();

        final VBox content = buildCandidateList(candidates, bugemon -> {
            dialog.setResult(bugemon.getName());
            dialog.close();
        });
        content.setAlignment(Pos.CENTER);
        dialog.getDialogPane().setContent(content);

        final String chosen = dialog.showAndWait().orElse(candidates.get(0).getName());
        if (this.listener != null) {
            this.listener.onSwitchClicked(chosen);
        }
    }

    /**
     * Builds a {@link VBox} containing one clickable entry per candidate.
     *
     * <p>Each entry is created by {@link #buildBugemonEntry}; the provided
     * {@code onSelect} callback is wired to the main button of each entry.</p>
     *
     * @param candidates collection of Bugémon to display
     * @param onSelect   callback invoked with the chosen {@link TrainerBugemonDTO}
     *                   when the player clicks an entry
     * @return the populated container, ready to be set as dialog content
     */
    private VBox buildCandidateList(
            final Collection<? extends TrainerBugemonDTO> candidates,
            final java.util.function.Consumer<TrainerBugemonDTO> onSelect
    ) {
        final VBox content = new VBox(10);
        content.getStyleClass().add("dialog-content-padded");

        for (final TrainerBugemonDTO bugemon : candidates) {
            final VBox entry = this.buildBugemonEntry(bugemon);
            final Button btn = (Button) entry.getChildren().get(0);
            btn.setOnAction(e -> onSelect.accept(bugemon));
            content.getChildren().add(entry);
        }
        return content;
    }

    /**
     * Builds the visual component representing a single Bugémon in the dialog.
     *
     * <p>The component consists of:</p>
     * <ul>
     *   <li>a button showing the Bugémon's display name and level, with its
     *       sprite displayed above the label if found in the classpath resources;</li>
     *   <li>a colour-coded HP progress bar;</li>
     *   <li>a text label showing {@code currentHp / maxHp}.</li>
     * </ul>
     *
     * <p>If the sprite cannot be found or fails to load, the button is rendered
     * without an image and the error is logged at {@link Level#WARNING}.</p>
     *
     * <p>If no HP data is associated with the Bugémon in {@link #switchHpData},
     * both the bar and the label display {@code 0 / 0}.</p>
     *
     * @param bugemon the Bugémon to represent
     * @return a centred {@link VBox} containing the three sub-components described above
     */
    private VBox buildBugemonEntry(final TrainerBugemonDTO bugemon) {
        final VBox entry = new VBox(4);
        entry.getStyleClass().add("switch-row");
        entry.setAlignment(Pos.CENTER);

        final String displayName = bugemon.getDisplayName() + " (Lv." + bugemon.getLevel() + ")";
        final Button btn = new Button(displayName);
        btn.getStyleClass().add("bugemon-switch-button");

        try {
            final String imagePath = "/png/" + bugemon.getName().toLowerCase() + ".png";
            final InputStream is = this.getClass().getResourceAsStream(imagePath);
            if (is != null) {
                final ImageView iv = new ImageView(new Image(is));
                iv.setFitWidth(64);
                iv.setFitHeight(64);
                iv.setPreserveRatio(true);
                btn.setGraphic(iv);
                btn.setContentDisplay(ContentDisplay.TOP);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, MessageConstants.ERROR_SPRITE_LOAD, e);
        }

        final int[] hpArr  = this.switchHpData.getOrDefault(bugemon, new int[]{0, 0});
        final int hp       = hpArr[0];
        final int hpMax    = hpArr[1];
        final double ratio = (hpMax > 0) ? Math.max(0, Math.min(1, (double) hp / hpMax)) : 0;

        final ProgressBar hpBar = new ProgressBar(ratio);
        hpBar.setPrefWidth(140);
        hpBar.setPrefHeight(10);
        applyHpBarColor(hpBar, ratio);

        final Label hpLabel = new Label(hp + " / " + hpMax);
        hpLabel.getStyleClass().add("hp-label-small");

        entry.getChildren().addAll(btn, hpBar, hpLabel);
        return entry;
    }

    /**
     * Applies a colour to the HP bar based on the current HP ratio.
     *
     * <ul>
     *   <li>{@code ratio > 0.5}  → {@code HP_HIGH}   (green)</li>
     *   <li>{@code 0.25 < ratio ≤ 0.5} → {@code HP_MEDIUM} (orange)</li>
     *   <li>{@code ratio ≤ 0.25} → {@code HP_LOW}    (red)</li>
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
     * Displays an information alert indicating that no switch is currently possible.
     *
     * <p>Used as a fallback in {@link #openSwitchDialog()} and
     * {@link #openForcedSwitchDialog} when the candidate list is empty or
     * {@code null}.</p>
     */
    private static void showNoSwitchCandidatesMessage() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Changement impossible");
        alert.setHeaderText(null);
        alert.setContentText("Aucun Bugémon disponible pour le changement.");
        alert.showAndWait();
    }
}