package view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Bridjet Walker
 */
public class DealerTableView extends BorderPane {
    private final Label label = new Label("Dealer Table (game starts next)");

    public DealerTableView(){
        setCenter(label);
    }

    public void setStatus(String text){
        label.setText(text);
    }
}
