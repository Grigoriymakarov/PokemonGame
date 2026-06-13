package views.tower.endscreen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * FXML controller for the intermediate win screen.
 *
 * <p>Handles UI interactions and dynamically builds the Bugemon
 * details list displayed after a combat defeat.</p>
 *
 * @see IntermediateWinView.Listener
 */
public final class IntermediateWinFXMLController extends CombatEndFXMLController<IntermediateWinView.Listener> {

    @FXML private Button next;

    /**
     * Initializes the button actions by binding them to the listener.
     * Called automatically by JavaFX after the FXML is loaded.
     */
    @FXML
    @Override
    protected void initialize(){
        super.initialize();
        this.next.setOnAction(event -> {
            this.listener.onNextClicked();
        });
    }
}
