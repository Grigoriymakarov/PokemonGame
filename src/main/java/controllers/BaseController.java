package controllers;

import controllers.exceptions.ControllerMissingListenerException;
import controllers.shared.MetaController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import views.BaseView;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Generic abstract base class for all MVC screen controllers.
 *
 * <p>A screen controller acts as the intermediary between a {@link BaseView} and the services and models necessary for core logic.
 * It owns a {@link BaseView} and is spawned in the application's global navigation logic (via {@link MetaController}).</p>
 *
 * <p>It receives messages from its {@link BaseView} according to a defined {@link BaseView.ViewListener}.
 * <strong>A Controller should thus always implement its {@code ViewListener} parameter.</strong></p>
 *
 * <p><strong>The {@link BaseView.ViewListener} parameter should be coherent with the {@link BaseView} parameter's implementation.</strong></p>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>
 * {@code
 * public class CombatController extends BaseController<CombatView.Listener, CombatView> implements CombatView.Listener {
 *     private CombatService combatService;
 *
 *     public CombatController(MetaController metaController, CombatView view, Team playerTeam, Team enemyTeam) {
 *         super(metaController, view);
 *         this.combatService = new CombatService(playerTeam, enemyTeam);
 *         ...
 *     }
 * }
 * }
 * </pre>
 *
 * @param <L> the type of listener used to receive events from the associated view
 * @param <V> the type of view managed by this controller
 * @see BaseView.ViewListener
 * @see BaseView
 * @see MetaController
 */
public abstract class BaseController<L extends BaseView.ViewListener, V extends BaseView<L, ?>> {

    /**
     * Logger instance for this controller class.
     */
    protected final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    /**
     * Global navigation controller of the application.
     */
    protected final MetaController metaController;

    /**
     * View managed by this controller.
     */
    protected final V view;

    /**
     * Creates a new screen controller.
     *
     * @param metaController the global navigation controller
     * @param view           the managed view
     * @throws ControllerMissingListenerException if the concrete controller class does not implement the ViewListener interface
     */
    protected BaseController(final MetaController metaController, final V view) {
        // Verify that the concrete controller class implements the ViewListener interface
        if (!(this instanceof BaseView.ViewListener)) {
            throw new ControllerMissingListenerException(this.getClass().getSimpleName());
        }

        this.metaController = metaController;
        this.view = view;
        // Cast is safe: the instanceof check above guarantees that 'this' implements L
        this.view.setListener((L)this);
    }

    /**
     * Displays this controller's view in the given stage.
     * <p>Concrete controller classes must implement this method using {@link BaseController#showView(Stage, int, int)} or {@link BaseController#showView(Stage, Scene)}
     * to define precisely how their view should be displayed in the stage.</p>
     *
     * @param stage the JavaFX stage
     */
    public abstract void showView(final Stage stage) throws FileNotFoundException;

    /**
     * Displays this controller's view in the given stage with specified dimensions.
     *
     * @param stage       the JavaFX stage
     * @param sceneWidth  the width of the scene
     * @param sceneHeight the height of the scene
     * @throws FileNotFoundException if the CSS file is not found in the resources
     */
    protected final void showView(final Stage stage, final int sceneWidth, final int sceneHeight) throws FileNotFoundException {
        final Scene scene = new Scene(this.view.getRoot(), sceneWidth, sceneHeight);
        //we take the root of fxml file and create from it a scene and put it on the given from main Stage
        this.showView(stage, scene);
    }

    /**
     * Displays this controller's view in the given stage with the provided scene.
     *
     * <p>This method is useful for controllers that need to customize the scene (e.g. dimensions) before displaying the view.</p>
     *
     * @param stage the JavaFX stage
     * @param scene the JavaFX scene to use for displaying the view
     * @throws FileNotFoundException if the CSS file is not found in the resources
     */
    protected final void showView(final Stage stage, Scene scene) throws FileNotFoundException {
        final URL css = getClass().getResource("/css/styles.css");
        if (css == null) {
            throw new FileNotFoundException("CSS file not found at resource path: /css/styles.css");
        } else {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.show();
    }
}