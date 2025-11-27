package app;

import controller.DealerTableController;
import controller.MenuController;
import controller.TutorialController;
import model.ActiveGame;
import view.DealerTableView;
import view.MenuPageView;
import view.TutorialView;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 *
 * @author Bridjet Walker
 */
public class SceneRouter {

    private final Stage stage;
    private final ActiveGame game = new ActiveGame(); // single-player state

    public SceneRouter(Stage stage) {
        this.stage = stage;
    }

    public void showMenu() {
        MenuPageView menuView = new MenuPageView();
        new MenuController(menuView, this);

        Scene scene = new Scene(menuView, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Blackjack - Menu");
    }

    public void showSinglePlayerTable() {
        DealerTableView tableView = new DealerTableView();
        new DealerTableController(game, tableView);

        Scene scene = new Scene(tableView, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Blackjack - Table");
    }

    public void showTutorial() {
        TutorialView tutorialView = new TutorialView();
        new TutorialController(tutorialView, this);

        Scene scene = new Scene(tutorialView, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Blackjack - Tutorial");
    }

    public void closeApp() {
        stage.close();
    }
}
