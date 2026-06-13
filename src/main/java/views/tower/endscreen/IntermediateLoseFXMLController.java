package views.tower.endscreen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * FXML controller for the intermediate lose screen.
 *
 * <p>Handles UI interactions and dynamically builds the Bugemon
 * details list displayed after a combat defeat.</p>
 *
 * @see IntermediateLoseView.Listener
 */
public class IntermediateLoseFXMLController extends CombatEndFXMLController<IntermediateLoseView.Listener> {

    @FXML private Button again;

    /**
     * Initializes the button actions by binding them to the listener.
     * Called automatically by JavaFX after the FXML is loaded.
     */
    @FXML
    @Override
    protected void initialize(){
        super.initialize();
        this.again.setOnAction(event -> {
            this.listener.onAgainClicked();
        });

    }
}
