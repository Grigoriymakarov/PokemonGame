import constants.LabelConstants;
import controllers.shared.MetaController;
import controllers.shared.Windows;
import javafx.application.Application;
import javafx.stage.Stage;
import models.shared.trainer.Player;
import repositories.AttackRepository;
import repositories.ItemRepository;
import services.BugemonService;
import services.DataInitializationService;
import services.InventoryService;
import services.TowerService;

import java.io.IOException;

/**
 * Main entry point for the Bugemon application.
 *
 * <p>This class extends {@link javafx.application.Application} and serves as the JavaFX
 * application entry point. It initializes the {@link MetaController} and navigates to the
 * home screen when the application starts.</p>
 *
 * @see MetaController
 * @see Windows
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application by initializing the meta controller and displaying the home screen.
     *
     * @param primaryStage the primary stage provided by JavaFX
     * @throws Exception if an error occurs during initialization
     */
    @Override
    public void start(final Stage primaryStage) {
        final Player trainer;
        final BugemonService bugemonService;
        final InventoryService inventoryService;
        final TowerService towerService;
        try {
            final AttackRepository attackRepository = new AttackRepository();
            final ItemRepository itemRepository = new ItemRepository();
            new DataInitializationService(attackRepository, itemRepository).initializeAllRepositories();

            trainer = new Player(LabelConstants.DEFAULT_PLAYER_NAME);
            bugemonService = new BugemonService();
            inventoryService = new InventoryService(trainer, itemRepository);
            towerService = new TowerService();
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize data repositories", e);
        }

        final MetaController metaController = new MetaController(
                primaryStage,
                trainer,
                bugemonService,
                inventoryService,
                towerService);
        metaController.switchTo(Windows.HOMESCREEN);
    }

    /**
     * Main method that launches the JavaFX application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(final String[] args) {Application.launch(args);}
}
