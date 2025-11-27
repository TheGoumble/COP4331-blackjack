package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
/**
 *
 * @author Bridjet Walker
 */
public class MenuPageView extends VBox {

    public final Button createGameButton = new Button("Create Blackjack Game");
    public final Button joinGameButton = new Button("Join Game");
    public final Button tutorialButton = new Button("Tutorial");
    public final Button closeButton = new Button("Close Application");

    public MenuPageView() {
        setSpacing(15);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        Label title = new Label("Blackjack");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // You can disable Join for now if you want:
        // joinGameButton.setDisable(true);

        getChildren().addAll(
                title,
                createGameButton,
                joinGameButton,
                tutorialButton,
                closeButton
        );
    }
}