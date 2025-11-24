package view;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 *
 * @author Bridjet Walker
 */
public class MenuPageView extends VBox {
    public final Button hostBtn = new Button("Create / Host Game");
    public final Button joinBtn = new Button("Join Game");

    public MenuPageView(){
        setSpacing(12);
        getChildren().addAll(hostBtn, joinBtn);
    }
}