package view;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
/**
 *
 * @author Bridjet Walker
 */
public class LobbyView extends VBox {
    public final TextField nameField = new TextField();
    public final TextField hostField = new TextField("localhost");
    public final TextField portField = new TextField("5000");
    public final Button connectBtn = new Button("Connect");

    public LobbyView(){
        setSpacing(8);
        nameField.setPromptText("Display name");
        hostField.setPromptText("Host IP");
        portField.setPromptText("Host port");
        getChildren().addAll(nameField, hostField, portField, connectBtn);
    }
}