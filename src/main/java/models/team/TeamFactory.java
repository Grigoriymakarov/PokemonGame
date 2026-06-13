package models.team;

/**
 * Abstract class defining the contract for creating Bugemon teams.
 *
 * <p>This class implements the <b>Factory Method</b> pattern: concrete subclasses
 * decide how to compose a team, allowing different creation strategies
 * without modifying the calling code.</p>
 *
 * <p>Available subclasses:</p>
 * <ul>
 *   <li>{@link RandomTeamFactory}: creates a random team</li>
 * </ul>
 *
 * @see RandomTeamFactory
 * @see Team
 */
public abstract class TeamFactory {

    /**
     * Creates a team containing the specified number of Bugemons.
     *
     * @param bugemonNumber the number of Bugemons to include in the team
     * @return a new {@link Team} assembled according to the subclass strategy
     */
    public abstract Team createTeam(int bugemonNumber);
}

