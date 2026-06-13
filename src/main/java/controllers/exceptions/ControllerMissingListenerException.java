package controllers.exceptions;

import controllers.BaseController;

/**
 * Exception thrown when a controller does not implement its required ViewListener interface.
 *
 * <p>Every controller extending {@link BaseController} must implement a sub-interface of ViewListener
 * corresponding to its first generic parameter.</p>
 */
public class ControllerMissingListenerException extends RuntimeException {

    /**
     * Constructs a new exception with the given controller class name.
     *
     * @param controllerClassName the name of the controller class that does not implement ViewListener
     */
    public ControllerMissingListenerException(final String controllerClassName) {
        super("The controller " + controllerClassName + " must implement a sub-interface of BaseView.ViewListener");
    }

    /**
     * Constructs a new exception with a detailed message.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public ControllerMissingListenerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

