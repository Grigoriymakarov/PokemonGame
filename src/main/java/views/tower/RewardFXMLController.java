package views.tower;

import views.BaseFXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import views.BaseView;

/**
 * JavaFX FXML controller for the reward screen.
 *
 * <p>This class handles the graphical components declared in {@code RewardView.fxml}.
 * It binds the "Suivant" button to the {@link RewardView.Listener} so that it is notified when the player clicks it.</p>
 *
 * <p>It also exposes a method to update the floor label displayed at the top.</p>
 */
public class RewardFXMLController extends BaseFXMLController<RewardView.Listener> {

    // The label that shows the current floor name (e.g. "Étage NO3")
    @FXML
    private Label floorLabel;

    // The "Next" button the player clicks to move on
    @FXML
    private Button nextButton;

    /**
     * Called automatically by JavaFX after the FXML file is loaded.
     * Binds the button to the listener.
     *
     * <p>Note: {@code listener} is set by {@link BaseView#setListener(BaseView.ViewListener)}
     * <em>after</em> this initialize() call, so button actions use a lambda
     * that reads {@code listener} at click time — this is always safe.</p>
     */
    @FXML
    private void initialize() {
        this.nextButton.setOnAction(event -> this.listener.onNextClicked());
    }

    /**
     * Updates the floor label text shown at the top of the screen.
     *
     * @param floorName the floor name to display (e.g. "NO3")
     */
    public void setFloorLabel(final String floorName) {
        this.floorLabel.setText("Étage " + floorName);
    }
}