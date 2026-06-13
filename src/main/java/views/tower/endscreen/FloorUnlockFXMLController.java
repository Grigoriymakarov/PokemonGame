package views.tower.endscreen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * FXML controller for the floor unlock screen.
 *
 * <p>Displayed after the player defeats a non-final boss. Extends
 * {@link CombatEndFXMLController} with a "Continuer" button that fires
 * {@link FloorUnlockView.Listener#onContinueClicked()} to advance to the next floor.</p>
 *
 * @see FloorUnlockView.Listener
 */
public final class FloorUnlockFXMLController extends CombatEndFXMLController<FloorUnlockView.Listener> {

    @FXML private Button continueButton;

    /**
     * Binds the "Continuer" button in addition to the inherited "Menu Principal" button.
     * Called automatically by JavaFX after the FXML is loaded.
     */
    @FXML
    @Override
    protected void initialize() {
        super.initialize();
        this.continueButton.setOnAction(event -> this.listener.onContinueClicked());
    }
}
