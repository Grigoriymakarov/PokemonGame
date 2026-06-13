package views.combat;

import dto.AttackWithEffectivenessDTO;
import dto.CombatStateDTO;
import dto.TrainerBugemonDTO;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import views.BaseFXMLController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JavaFX controller associated with the combat FXML view.
 *
 * <p>This class owns the {@code @FXML} fields and wires them to three focused helpers:</p>
 * <ul>
 *   <li>{@link CombatRenderer}     — updates all widgets from a {@link CombatStateDTO};</li>
 *   <li>{@link AttackMenuHandler}  — manages the attack selection panel;</li>
 *   <li>{@link SwitchDialogHandler} — builds and shows switch dialogs.</li>
 * </ul>
 *
 * <p>This class is intentionally thin: it holds no business logic and delegates
 * every specialised concern to the appropriate helper. Its sole responsibilities
 * are field injection, helper wiring, and exposing a clean public API consumed
 * by {@link CombatView}.</p>
 *
 * @see CombatView
 */
public class CombatFXMLController extends BaseFXMLController<CombatView.Listener> {

    @FXML private ImageView playerSprite;
    @FXML private ImageView enemySprite;
    @FXML private Label     playerNameLabel;
    @FXML private Label     enemyNameLabel;
    @FXML private ImageView playerTypeIcon;
    @FXML private ImageView enemyTypeIcon;
    @FXML private ProgressBar playerHpBar;
    @FXML private ProgressBar enemyHpBar;
    @FXML private Label     playerHpLabel;
    @FXML private Label     enemyHpLabel;
    @FXML private ListView<String> battleLogList;
    @FXML private Label     floorNameLabel;
    @FXML private Label     stepNameLabel;

    @FXML private Button    attackButton;
    @FXML private Button    forfeitButton;
    @FXML private Button    switchButton;
    @FXML private Button    itemButton;

    @FXML private StackPane attack1Button;
    @FXML private StackPane attack2Button;
    @FXML private StackPane attack3Button;
    @FXML private Label     attack1NameLabel;
    @FXML private Label     attack1EffectivenessLabel;
    @FXML private Label     attack2NameLabel;
    @FXML private Label     attack2EffectivenessLabel;
    @FXML private Label     attack3NameLabel;
    @FXML private Label     attack3EffectivenessLabel;
    @FXML private Button    backButton;

    /** Renders {@link CombatStateDTO} data onto the combat screen widgets. */
    private CombatRenderer      stateRenderer;

    /** Manages the attack selection panel and its back-navigation. */
    private AttackMenuHandler   attackMenuHandler;

    /** Builds and shows voluntary and forced Bugémon switch dialogs. */
    private SwitchDialogHandler switchDialogHandler;

    /**
     * Called by the {@link javafx.fxml.FXMLLoader} after all {@code @FXML} fields
     * have been injected.
     *
     * <p>Responsibilities:</p>
     * <ul>
     *   <li>instantiates and wires the three helper objects;</li>
     *   <li>hides the attack button panel initially;</li>
     *   <li>binds the main action buttons ({@code Attack}, {@code Forfeit},
     *       {@code Switch}) to their respective listener callbacks.</li>
     * </ul>
     */
    @FXML
    private void initialize() {
        final List<Node> mainButtons   = new ArrayList<>(List.of(this.attackButton, this.forfeitButton, this.switchButton));
        final List<Node> attackButtons = new ArrayList<>(List.of(this.attack1Button, this.attack2Button, this.attack3Button, this.backButton));

        this.stateRenderer = new CombatRenderer(
                this.playerSprite, this.enemySprite,
                this.playerNameLabel, this.enemyNameLabel,
                this.playerTypeIcon, this.enemyTypeIcon,
                this.playerHpBar, this.enemyHpBar,
                this.playerHpLabel, this.enemyHpLabel,
                this.battleLogList,
                this.floorNameLabel, this.stepNameLabel
        );

        this.attackMenuHandler = new AttackMenuHandler(
                mainButtons, attackButtons,
                this.attack1Button, this.attack2Button, this.attack3Button,
                this.attack1NameLabel, this.attack1EffectivenessLabel,
                this.attack2NameLabel, this.attack2EffectivenessLabel,
                this.attack3NameLabel, this.attack3EffectivenessLabel,
                this.backButton, this.itemButton
        );

        this.switchDialogHandler = new SwitchDialogHandler();

        attackButtons.forEach(b -> b.setVisible(false));

        this.attackButton.setOnAction(e -> this.listener.onAttackClicked());
        this.forfeitButton.setOnAction(e -> this.listener.onForfeitClicked());
        this.switchButton.setOnAction(e -> this.switchDialogHandler.openSwitchDialog());
    }

    /**
     * Sets the {@link CombatView.Listener} on this controller and propagates it to
     * {@link AttackMenuHandler} and {@link SwitchDialogHandler}.
     *
     * @param listener the listener to notify of combat UI events;
     *                 must not be {@code null}
     */
    @Override
    public void setListener(final CombatView.Listener listener) {
        super.setListener(listener);
        this.attackMenuHandler.setListener(listener);
        this.switchDialogHandler.setListener(listener);
    }

    /**
     * Updates all combat screen widgets to reflect the given state.
     *
     * <p>Delegates to {@link CombatRenderer#render(CombatStateDTO)}.</p>
     *
     * @param state the combat state to render; if {@code null}, no widget is modified
     */
    public void render(final CombatStateDTO state) {
        this.stateRenderer.render(state);
    }

    /**
     * Switches the UI to the attack selection panel and populates it with the
     * given attacks.
     *
     * <p>Delegates to {@link AttackMenuHandler#showAttackMenu(List)}.</p>
     *
     * @param attacks list of attacks to display, each carrying its computed
     *                type-effectiveness; must not be {@code null}
     */
    public void showAttackMenu(final List<AttackWithEffectivenessDTO> attacks) {
        this.attackMenuHandler.showAttackMenu(attacks);
    }

    /**
     * Restores the main action button panel (Attack / Forfeit / Switch),
     * hiding the attack selection panel.
     *
     * <p>Delegates to {@link AttackMenuHandler#showMainMenu()}.</p>
     */
    public void showMainMenu() {
        this.attackMenuHandler.showMainMenu();
    }

    /**
     * Stores the switch candidates and their HP data for the next voluntary
     * switch dialog.
     *
     * <p>Delegates to
     * {@link SwitchDialogHandler#setSwitchCandidatesWithHp(List, Map)}.</p>
     *
     * @param candidates list of Bugémon available for switching;
     *                   {@code null} is treated as an empty list
     * @param hpData     map associating each candidate with an {@code int[2]}
     *                   array of the form {@code {currentHp, maxHp}};
     *                   {@code null} is treated as an empty map
     */
    public void setSwitchCandidatesWithHp(
            final List<? extends TrainerBugemonDTO> candidates, final Map<? extends TrainerBugemonDTO, int[]> hpData
    ) {
        this.switchDialogHandler.setSwitchCandidatesWithHp(candidates, hpData);
    }

    /**
     * Opens the forced switch dialog after the active Bugémon has been knocked out.
     *
     * <p>Delegates to {@link SwitchDialogHandler#openForcedSwitchDialog(List)}.</p>
     *
     * @param candidates alive Bugémon that can replace the knocked-out one;
     *                   if {@code null} or empty, an information alert is shown
     *                   and no event is fired
     */
    public void openForcedSwitchDialog(final List<? extends TrainerBugemonDTO> candidates) {
        this.switchDialogHandler.openForcedSwitchDialog(candidates);
    }
}