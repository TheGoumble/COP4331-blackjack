package controller;
import app.SceneRouter;
import model.User;
import net.NetMessage;
import net.PeerNode;
import view.LobbyView;

import java.io.PrintWriter;
import java.net.Socket;
/**
 *
 * @author Bridjet Walker
 */
public class LobbyController {
    private final PeerNode peer;

    public LobbyController(LobbyView view, SceneRouter router, PeerNode peer){
        this.peer = peer;

        view.connectBtn.setOnAction(e -> {
            try {
                String name = view.nameField.getText().trim();
                String host = view.hostField.getText().trim();
                int port = Integer.parseInt(view.portField.getText().trim());

                Socket s = peer.connectToHost(host, port);
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                out.println(new NetMessage("JOIN", name).toLine());

                router.goTable(new User(name));

            } catch(Exception ex){
                ex.printStackTrace();
            }
        });
    }
}
