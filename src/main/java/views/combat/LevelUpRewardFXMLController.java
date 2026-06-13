package views.combat;

import views.BaseFXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

/**
 * JavaFX FXML controller for the level-up bonus selection screen.
 *
 * <p>This controller handles the display of bonus options and manages
 * user interaction with the bonus selection UI.</p>
 */
public class LevelUpRewardFXMLController extends BaseFXMLController<LevelUpRewardView.Listener> {

    @FXML
    private Label bugemonNameLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private VBox bonusOptionsBox;

    /**
     * Displays the level-up bonus options.
     *
     * @param bugemonName the name of the Bugémon that leveled up
     * @param oldLevel the previous level
     * @param newLevel the new level
     * @param bonusOptions list of bonus descriptions (e.g., "+20 PV", "+10 Attaque")
     * @param onBonusSelected callback when a bonus is selected (receives index: 0, 1, or 2)
     */
    public void displayBonusOptions(final String bugemonName, final int oldLevel, final int newLevel,
                                    final List<String> bonusOptions,
                                    final Consumer<Integer> onBonusSelected) {
        this.bugemonNameLabel.setText(bugemonName);
        this.levelLabel.setText("Niveau " + oldLevel + " → " + newLevel);

        this.bonusOptionsBox.getChildren().clear();

        // Create 3 buttons for the bonus options
        for (int i = 0; i < bonusOptions.size(); i++) {
            final String bonusText = bonusOptions.get(i);

            final Button bonusButton = this.createBonusButton(bonusText, i, onBonusSelected);
            this.bonusOptionsBox.getChildren().add(bonusButton);
        }
    }

    /**
     * Creates a styled button for a bonus option.
     *
     * @param bonusText the text describing the bonus
     * @param index the index of this bonus (0, 1, or 2)
     * @param onBonusSelected callback when clicked
     * @return the created Button
     */
    private Button createBonusButton(final String bonusText, final int index, final Consumer<Integer> onBonusSelected) {
        final Button button = new Button();
        button.setText(bonusText);
        button.getStyleClass().add("bonus-btn");

        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(60);
        button.setWrapText(true);

        button.setOnAction(event -> onBonusSelected.accept(index));

        return button;
    }
}

