package models.shared.trainer;

import dto.CombatStateDTO;
import dto.TeamDTO;
import models.exceptions.CouldNotDetermineActionException;
import models.exceptions.NoActiveBugemonException;
import models.exceptions.NoAvailableReplacerException;
import models.shared.Attack;
import models.shared.Inventory;
import models.shared.Item;
import models.shared.Type;
import models.shared.bugemon.TrainerBugemon;
import models.exceptions.ItemNotAvailableException;
import models.shared.statistics.Statistics;
import models.team.Team;
import services.combat.actions.Action;

import java.util.List;
import java.util.Optional;

/**
 * Abstract class representing a participant in a battle.
 *
 * <p>A {@code Trainer} is an entity that has a name and can participate
 * in a battle as a trainer. Two concrete subclasses exist:</p>
 * <ul>
 *   <li>{@link Player}: represents the human player</li>
 *   <li>{@link Enemy}: represents the computer-controlled opponent</li>
 * </ul>
 */
public abstract class Trainer {

    /** Name of the participant (player or enemy). */
    protected final String name;

    protected Optional<TrainerBugemon> activeBugemon;

    protected final Inventory inventory = new Inventory();

    protected Team team;

    /**
     * Creates a participant with the given name.
     *
     * @param name the name of the participant
     */
    public Trainer(final String name) {
        this.name = name;
    }

    /**
     * Returns the name of the participant.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the currently active Bugemon for this trainer.
     *
     * @return the active Bugemon
     * @throws NoActiveBugemonException if no Bugemon is active
     */
    public TrainerBugemon getActiveBugemon() throws NoActiveBugemonException {
        return this.activeBugemon.orElseThrow(() -> new NoActiveBugemonException("No active Bugemon found for trainer: " + this.name));
    }

    /**
     * Returns current attacks for the currently active Bugemon for thsi trainer.
     */
    public List<Attack> getActiveBugemonAttacks() {
        return this.getActiveBugemon().getCurrentAttacks();
    }

    /**
     * Sets the active Bugemon for this trainer.
     *
     * @param newActiveBugemon the Bugemon to set as active
     */
    public void setActiveBugemon(final TrainerBugemon newActiveBugemon) {
        this.activeBugemon = Optional.of(newActiveBugemon);;
    }


    /**
     * Sets the team for this trainer and sets the first member as the active Bugemon.
     *
     * <p>If the team is empty, no active Bugemon is set.</p>
     *
     * @param team the team to assign to this trainer
     */
    public void setTeam(final Team team) {
        this.team = team;
        if (!team.isEmpty()) {
            this.setActiveBugemon(team.getMember(0));
        }
    }


    /**
     * Returns the inventory of this trainer containing all items.
     *
     * @return the trainer's inventory
     */
    public Inventory getInventory() {
        return this.inventory;

    }

    /** Returns a list of the alive Bugemon in the trainer's team.
     *
     * @return a list of alive Bugemon members
     */
    public List<TrainerBugemon> getAliveTeam() {
        return this.team.getAliveMembers();
    }

    /**
     * Returns the trainer's team as a list of all members.
     *
     * @return an unmodifiable list of all team members
     */
    public List<TrainerBugemon> getTeamAsList(){
        return this.team.getMembers();
    }

    /**
     * Returns the trainer's team as a DTO (Data Transfer Object).
     *
     * <p>The returned DTO provides a read-only interface to team data.</p>
     *
     * @return the team as a DTO interface
     */
    public TeamDTO getTeamDTO() {
        return this.team;
    }


    /**
     * Returns the elemental type of this trainer's currently active Bugemon.
     *
     * @return the type of the active Bugemon
     * @throws NoActiveBugemonException if no Bugemon is currently active
     */
    public Type getActiveBugemonType() {
        return this.getActiveBugemon().getSpecieType();
    }

    /** Consumes the specified item from the trainer's inventory, applying its effects.
     *
     * <p>This method removes the item from the inventory and applies its effects to the active Bugemon
     * or the trainer as appropriate. It should be called when an item is used during combat.</p>
     *
     * @param item the item to consume
     * @throws models.exceptions.ItemNotAvailableException if the item is not present in the inventory
     */
    public void consumeItemFromInventory(final Item item) throws ItemNotAvailableException {
        try {
            this.inventory.consumeItem(item);
        } catch (final ItemNotAvailableException e) {
            throw new ItemNotAvailableException("Cannot consume item: " + item.name() + " is not available in the inventory of the trainer" + this.name, e, item, this.inventory);
        }
    }

    /** Returns the number of Bugemon in the trainer's team.
     *
     * @return the size of the trainer's team
     */
    public int getTeamSize() {
        return this.team.size();
    }

    public int getActiveBugemonInitiative() {
        return this.getActiveBugemon().getStat(Statistics.Stat.INITIATIVE);
    }

    /** Clears all items from the trainer's inventory.
     *
     * <p>This method removes all items from the inventory, leaving it empty. It can be used to reset
     * the inventory state, for example at the start of a new battle or after using certain effects.</p>
     */
    public void clearInventory() {
        this.inventory.clear();
    }

    /** Adds a specified quantity of an item to the trainer's inventory.
     *
     * <p>This method increases the quantity of the given item in the inventory by the specified amount.
     * If the item is not already present, it will be added with the given quantity.</p>
     *
     * @param item the item to add
     * @param quantity the quantity to add
     */
    public void addItemToInventory(Item item, Integer quantity) {
        this.inventory.addItem(item, quantity);
    }


    /** Chooses a replacement Bugemon for this trainer after the current active Bugemon has been knocked out.
     *
     * <p>This method is abstract and must be implemented by concrete subclasses of {@code Trainer}.
     * It should contain the logic for selecting a new active Bugemon from the trainer's team after a knockout,
     * which may involve analyzing the combat state, the remaining alive Bugemon, and other factors.</p>
     *
     * @param combatState the current state of the combat
     * @return the chosen switch action to perform
     */
    public abstract TrainerBugemon chooseReplacerAfterKO(final CombatStateDTO combatState) throws NoAvailableReplacerException;
}
