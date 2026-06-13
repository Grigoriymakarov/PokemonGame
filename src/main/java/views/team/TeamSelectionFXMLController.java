package views.team;

import constants.PathConstants;
import constants.UIConstants;
import dto.TeamDTO;
import dto.TrainerBugemonDTO;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.exceptions.BugemonNotFoundException;
import models.shared.bugemon.BugemonSpecie;
import models.shared.statistics.Statistics;
import views.BaseFXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.util.Optional;

/**
 * FXML Controller for the team selection screen.
 * * <p>This controller manages the low-level UI components defined in {@code TeamSelection.fxml},
 * including the grid of available Bugemon and the current team composition bar.</p>
 */
public class TeamSelectionFXMLController extends BaseFXMLController<TeamSelectionView.Listener> {

    /** Grid container for displaying available Bugemon (the Bugedex). */
    @FXML private FlowPane bugedexGrid;

    /** Horizontal bar displaying the current team members. */
    @FXML private HBox teamBar;

    /** Button to confirm the current selection and proceed. */
    @FXML private Button confirmButton;

    /** Button to save the current team configuration. */
    @FXML private Button saveButton;

    /** Button to load a previously saved team. */
    @FXML private Button loadButton;

    /**
     * Initializes the controller after the FXML components have been loaded.
     * Sets up the event handlers for the navigation and action buttons.
     * @throws IllegalStateException if a button is clicked before the listener is attached.
     */
    @FXML
    private void initialize(){
        this.confirmButton.setOnAction(e -> {
            if (this.listener == null) throw new IllegalStateException("Listener non initialisé sur confirmButton");
            this.listener.onConfirmTeam();
        });

        this.saveButton.setOnAction(e -> {
            if (this.listener == null) throw new IllegalStateException("Listener non initialisé sur saveButton");
            this.listener.onSave();
        });

        this.loadButton.setOnAction(e -> {
            if (this.listener == null) throw new IllegalStateException("Listener non initialisé sur loadButton");
            this.listener.onLoad();
        });
    }

    /**
     * Creates a {@link Button}/card representing a {@link BugemonSpecie} in the Bugedex grid.
     *
     * <p>The card displays the Bugemon's sprite and name, and is configured with:
     * <ul>
     *   <li>A {@link Tooltip} showing base stats depending on team membership</li>
     *   <li>A selected style ({@code "card-selected"}) if the Bugemon is already in the team</li>
     *   <li>A click handler that toggles the Bugemon's selection in the team</li>
     * </ul>
     * </p>
     *
     * @param bugemon     the {@link BugemonSpecie} to represent
     * @param currentTeam the player's current {@link TeamDTO}, used to check team membership
     * @return a configured {@link Button} ready to be added to the Bugedex grid
     */
    public Button createBugemonCard(final BugemonSpecie bugemon, final TeamDTO currentTeam) {
        Optional<TrainerBugemonDTO> teamMember;

        try {
            // teamMember is empty if no Bugemon of this specie is found in the current team
            teamMember = Optional.of(currentTeam.getMemberOfSpecie(bugemon));
        } catch (final BugemonNotFoundException e) {
            teamMember = Optional.empty();
        }

        final Button card = new Button(bugemon.getName());

        card.setPrefSize(160, 120);
        card.setGraphic(this.createImageView(bugemon));
        card.setTooltip(TeamSelectionFXMLController.createBugemonTooltip(bugemon, teamMember));

        if (teamMember.isPresent()) card.getStyleClass().add("card-selected");

        card.setOnAction(e -> {
            if (currentTeam.containsSpecie(bugemon)) {
                this.listener.onBugemonDeselected(bugemon);
            } else {
                this.listener.onBugemonSelected(bugemon);
            }
        });

        return card;
    }

    /**
     * Creates a {@link Tooltip} displaying the stats of a given {@link BugemonSpecie}.
     *
     * <p>The tooltip content differs based on whether the Bugemon is part of the current team:
     * <ul>
     *   <li>If not in the team: displays base species stats via {@link #buildSpecieStatsText}</li>
     *   <li>If in the team: displays current statsc via {@link #buildCurrentStatsText}</li>
     * </ul>
     * </p>
     *
     * @param bugemon    the {@link BugemonSpecie} for which the tooltip is created
     * @param teamMember the corresponding {@link TrainerBugemonDTO} if present in the team, or empty
     * @return a configured {@link Tooltip} ready to be attached to a UI component
     */
    private static Tooltip createBugemonTooltip(final BugemonSpecie bugemon, final Optional<TrainerBugemonDTO> teamMember) {
        // Difference is made between a general specie not in team and a team member that can have custom stats
        String text = TeamSelectionFXMLController.buildSpecieStatsText(bugemon);
        if (teamMember.isPresent()) {
            text = TeamSelectionFXMLController.buildCurrentStatsText(teamMember.get());
        }

        return TeamSelectionFXMLController.createTooltip(text);
    }

    private static String buildSpecieStatsText(final BugemonSpecie specie) {
        return String.format(UIConstants.BUGEMON_TOOLTIP_FORMAT,
                specie.getType(),
                UIConstants.BUGEMON_TOOLTIP_BASE,
                specie.getStat(Statistics.Stat.HP),
                specie.getStat(Statistics.Stat.ATTACK),
                specie.getStat(Statistics.Stat.DEFENSE),
                specie.getStat(Statistics.Stat.INITIATIVE)
        );
    }

    private static String buildCurrentStatsText(final TrainerBugemonDTO member) {
        return String.format(UIConstants.BUGEMON_TOOLTIP_FORMAT,
                member.getSpecieType(),
                UIConstants.BUGEMON_TOOLTIP_ACTUAL,
                member.getMaxHp(),
                member.getStat(Statistics.Stat.ATTACK),
                member.getStat(Statistics.Stat.DEFENSE),
                member.getStat(Statistics.Stat.INITIATIVE)
        );
    }

    /**
     * Creates a {@link Button}/slot representing an element in the team  to put it in the team bar.
     * @param member to represent in the team bar
     * @return a configured {@link Button} ready to be added to the team bar via the "addMemberToBar" method in
     * the TeamSelectionFXMLController class
     */
    public Button createTeamMemberSlot(final TrainerBugemonDTO member) {
        final Button slot = new Button(member.getDisplayName() + " (Lv." + member.getLevel() + ")");
        slot.getStyleClass().add("team-slot");
        slot.setTooltip(TeamSelectionFXMLController.createMemberTooltip(member));
        slot.setOnAction(e -> this.listener.onBugemonDeselected(member.getSpecie()));
        return slot;
    }


    private static Tooltip createMemberTooltip(final TrainerBugemonDTO member) {
        final String text = String.format(UIConstants.BUGEMON_TOOLTIP_LEVEL_HEADER, member.getLevel()) + "\n"
                + buildCurrentStatsText(member) + "\n"
                + String.format(UIConstants.BUGEMON_TOOLTIP_XP_FOOTER, member.getXP(), member.getXPToNextLevel());
        return TeamSelectionFXMLController.createTooltip(text);
    }

    private static Tooltip createTooltip(final String text) {
        final Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(javafx.util.Duration.millis(300));
        return tooltip;
    }

    /**
     * Creates an image to isert into the card with a bugemon to select, by accesing to the image path via
     *the name of the bugemon for which the button should be created
     */
    private ImageView createImageView(final BugemonSpecie bugemon) {
        final Image image = new Image(
                this.getClass().getResource(PathConstants.PNG_PATH + bugemon.id() + ".png").toExternalForm()
        );
        final ImageView imageView = new ImageView(image);
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        return imageView;
    }

    /**
     * Adds a Bugemon visual representation (button/card) to the Bugedex grid.
     * @param bugemonCard the UI component representing a Bugemon
     */
    public void addCardToGrid(final Button bugemonCard){
        this.bugedexGrid.getChildren().add(bugemonCard); }

    /**
     * Adds a team member visual representation (button) to the selection bar.
     * @param member the UI component representing a team member
     */
    public void addMemberToBar(final Button member){
        this.teamBar.getChildren().add(member);
    }


    /**
     * Empties both the Bugedex grid and the team bar.
     * This is typically called before re-rendering the view.
     */
    public void clear(){
        this.teamBar.getChildren().clear();
        this.bugedexGrid.getChildren().clear();
    }

    /**
     * Hides the load and confirm buttons from the UI.
     * Useful for specific modes where team modification is allowed but not finalization.
     */
    public void hideLoadAndConfirmButtons() {
        this.loadButton.setVisible(false);
        this.confirmButton.setVisible(false);
    }
}
