package views.tower.endscreen;

import java.io.IOException;
/**
 * View for the intermediate lose screen.
 *
 * <p>Loads the FXML layout and delegates UI updates
 * to {@link IntermediateLoseFXMLController}.</p>
 *
 * @see IntermediateLoseFXMLController
 * @see Listener
 */
public final class IntermediateLoseView extends CombatEndView<IntermediateLoseView.Listener, IntermediateLoseFXMLController> {

    /**
     * Listener interface for intermediate tower loss screen events.
     *
     * <p>This interface defines the contract for user interactions on the screen displayed
     * when the player loses in the tower mode at an intermediate level. The concrete
     * implementation is provided by the corresponding controller.</p>
     *
     * <p>The interface extends {@link ViewListener} to integrate with the framework's
     * view/listener architecture.</p>
     *
     * @see ViewListener
     */
    public static interface Listener extends ViewListener, CombatEndListener {
        /**
         * Called when the user clicks to try again (restart the tower mode).
         *
         * <p>The controller should reset the tower state and restart the combat sequence
         * from the beginning or from the appropriate level.</p>
         */
        void onAgainClicked();
    }


    /**
     * Creates the loss screen view by loading its FXML layout.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public IntermediateLoseView() throws IOException {
        this.loadFXML(constants.PathConstants.FXML_INTERMEDIATE_LOSE);
    }
}
