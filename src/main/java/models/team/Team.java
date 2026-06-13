package models.team;


import dto.TeamDTO;
import models.exceptions.BugemonNotFoundException;
import models.shared.bugemon.BugemonSpecie;
import models.shared.bugemon.TrainerBugemon;
import services.TeamService;
import constants.LogicConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a team of Bugemons assembled by a trainer.
 *
 * <p>A team can contain between {@value constants.LogicConstants#TEAM_MIN_SIZE} and {@value constants.LogicConstants#TEAM_MAX_SIZE} Bugemons.
 * The following rules apply:</p>
 * <ul>
 *   <li>The same Bugemon (same specie) cannot appear twice</li>
 *   <li>The team cannot exceed {@value constants.LogicConstants#TEAM_MAX_SIZE} members</li>
 *   <li>A team is considered valid if it contains at least {@value constants.LogicConstants#TEAM_MIN_SIZE} members</li>
 * </ul>
 *
 * <p>This class encapsulates all team validation logic.
 * It is managed via {@link TeamService}.</p>
 *
 * @see TeamService
 * @see TeamFactory
 */
public class Team implements TeamDTO {

    /** Internal list of team members. */
    private final List<TrainerBugemon> members = new ArrayList<>();

    /**
     * Checks whether a Bugemon can be added to the team.
     *
     * @param bugemon the Bugemon to check
     * @return {@code true} if the team is not full and does not already contain the specie
     */
    public boolean canAdd(final TrainerBugemon bugemon) {
        return !this.isFull() && !this.containsSpecie(bugemon.getSpecie());
    }

    /**
     * Adds a Bugemon to the team.
     *
     * <p>This is a command: it mutates the team members list and does not return a value.</p>
     *
     * @param bugemon the Bugemon to add (must not be {@code null})
     * @throws IllegalArgumentException if the Bugemon is {@code null} or if a Bugemon of the same specie is already in the team
     * @throws IllegalStateException if the team is already full
     */
    public void add(final TrainerBugemon bugemon) {
        if (bugemon == null) {
            throw new IllegalArgumentException("Bugemon cannot be null");
        }
        if (this.isFull()) {
            throw new IllegalStateException("Team is full");
        }
        if (this.containsSpecie(bugemon.getSpecie())) {
            throw new IllegalArgumentException("Bugemon already present in team");
        }

        this.members.add(bugemon);
    }

    /**
     * Checks whether a Bugemon with the given specie can be removed from the team.
     *
     * @param specie the specie to check
     * @return {@code true} if a Bugemon with this specie is present in the team
     */
    public boolean canRemove(final BugemonSpecie specie) {
        return this.containsSpecie(specie);
    }

    /**
     * Removes a Bugemon from the team by its specie.
     *
     * @param specie the specie of the Bugemon to remove (must not be {@code null})
     */
    public void removeBySpecie(final BugemonSpecie specie) {
        this.members.removeIf(bugemon -> bugemon.isSpecie(specie));
    }

    /**
     * Checks if a Bugemon with the given specie is in the team.
     *
     * @param specie the specie to check
     * @return {@code true} if a Bugemon with this specie is in the team
     */
    public boolean containsSpecie(final BugemonSpecie specie) {
        for (final TrainerBugemon bugemon : this.members) {
            if (bugemon.isSpecie(specie)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the team has reached its maximum capacity.
     *
     * @return {@code true} if the team contains {@value constants.LogicConstants#TEAM_MAX_SIZE} Bugemons
     */
    public boolean isFull() {
        return this.members.size() >= LogicConstants.TEAM_MAX_SIZE;
    }

    /**
     * Returns whether the team has no members.
     *
     * @return {@code true} if the team is empty
     */
    public boolean isEmpty() {
        return this.members.isEmpty();
    }

    /**
     * Returns whether the team is valid for battle.
     *
     * <p>A team is valid if it contains at least {@value constants.LogicConstants#TEAM_MIN_SIZE} Bugemon.</p>
     *
     * @return {@code true} if the team contains at least one Bugemon
     */
    public boolean isValid() {
        return this.members.size() >= LogicConstants.TEAM_MIN_SIZE;
    }

    /**
     * Returns the current number of members in the team.
     *
     * @return the number of Bugemons in the team
     */
    public int size() {
        return this.members.size();
    }

    /**
     * Returns a copy of the team members list.
     *
     * @return a new list containing the current team members
     */
    public List<TrainerBugemon> getMembers() {
        return Collections.unmodifiableList(this.members);
    }

    /**
     * Removes all members from the team.
     */
    public void clear() {
        this.members.clear();
    }

    /** Returns the team member at the specified index.
     *
     * @param i the index of the member to retrieve (0-based)
     * @return the TrainerBugemon at index {@code i}
     * @throws IndexOutOfBoundsException if {@code i} is out of range (i < 0 || i >= size())
     */
    public TrainerBugemon getMember(final int i) {
        return this.members.get(i);
    }

    /**
     * Returns the team member of a specific Bugemon specie.
     *
     * @param specie the specie of the Bugemon to find
     * @return the TrainerBugemon of the specified specie
     * @throws BugemonNotFoundException if no Bugemon of the specified specie is found in the team
     */
    public TrainerBugemon getMemberOfSpecie(final BugemonSpecie specie) throws BugemonNotFoundException {
        for (final TrainerBugemon bugemon : this.members) {
            if (bugemon.isSpecie(specie)) {
                return bugemon;
            }
        }
        throw new BugemonNotFoundException("Not bugemon of specie " + specie.getName() + " found in team" + this);
    }

    public List<TrainerBugemon> getAliveMembers() {
        List<TrainerBugemon> aliveMembers = this.members.stream().filter(TrainerBugemon::isAlive).toList();
        return aliveMembers;
    }

    /**
     * Resets every Bugemon in the team to its pre-combat state (full HP, no status).
     */
    public void resetForCombat() {
        for (final TrainerBugemon bugemon : this.members) {
            bugemon.resetForCombat();
        }
    }

    /**
     * Resets every Bugemon in the team to level 1 with no XP or stat bonuses, as if starting fresh.
     */
    public void resetProgression() {
        for (final TrainerBugemon bugemon : this.members) {
            bugemon.resetProgression();
        }
    }
}


