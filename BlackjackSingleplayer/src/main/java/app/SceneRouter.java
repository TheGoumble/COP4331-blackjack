package app;

import controller.DealerTableController;
import controller.MenuController;
import controller.TutorialController;
import controller.MultiplayerTableController;
import controller.GameLobbyController;
import model.ActiveGame;
import model.User;
import network.BlackjackPeer;
import network.DesignatedHost;
import view.DealerTableView;
import view.MenuPageView;
import view.TutorialView;
import view.MultiplayerTableView;
import view.GameLobbyView;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Scene router for navigation between different views
 * 
 * @author Bridjet Walker, Javier Vargas, Group 12
 */
public class SceneRouter {

    private final Stage stage;
    private final ActiveGame game = new ActiveGame(); // single-player state
    private User currentUser; // Store user across navigation
    private DesignatedHost activeHost; // Track active host for cleanup

    public SceneRouter(Stage stage) {
        this.stage = stage;
        
        // Add shutdown hook to cleanup on window close
        stage.setOnCloseRequest(event -> {
            if (activeHost != null) {
                System.out.println("[APP] Window closing - shutting down host");
                activeHost.shutdown();
            }
        });
    }

    public void showMenu() {
        // Clear active host when returning to menu
        this.activeHost = null;
        
        MenuPageView menuView = new MenuPageView();
        MenuController controller = new MenuController(menuView, this, currentUser);
        
        // Store user after menu controller initializes it
        if (currentUser == null) {
            currentUser = controller.getCurrentUser();
        }

        Scene scene = new Scene(menuView, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Blackjack - Menu");
    }

    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void showSinglePlayerTable() {
        DealerTableView tableView = new DealerTableView();
        new DealerTableController(game, tableView, this);

        Scene scene = new Scene(tableView, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Blackjack - Table");
    }

    public void showGameLobby(String userId) {
        GameLobbyView lobbyView = new GameLobbyView();
        new GameLobbyController(lobbyView, this, userId);

        Scene scene = new Scene(lobbyView, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Blackjack - Game Lobby");
    }

    public void showMultiplayerTable(BlackjackPeer peer, DesignatedHost host, String sessionId) {
        // Store host reference for cleanup
        this.activeHost = host;
        
        // Display name should already be set before connecting
        // (see CreateGameObserver, joinGameSession, joinGameByAddress)
        
        MultiplayerTableView tableView = new MultiplayerTableView();
        new MultiplayerTableController(peer, host, tableView, this, sessionId);

        Scene scene = new Scene(tableView, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Blackjack - Multiplayer Table");
    }

    public void joinGameSession(String userId, String sessionId, DesignatedHost host) {
        // Create peer for this player and connect to host
        BlackjackPeer peer = new BlackjackPeer(userId);
        if (currentUser != null) {
            peer.setDisplayName(currentUser.getDisplayName());
        }
        peer.connectToHost(host);
        
        // Navigate to multiplayer table
        showMultiplayerTable(peer, host, sessionId);
    }
    
    public void joinGameByAddress(String userId, String hostAddress, int port) {
        try {
            // Create peer for this player and connect via network
            BlackjackPeer peer = new BlackjackPeer(userId);
            if (currentUser != null) {
                peer.setDisplayName(currentUser.getDisplayName());
            }
            peer.connectToHost(hostAddress, port);
            
            // Navigate to multiplayer table (no host reference for remote connections)
            MultiplayerTableView tableView = new MultiplayerTableView();
            new MultiplayerTableController(peer, null, tableView, this, "remote_" + hostAddress + ":" + port);

            Scene scene = new Scene(tableView, 1400, 800);
            stage.setScene(scene);
            stage.setTitle("Blackjack - Multiplayer Table");
        } catch (Exception e) {
            System.err.println("[ROUTER] Failed to join game: " + e.getMessage());
            // Stay on lobby and show error
            showGameLobby(userId);
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Connection Failed");
                alert.setHeaderText("Unable to connect to game");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
        }
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
