package app;

import javafx.application.Application;
import javafx.stage.Stage;
/**
 *
 * @author Bridjet Walker, Javier Vargas, Group 12
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        SceneRouter router = new SceneRouter(stage);
        router.showMenu();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}