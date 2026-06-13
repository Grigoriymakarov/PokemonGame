package models.shared.trainer;

import dto.CombatStateDTO;
import models.exceptions.CouldNotDetermineActionException;
import models.exceptions.NoAvailableReplacerException;
import models.shared.bugemon.TrainerBugemon;
import models.team.Team;
import services.combat.actions.Action;

import java.util.Optional;


/**
 * Represents the human player in the game.
 *
 * <p>The {@code Player} is a {@link Trainer} whose actions in battle
 * are chosen by the user through the graphical interface,
 * unlike the {@link Enemy} whose actions are automatic.</p>
 *
 * @see Enemy
 * @see Team
 */
public class Player extends Trainer {

    private Optional<Action> chosenAction = Optional.empty();
    private Optional<TrainerBugemon> chosenReplacerAfterKO = Optional.empty();

    /**
     * Creates a trainer with the given name.
     *
     * @param name the name of the trainer
     */
    public Player(final String name) {
        super(name);
    }


    /**
     * Sets the chosen action for this trainer to perform in the current turn.
     * <p>This method is intended to be called by the graphical interface controller</p>
     * @param chosenAction the action to perform
     */
    public void setChosenAction(final Action chosenAction) {
        this.chosenAction = Optional.of(chosenAction);
    }
    /**
     * Sets the chosen replacement action for this trainer to perform after a knockout.
     * <p>This method is intended to be called by the graphical interface controller</p>
     * @param chosenReplacerAfterKO the switch action to perform after knockout
     */
    public void setChosenReplacerAfterKO(final TrainerBugemon chosenReplacerAfterKO) {
        this.chosenReplacerAfterKO = Optional.of(chosenReplacerAfterKO);
    }

    /**
     * Clears the chosen action for this trainer to perform in the current turn.
     * <p>This method is intended to be called by the graphical interface controller after {@link #chooseAction()}</p>
     */
    public void clearChosenAction() {
        this.chosenAction = Optional.empty();
    }
    /**
     * Clears the chosen action for this trainer to perform in the current turn.
     * <p>This method is intended to be called by the graphical interface controller after {@link #chooseReplacerAfterKO(CombatStateDTO)}</p>
     */
    public void clearChosenReplacerAfterKO() {
        this.chosenReplacerAfterKO = Optional.empty();
    }


    /**
     * Chooses an action for this player based on the current combat state.
     *
     * @return the chosen action to perform
     * @throws CouldNotDetermineActionException if no action has been chosen by the player
     */

    public Action chooseAction() throws CouldNotDetermineActionException {
        return chosenAction.orElseThrow(
                () -> new CouldNotDetermineActionException("No action chosen by the player " + this.name)
        );
    }

    /**
     * Chooses a replacement Bugemon for this player after the current active Bugemon has been knocked out.
     *
     * @param combatState the current state of the combat
     * @return the chosen switch action to perform
     */
    @Override
    public TrainerBugemon chooseReplacerAfterKO(final CombatStateDTO combatState) throws NoAvailableReplacerException {
        return chosenReplacerAfterKO.orElseThrow(
                () -> new NoAvailableReplacerException("No replacement Bugemon chosen by the player" + this.name)
        );
    }
}
