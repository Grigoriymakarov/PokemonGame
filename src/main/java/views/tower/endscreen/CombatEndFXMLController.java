package views.tower.endscreen;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import views.BaseFXMLController;

import java.util.Optional;

import static constants.ColorConstants.*;

/**
 * The FXML controller for the combat end screen in the tower mode.
 *
 * <p>This class manages the UI components of the combat end screen, including displaying the player's team details and floor information.</p>
 *
 * <p>It provides methods to add Bugemon details to the team display, clear existing details, and manage the floor label visibility and text.</p>
 *
 * <p>The controller interacts with a listener that implements {@link CombatEndView.CombatEndListener} to handle user actions such as returning to the main menu.</p>
 *
 * @param <L> the type of listener that handles events from this controller, must extend {@link CombatEndView.CombatEndListener}
 */
public class CombatEndFXMLController<L extends CombatEndView.CombatEndListener> extends BaseFXMLController<L> {

    @FXML
    protected VBox vboxBugemons;
    @FXML
    protected Label floorLabel;
    @FXML
    private Button mainmenuButton;

    @FXML
    protected void initialize() {
        this.mainmenuButton.setOnAction(event -> {
            this.listener.onMainClicked();
        });
    }

    public void setFloorLabel(final String name) {
        this.floorLabel.setText("Étage " + name);
    }

    /**
     * Applies the appropriate HP bar color based on the HP ratio.
     *
     * <p>This method determines the color of the HP bar based on the health ratio:
     * - Green (#4caf50) for more than 50% HP
     * - Orange (#ff9800) for 25-50% HP
     * - Red (#f44336) for less than 25% HP</p>
     *
     * @param bar   the ProgressBar to style
     * @param ratio the health ratio (0.0 to 1.0)
     */
    private static void applyHpBarColor(final ProgressBar bar, final double ratio) {
        final String color = ratio > 0.5 ? HP_HIGH : ratio > 0.25 ? HP_MEDIUM : HP_LOW;
        bar.setStyle("-fx-accent: " + color + ";");
    }

    /**
     * Hides the floor label from the UI and removes it from the layout.
     *
     * <p>Should be called in free combat mode, where no floor number is relevant.
     * Setting {@code managed} to {@code false} ensures the label does not occupy
     * space in the layout even when invisible.</p>
     */

    public void hideFloorLabel() {
        this.floorLabel.setVisible(false);
        this.floorLabel.setManaged(false);
    }

    /**
     * Clears all Bugemon entries from the team list.
     * Should be called before repopulating the list to avoid duplicates.
     */
    public void clearTeam(){
        // To empty the view in case of
        this.vboxBugemons.getChildren().clear();
    }

    /**
     * Adds a card displaying a Bugemon's details to the team list.
     * The first row contains the Bugemon's name, current HP, level and HP progress bar.
     * The second contains a progress bar about current XP.
     *
     * @param playerName  the display name of the Bugemon
     * @param currentHP   the current HP of the Bugemon
     * @param maxHP       the maximum HP of the Bugemon
     * @param level       the current level of the Bugemon
     * @param currentXP   the current XP of the Bugemon
     * @param xpThreshold the XP threshold for next level
     */
    public void addToTeamDisplay(final String playerName, final int currentHP, final int maxHP, final int level, final int currentXP, final int xpThreshold) {
        final VBox card = CombatEndFXMLController.generateCardWithTopRow(playerName, currentHP, maxHP, Optional.of(level));

        final HBox xpRow = new HBox(8);
        xpRow.setAlignment(Pos.CENTER_LEFT);

        final Label xpLabel = new Label("XP : " + currentXP + " / " + xpThreshold);
        xpLabel.getStyleClass().add("xp-label");

        final ProgressBar xpBar = new ProgressBar((double) currentXP / xpThreshold);
        xpBar.setPrefWidth(150);
        xpBar.getStyleClass().add("xp-bar");

        xpRow.getChildren().addAll(xpLabel, xpBar);

        card.getChildren().addAll(xpRow);

        this.vboxBugemons.getChildren().add(card);
    }

    /**
     * Adds a row displaying a Bugemon's details to the team list.
     * Used for a non tower mode.
     *
     * @param playerName  the display name of the Bugemon
     * @param currentHP   the current HP of the Bugemon
     * @param maxHP       the maximum HP of the Bugemon
     */
    public void addToTeamDisplay(final String playerName, final int currentHP, final int maxHP) {
        final VBox card = CombatEndFXMLController.generateCardWithTopRow(playerName, currentHP, maxHP, Optional.empty());
        this.vboxBugemons.getChildren().add(card);
    }

    /**
     * Creates a card with row displaying a Bugemon's details: name, current HP, and HP progress bar.
     *
     * @param playerName  the display name of the Bugemon
     * @param currentHP   the current HP of the Bugemon
     * @param maxHP       the maximum HP of the Bugemon
     * @param level       optional current level of the Bugemon
     * @return a VBox containing the Bugemon card content
     */
    private static VBox generateCardWithTopRow(final String playerName, final int currentHP, final int maxHP, Optional<Integer> level) {
        final VBox card = new VBox(6);
        card.getStyleClass().add("bugemon-card");

        final HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        final Label nameLabel = new Label(playerName);
        nameLabel.getStyleClass().add("bugemon-card-name");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        final Label hpLabel = new Label("HP : " + currentHP + " / " + maxHP);
        hpLabel.getStyleClass().add("hp-label");

        final ProgressBar hpBar = new ProgressBar((double) currentHP / maxHP);
        hpBar.setPrefWidth(120);
        CombatEndFXMLController.applyHpBarColor(hpBar, (double) currentHP / maxHP);

        if  (level.isPresent()) {
            final Label levelLabel = new Label("Niv. " + level.get());
            levelLabel.getStyleClass().add("level-badge");
            topRow.getChildren().addAll(nameLabel, levelLabel, hpLabel, hpBar);
        } else {
            topRow.getChildren().addAll(nameLabel, hpLabel, hpBar);
        }

        card.getChildren().addAll(topRow);

        return card;
    }
}
