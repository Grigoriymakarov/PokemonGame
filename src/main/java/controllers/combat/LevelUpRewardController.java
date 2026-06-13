package controllers.combat;

import controllers.BaseController;
import controllers.shared.MetaController;
import javafx.stage.Stage;
import views.combat.LevelUpRewardView;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller for the level-up bonus selection screen.
 *
 * <p>This controller manages the display of bonus options when a Bugemon levels up
 * and handles the player's selection via callbacks.</p>
 */
public class LevelUpRewardController extends BaseController<LevelUpRewardView.Listener, LevelUpRewardView> implements LevelUpRewardView.Listener {

    private Consumer<Integer> onBonusSelectedCallback;

    /**
     * Creates a controller for level-up bonus selection.
     *
     * @param metaController the global navigation controller
     * @throws IOException if the view cannot be loaded
     */
    public LevelUpRewardController(final MetaController metaController) throws IOException {
        super(metaController, new LevelUpRewardView());
    }

    /**
     * Displays the bonus selection screen with the given options.
     *
     * @param bugemonName the name of the Bugemon
     * @param oldLevel the previous level
     * @param newLevel the new level
     * @param bonusOptions list of bonus descriptions
     * @param onBonusSelected callback when a bonus is selected (receives index: 0, 1, or 2)
     */
    public void showBonusSelection(final String bugemonName, final int oldLevel, final int newLevel,
                                   final List<String> bonusOptions,
                                   final Consumer<Integer> onBonusSelected) {
        this.onBonusSelectedCallback = onBonusSelected;
        this.view.displayBonusOptions(bugemonName, oldLevel, newLevel, bonusOptions, this::onBonusSelected);
    }

    /**
     * Displays the bonus selection screen.
     *
     * @param stage the primary JavaFX stage
     */
    @Override
    public void showView(final Stage stage) throws FileNotFoundException {
        this.showView(stage, 700, 600);
    }

    /**
     * Called when the player selects a bonus option.
     *
     * @param selectedIndex the index of the selected bonus (0, 1, or 2)
     */
    @Override
    public void onBonusSelected(final int selectedIndex) {
        if (this.onBonusSelectedCallback != null) {
            this.onBonusSelectedCallback.accept(selectedIndex);
        }
    }
}

