package views.tower.endscreen;

import java.io.IOException;

/**
 * View for the intermediate win screen (displayed between tower floors).
 * <p>
 * Loads the FXML layout and delegates UI updates to {@link IntermediateWinFXMLController}.
 * It shows the current status of the player's team and progress in the tower.
 * </p>
 *
 * @see IntermediateWinFXMLController
 * @see Listener
 */
public final class IntermediateWinView extends CombatEndView<IntermediateWinView.Listener, IntermediateWinFXMLController> {

    /**
     * Listener interface for intermediate tower victory screen events.
     *
     * <p>This interface defines the contract for user interactions on the screen displayed
     * when the player wins in the tower mode at an intermediate level. The concrete
     * implementation is provided by the corresponding controller.</p>
     *
     * <p>The interface extends {@link ViewListener} to integrate with the framework's
     * view/listener architecture.</p>
     *
     * @see ViewListener
     */
    public static interface Listener extends CombatEndListener {
        /**
         * Called when the user clicks to proceed to the next level in the tower.
         *
         * <p>The controller should advance the tower progress and start the next combat
         * encounter with an appropriate opponent.</p>
         */
        void onNextClicked();
    }


    /**
     * Creates the win screen view by loading its FXML layout.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public IntermediateWinView() throws IOException {
        this.loadFXML(constants.PathConstants.FXML_INTERMEDIATE_WIN);
    }
}
