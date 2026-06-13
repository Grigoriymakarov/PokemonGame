package views.tower.endscreen;

import java.io.IOException;

/**
 * View for the floor unlock screen, shown after defeating a non-final boss.
 */
public final class FloorUnlockView extends CombatEndView<FloorUnlockView.Listener, FloorUnlockFXMLController> {

    /**
     * Listener interface for the floor unlock screen events.
     *
     * <p>Shown after the player defeats a non-final boss. Extends {@link CombatEndListener}
     * with a "Continuer" action that advances to the first step of the next floor.</p>
     *
     * @see CombatEndListener
     */
    public static interface Listener extends CombatEndListener {

        /**
         * Called when the player clicks "Continuer" to start the newly unlocked floor.
         *
         * <p>The controller should advance the tower to the first step of the next floor.</p>
         */
        void onContinueClicked();
    }


    /**
     * Creates the floor unlock screen view by loading its FXML layout.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public FloorUnlockView() throws IOException {
        this.loadFXML(constants.PathConstants.FXML_FLOOR_UNLOCK);
    }
}
