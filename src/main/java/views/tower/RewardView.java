package views.tower;

import controllers.tower.RewardController;
import views.BaseView;

import java.io.IOException;

/**
 * View for the reward screen shown after certain combats in the tower.
 *
 * <p>This class loads the FXML layout and delegates UI updates
 * to {@link RewardFXMLController}.</p>
 *
 * <p>For now, the screen only shows a floor label and a "Next" button.
 * The actual reward logic (picking a bonus, etc.) will be added in Story 11.</p>
 *
 * @see RewardFXMLController
 * @see Listener
 */
public class RewardView extends BaseView<RewardView.Listener, RewardFXMLController> {

    /**
     * Interface that defines the events the reward screen can trigger.
     *
     * <p>The {@link RewardController} implements this interface to receive
     * those events and react accordingly (e.g. navigate to the next step).</p>
     *
     * <p>For now, only one action is needed: clicking "Next" to move on.</p>
     */
    public static interface Listener extends ViewListener {

        /**
         * Called when the player clicks the "Next" button.
         * The controller will then ask MetaController to advance to the next tower step.
         */
        void onNextClicked();
    }


    /**
     * Creates the reward screen view by loading its FXML layout.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public RewardView() throws IOException {
        this.loadFXML(constants.PathConstants.FXML_REWARD);
    }

    /**
     * Updates the floor label shown at the top of the screen.
     *
     * <p>Called by {@link RewardController} right after construction,
     * before the screen is displayed.</p>
     *
     * @param floorName the floor name to display (e.g. "NO3")
     */
    public void setFloorLabel(final String floorName) {
        this.fxmlController.setFloorLabel(floorName);
    }
}