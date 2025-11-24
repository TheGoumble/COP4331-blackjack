package app;

import controller.*;
import model.ActiveGame;
import model.User;
import net.PeerNode;
import view.*;

import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 *
 * @author Bridjet Walker
 */
public class SceneRouter {
    private final Stage stage;
    private final ActiveGame game = new ActiveGame();

    private PeerNode peer;

    public SceneRouter(Stage stage){
        this.stage = stage;
    }

    public void goMenu(){
        MenuPageView menu = new MenuPageView();
        new MenuController(menu, this);
        stage.setScene(new Scene(menu, 800, 600));
    }

    public void goHost(){
        try {
            peer = new PeerNode(5000);
            peer.startHost();

            // host is player 1
            User hostUser = new User("Host");
            game.addPlayer(hostUser);

            goTable(hostUser);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void goJoin(){
        peer = new PeerNode(0); // client can use ephemeral port
        LobbyView lobby = new LobbyView();
        new LobbyController(lobby, this, peer);
        stage.setScene(new Scene(lobby, 800, 600));
    }

    public void goTable(User me){
        DealerTableView table = new DealerTableView();
        new DealerTableController(table, game, me);
        stage.setScene(new Scene(table, 900, 650));
    }
}