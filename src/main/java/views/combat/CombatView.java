package views.combat;

import dto.AttackWithEffectivenessDTO;
import dto.CombatStateDTO;
import dto.TrainerBugemonDTO;
import views.BaseView;

import constants.LabelConstants;
import constants.PathConstants;

import java.io.IOException;
import java.util.List;

/**
 * JavaFX implementation of the {@link CombatView} interface.
 *
 * <p>This class loads the combat FXML file, gets its controller
 * ({@link CombatFXMLController}), and forwards rendering and user actions to it.</p>
 *
 *<p>It also shows dialog windows for messages and the final combat result.</p>
 *
 * @see CombatFXMLController
 * @see CombatStateDTO
 */
public class CombatView extends BaseView<CombatView.Listener, CombatFXMLController> {

    /**
     * Listener interface for combat view events.
     *
     * <p>This interface defines the methods that the combat view will call
     * when the user interacts with the combat interface. The combat controller
     * implements this interface to handle these events and update the combat state accordingly.</p>
     *
     * <p>The listener follows the callback pattern to maintain loose coupling between
     * the view layer and the business logic. All user actions (attack selection, switch,
     * forfeit) are routed through this interface.</p>
     */
    public static interface Listener extends ViewListener {

        /**
         * Called when the player clicks the "Attack" button.
         *
         * <p>This method triggers the display of the attack selection dialog,
         * allowing the player to choose which attack their active Bugemon will use.
         * The method should check if the player is in a valid state (not waiting for
         * a forced switch) before proceeding.</p>
         *
         */
        void onAttackClicked();

        /**
         * Called when the player clicks the "Forfeit" button.
         *
         * <p>This method handles the player's decision to surrender the combat.
         * The forfeit action will end the combat immediately with a loss for the player.
         * This method should verify that the player is not in an invalid state
         * (e.g., waiting for a forced switch) before processing.</p>
         */
        void onForfeitClicked();
        /**
         * Called when the player selects an item to use from their inventory.
         *
         * <p>This method handles item selection during combat. The selected item
         * will be used on the player's active Bugemon. This method should validate
         * that the player is not waiting for a forced switch before proceeding.</p>
         */
        void onItemClicked();

        /**
         * Updates the combat view with the current state.
         *
         * <p>This method is called after each action to refresh the display.
         * It processes any pending turns, handles state transitions (KO detection,
         * switch requirements), and updates the view accordingly. It is responsible for:</p>
         * <ul>
         *     <li>Processing the turn if an action was selected</li>
         *     <li>Detecting knock-out conditions and triggering switch dialogs</li>
         *     <li>Updating HP bars, messages, and sprites</li>
         *     <li>Navigating to win/lose screens when combat ends</li>
         * </ul>
         */
        void updateView();

        /**
         * Called when the player selects a Bugemon to switch to.
         *
         * <p>This method is invoked when the player either manually switches their
         * active Bugemon using the "Switch" button or is forced to switch due to
         * their current Bugemon being knocked out. The method finds the Bugemon
         * by name, validates it is alive, and updates the combat state accordingly.</p>
         *
         * @param bugemonName the name of the Bugemon to switch to. Must be the name
         *                    of an alive Bugemon in the player's team.
         *
         * @throws IllegalArgumentException if the Bugemon name does not match any
         *                                  alive Bugemon in the player's team
         *
         * @see #updateView()
         */
        void onSwitchClicked(String bugemonName);

        /**
         * Called when the player selects an attack to perform.
         *
         * <p>This method sets the player's action for the current turn.
         * After both the player and opponent have selected their actions,
         * the turn is executed by calling {@link #updateView()}.</p>
         *
         * @param attackName the name of the attack to perform. Must be one of the
         *                   available attacks for the player's active Bugemon
         */

        void onAttackChoice(String attackName);
    }


    /**Creates the JavaFX combat view by loading its FXML layout.
     * @throws RuntimeException if the FXML file cannot be loaded*/
    public CombatView() throws IOException {
        this.loadFXML(PathConstants.FXML_COMBAT);
    }

    /**Displays the current combat state in the JavaFX interface.
     @param state immutable combat state to render*/
    public void showCombatState(final CombatStateDTO state) {
        this.fxmlController.render(state);
    }

    /**Displays an informational dialog to the user.
     @param message message to display*/
    public void showMessage(final String message) {
        this.showInfoMessage(LabelConstants.TITLE_COMBAT, null, message);
    }

    /**Displays the attack selection menu with available attacks.
     @param availableAttacks list of attacks that can be performed, with their effectiveness*/
    public void showAttackMenu(final List<AttackWithEffectivenessDTO> availableAttacks) {
        this.fxmlController.showAttackMenu(availableAttacks);
    }

    /**Displays the main combat menu with action buttons.*/
    public void showMainMenu() {this.fxmlController.showMainMenu();}

    /**Sets the list of Bugemon candidates available for switching.
     @param candidates list of Bugemon that can be switched to
     @param hpData a map associating each Bugemon with its HP values (e.g. current HP and maximum HP)
     */
    public void setSwitchCandidatesWithHp(final List<? extends TrainerBugemonDTO> candidates, final java.util.Map<? extends TrainerBugemonDTO, int[]> hpData) {
        this.fxmlController.setSwitchCandidatesWithHp(candidates, hpData);
    }
    /**Displays a forced switch dialog when the active Bugemon is knocked out.
     @param candidates list of alive Bugemon that can replace the knocked-out one*/
    public void showForcedSwitchDialog(final List<? extends TrainerBugemonDTO> candidates) {
        this.fxmlController.openForcedSwitchDialog(candidates);
    }

    /**Displays an error message for invalid inventory selection.
     @param message error message to display*/
    public void showInventoryError(final String message){
        this.showErrorMessage(message);
    }
}
