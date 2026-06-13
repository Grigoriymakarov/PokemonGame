package views.tower.endscreen;

import java.io.IOException;

/**
 * View for the tower win screen, shown after defeating the final boss.
 */
public final class TowerWinView extends CombatEndView<TowerWinView.Listener, TowerWinFXMLController> {

    /**
     * Listener interface for the tower win screen events.
     *
     * <p>Shown after the player defeats the final boss (floor NO9). The only available
     * action is returning to the main menu, which is already defined by {@link CombatEndListener}.</p>
     *
     * @see CombatEndListener
     */
    public static interface Listener extends CombatEndListener {
    }


    /**
     * Creates the tower win screen view by loading its FXML layout.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public TowerWinView() throws IOException {
        this.loadFXML(constants.PathConstants.FXML_TOWER_WIN);
    }
}
