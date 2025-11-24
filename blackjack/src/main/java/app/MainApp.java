package app;

import javafx.application.Application;
import javafx.stage.Stage;
/**
 *
 * @author Bridjet Walker
 */
public class MainApp extends Application {
    @Override
    public void start(Stage stage){
        SceneRouter router = new SceneRouter(stage);
        router.goMenu();
        stage.setTitle("Blackjack P2P");
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
