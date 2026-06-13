package views.tower.endscreen;

import dto.TrainerBugemonDTO;
import views.BaseView;


/**
 * The view class for the combat end screen in the tower mode.
 *
 * <p>This class is responsible for displaying the player's team details and floor information at the end of a combat encounter.</p>
 *
 * <p>It provides methods to add Bugemon details to the team display, clear existing details, and manage the floor label visibility and text.</p>
 *
 * <p>The view interacts with a controller that implements {@link CombatEndListener} to handle user actions such as returning to the main menu.</p>
 *
 * @param <L> the type of listener that handles events from this view, must extend {@link CombatEndListener}
 * @param <FC> the type of FXML controller associated with this view, must extend {@link CombatEndFXMLController}
 */
public class CombatEndView<L extends CombatEndView.CombatEndListener, FC extends CombatEndFXMLController<L>> extends BaseView<L, FC> {

    /**
     * The listener interface for handling user interactions on the combat end screen.
     *
     * <p>Controllers that manage the combat end screen must implement this interface (or a sub-interface) to receive events from the view.</p>
     *
     * <p>Currently, it defines a method for handling clicks on the "Main Menu" button, allowing the controller to navigate back to the home screen.</p>
     */
    public static interface CombatEndListener extends ViewListener {
        /**
         * Called when the user clicks to return to the main menu.
         *
         * <p>The controller should navigate back to the home screen or main menu screen
         * via the MetaController.</p>
         */
        void onMainClicked();
    }


    /**
     * Displays the details of a single Bugemon in the team list.
     * @param bugemonDTO the DTO object representing the team member
     * @param showXP whether to include XP information in the display (true for intermediate win screen, false for intermediate lose screen)
     */
    public void addBugemonDetails(TrainerBugemonDTO bugemonDTO, boolean showXP) {
        if  (showXP) {
            this.fxmlController.addToTeamDisplay(bugemonDTO.getDisplayName(), bugemonDTO.getCurrentHP(), bugemonDTO.getMaxHp(), bugemonDTO.getLevel(), bugemonDTO.getXP(), bugemonDTO.getXPToNextLevel());
        } else {
            this.fxmlController.addToTeamDisplay(bugemonDTO.getDisplayName(), bugemonDTO.getCurrentHP(), bugemonDTO.getMaxHp());
        }

    }

    /**
     * Clears all Bugemon entries from the team list.
     * Should be called before repopulating to avoid duplicates.
     */
    public void clearBugemonDetails(){
        this.fxmlController.clearTeam();
    }

    /**
     * Updates the floor label in the UI to show current progress.
     * @param name the text to display (e.g., "Étage 5")
     */
    public void setFloorLabel(final String name) {
        this.fxmlController.setFloorLabel(name); }

    /**
     * Hides the floor label from the UI.
     *
     * <p>Should be called in free combat mode, where no floor number is relevant.
     * The label is both hidden and removed from the layout to avoid empty spacing.</p>
     */
    public void hideFloorLabel() {
        this.fxmlController.hideFloorLabel();
    }
}
