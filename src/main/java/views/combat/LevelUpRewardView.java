package views.combat;

import views.BaseView;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * View for the level-up bonus selection screen.
 *
 * <p>Displays 3 random bonus options for the player to choose from.</p>
 *
 * @see LevelUpRewardFXMLController
 * @see Listener
 */
public class LevelUpRewardView extends BaseView<LevelUpRewardView.Listener, LevelUpRewardFXMLController> {

    /**
     * Listener interface for level-up bonus selection events.
     */
    public static interface Listener extends ViewListener {
        /**
         * Called when the player selects a bonus option.
         *
         * @param selectedIndex the index of the selected bonus (0, 1, or 2)
         */
        void onBonusSelected(int selectedIndex);
    }


    /**
     * Creates the level-up bonus view by loading its FXML layout.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public LevelUpRewardView() throws IOException {
        this.loadFXML(constants.PathConstants.FXML_LEVEL_UP_REWARD);
    }

    /**
     * Displays the bonus options for the player to choose from.
     *
     * @param bugemonName the name of the Bugemon that leveled up
     * @param oldLevel the previous level
     * @param newLevel the new level
     * @param bonusOptions list of bonus descriptions (e.g., "+20 PV", "+10 Attaque")
     * @param onBonusSelected callback when a bonus is selected (receives index: 0, 1, or 2)
     */
    public void displayBonusOptions(final String bugemonName, final int oldLevel, final int newLevel,
                                    final List<String> bonusOptions,
                                    final Consumer<Integer> onBonusSelected) {
        this.fxmlController.displayBonusOptions(bugemonName, oldLevel, newLevel, bonusOptions, onBonusSelected);
    }
}

