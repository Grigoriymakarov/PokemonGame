package views.combat;

import constants.UIConstants;
import dto.AttackWithEffectivenessDTO;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import views.TypeTheme;

import java.util.Collection;
import java.util.List;

/**
 * Handles the display and interaction of the attack selection menu.
 *
 * <p>Manages toggling between the main action button panel and the attack slots,
 * populating each slot with attack data, and wiring click callbacks to the
 * {@link CombatView.Listener}.</p>
 *
 * <p>Up to three attack slots are supported. If fewer attacks are provided,
 * the remaining slots are rendered as disabled empty entries.</p>
 *
 * <p><b>Must be used on the JavaFX Application Thread.</b></p>
 */
final class AttackMenuHandler {

    /** Main action buttons (Attack / Forfeit / Switch), hidden while the attack menu is open. */
    private final Collection<Node> mainButtons;

    /** Attack slot nodes and back button, hidden while the main menu is shown. */
    private final Collection<Node> attackButtons;

    /** First attack slot container. */
    private final StackPane attack1Button;

    /** Second attack slot container. */
    private final StackPane attack2Button;

    /** Third attack slot container. */
    private final StackPane attack3Button;

    /** Name label for the first attack slot. */
    private final Label attack1NameLabel;

    /** Type-effectiveness label for the first attack slot. */
    private final Label attack1EffectivenessLabel;

    /** Name label for the second attack slot. */
    private final Label attack2NameLabel;

    /** Type-effectiveness label for the second attack slot. */
    private final Label attack2EffectivenessLabel;

    /** Name label for the third attack slot. */
    private final Label attack3NameLabel;

    /** Type-effectiveness label for the third attack slot. */
    private final Label attack3EffectivenessLabel;

    /** Back button that returns to the main action panel. */
    private final Button backButton;

    /** Item button wired to {@link CombatView.Listener#onItemClicked()} once a listener is set. */
    private final Button itemButton;

    /** Listener notified of attack choices, item clicks, and menu navigation. */
    private CombatView.Listener listener;

    /**
     * Creates an {@code AttackMenuHandler} bound to the given UI nodes.
     *
     * <p>All parameters are stored by reference. The {@code itemButton} action
     * handler is not set until {@link #setListener(CombatView.Listener)} is called.</p>
     *
     * @param mainButtons              nodes that form the main action panel
     * @param attackButtons            nodes that form the attack selection panel
     *                                 (slots + back button)
     * @param attack1Button            first attack slot container
     * @param attack2Button            second attack slot container
     * @param attack3Button            third attack slot container
     * @param attack1NameLabel         name label for the first slot
     * @param attack1EffectivenessLabel effectiveness label for the first slot
     * @param attack2NameLabel         name label for the second slot
     * @param attack2EffectivenessLabel effectiveness label for the second slot
     * @param attack3NameLabel         name label for the third slot
     * @param attack3EffectivenessLabel effectiveness label for the third slot
     * @param backButton               button that navigates back to the main panel
     * @param itemButton               button that triggers the item selection flow
     */
    AttackMenuHandler(
            final Collection<Node> mainButtons,
            final Collection<Node> attackButtons,
            final StackPane attack1Button,
            final StackPane attack2Button,
            final StackPane attack3Button,
            final Label attack1NameLabel,
            final Label attack1EffectivenessLabel,
            final Label attack2NameLabel,
            final Label attack2EffectivenessLabel,
            final Label attack3NameLabel,
            final Label attack3EffectivenessLabel,
            final Button backButton,
            final Button itemButton
    ) {
        this.mainButtons               = mainButtons;
        this.attackButtons             = attackButtons;
        this.attack1Button             = attack1Button;
        this.attack2Button             = attack2Button;
        this.attack3Button             = attack3Button;
        this.attack1NameLabel          = attack1NameLabel;
        this.attack1EffectivenessLabel = attack1EffectivenessLabel;
        this.attack2NameLabel          = attack2NameLabel;
        this.attack2EffectivenessLabel = attack2EffectivenessLabel;
        this.attack3NameLabel          = attack3NameLabel;
        this.attack3EffectivenessLabel = attack3EffectivenessLabel;
        this.backButton                = backButton;
        this.itemButton                = itemButton;
    }

    /**
     * Sets the listener that will receive combat UI events and wires the item
     * button action handler.
     *
     * @param listener the {@link CombatView.Listener} to notify; must not be {@code null}
     */
    void setListener(final CombatView.Listener listener) {
        this.listener = listener;
        this.itemButton.setOnAction(e -> this.listener.onItemClicked());
    }

    /**
     * Switches the UI to the attack selection panel and populates the slots with
     * the given attacks.
     *
     * <p>Each of the (up to) three slots is either populated via
     * {@link #renderAttackSlot} or cleared via {@link #renderEmptySlot},
     * depending on whether a corresponding attack exists in the list.
     * The back button is also configured to return to the main panel.</p>
     *
     * @param attacks available attacks with their computed type-effectiveness;
     *                must not be {@code null}; only the first three entries are used
     */
    void showAttackMenu(final List<AttackWithEffectivenessDTO> attacks) {
        final StackPane[] slots    = {this.attack1Button, this.attack2Button, this.attack3Button};
        final Label[] nameLabels   = {this.attack1NameLabel, this.attack2NameLabel, this.attack3NameLabel};
        final Label[] effectLabels = {this.attack1EffectivenessLabel, this.attack2EffectivenessLabel, this.attack3EffectivenessLabel};

        for (int i = 0; i < slots.length; i++) {
            if (i < attacks.size()) {
                this.renderAttackSlot(slots[i], nameLabels[i], effectLabels[i], attacks.get(i));
            } else {
                this.renderEmptySlot(slots[i], nameLabels[i], effectLabels[i]);
            }
        }

        this.configureBackButton();
        this.setGroupVisible(this.mainButtons, false);
        this.setGroupVisible(this.attackButtons, true);
    }

    /**
     * Switches the UI back to the main action panel, hiding the attack slots.
     */
    void showMainMenu() {
        this.setGroupVisible(this.attackButtons, false);
        this.setGroupVisible(this.mainButtons, true);
    }

    /**
     * Populates an attack slot with the given attack data and enables it.
     *
     * <p>The slot background colour is set to the type colour returned by
     * {@link TypeTheme#hexColor()}. A mouse-click handler is wired to notify
     * {@link CombatView.Listener#onAttackChoice(String)} with the attack name.</p>
     *
     * @param slot               the slot container to style and enable
     * @param nameLabel          label to display the attack name
     * @param effectivenessLabel label to display the type-effectiveness hint
     * @param attack             attack data used to populate the slot
     */
    private void renderAttackSlot(
            final StackPane slot,
            final Label nameLabel,
            final Label effectivenessLabel,
            final AttackWithEffectivenessDTO attack
    ) {
        final String effectivenessText = getEffectivenessText(attack.getEffectiveness());
        final String buttonStyle = "-fx-background-color:" + TypeTheme.of(attack.getType()).hexColor()
                + "; -fx-background-radius: 8; -fx-cursor: hand;";

        nameLabel.setText(attack.getName());
        setEffectivenessLabel(effectivenessLabel, effectivenessText);
        slot.setStyle(buttonStyle);
        slot.setDisable(false);
        slot.setVisible(true);
        slot.setOnMouseClicked(e -> this.listener.onAttackChoice(attack.getName()));
    }

    /**
     * Resets an attack slot to its empty, disabled state.
     *
     * <p>The name label is set to {@code "-"}, the effectiveness label is hidden,
     * and the slot is marked as disabled with no click handler.</p>
     *
     * @param slot               the slot container to reset
     * @param nameLabel          label to clear
     * @param effectivenessLabel label to hide
     */
    private void renderEmptySlot(
            final StackPane slot,
            final Label nameLabel,
            final Label effectivenessLabel
    ) {
        nameLabel.setText("-");
        setEffectivenessLabel(effectivenessLabel, "");
        slot.getStyleClass().add("attack-slot-empty");
        slot.setDisable(true);
        slot.setVisible(true);
        slot.setOnMouseClicked(null);
    }

    /**
     * Applies the back-button style class and wires its action to {@link #showMainMenu()}.
     */
    private void configureBackButton() {
        this.backButton.getStyleClass().add("btn-back");
        this.backButton.setOnAction(e -> this.showMainMenu());
    }

    /**
     * Updates a type-effectiveness label, hiding it entirely when the text is empty.
     *
     * <p>Both {@link Label#setVisible(boolean)} and {@link Label#setManaged(boolean)}
     * are set to {@code false} when the text is empty, ensuring the label takes up
     * no space in the layout.</p>
     *
     * @param label the label to update
     * @param text  the text to display; pass an empty string to hide the label
     */
    private static void setEffectivenessLabel(final Label label, final String text) {
        label.setText(text);
        final boolean visible = !text.isEmpty();
        label.setVisible(visible);
        label.setManaged(visible);
    }

    /**
     * Returns the UI hint string corresponding to the given type-effectiveness value.
     *
     * <p>The returned strings are sourced from {@link UIConstants}:</p>
     * <ul>
     *   <li>{@code SUPER_EFFECTIVE}    → {@link UIConstants#HINT_SUPER_EFFECTIVE}</li>
     *   <li>{@code NOT_VERY_EFFECTIVE} → {@link UIConstants#HINT_NOT_VERY_EFFECTIVE}</li>
     *   <li>{@code NEUTRAL}            → {@link UIConstants#HINT_NEUTRAL}</li>
     * </ul>
     *
     * @param effectiveness the effectiveness value to convert
     * @return the corresponding display string; never {@code null}
     */
    private static String getEffectivenessText(final models.shared.TypeEffectiveness effectiveness) {
        return switch (effectiveness) {
            case SUPER_EFFECTIVE    -> UIConstants.HINT_SUPER_EFFECTIVE;
            case NOT_VERY_EFFECTIVE -> UIConstants.HINT_NOT_VERY_EFFECTIVE;
            case NEUTRAL            -> UIConstants.HINT_NEUTRAL;
        };
    }

    /**
     * Sets the visibility of all nodes in the given group to the specified value.
     *
     * @param group   the nodes to update
     * @param visible {@code true} to show the nodes, {@code false} to hide them
     */
    private static void setGroupVisible(final Iterable<Node> group, final boolean visible) {
        group.forEach(b -> b.setVisible(visible));
    }
}