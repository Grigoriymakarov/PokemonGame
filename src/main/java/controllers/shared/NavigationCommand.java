package controllers.shared;

import java.io.IOException;

/**
 * Represents a navigation action that can be executed by the application.
 *
 * <p>This functional interface is used to pass screen-switching operations as
 * commands, while still allowing navigation code to report loading errors.</p>
 */
@FunctionalInterface
public interface NavigationCommand {

    /**
     * Executes the navigation action.
     *
     * @throws IOException if the target screen or one of its resources cannot be loaded
     */
    void execute() throws IOException;
}
