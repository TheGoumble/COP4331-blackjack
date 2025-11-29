package controller;

import app.SceneRouter;
import network.GameDiscoveryService;
import network.GameDiscoveryService.GameAnnouncement;
import view.GameLobbyView;
import javafx.application.Platform;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controller for game lobby
 * Handles browsing and joining game sessions
 * 
 * @author Javier Vargas, Group 12
 */
public class GameLobbyController {

    private final GameLobbyView view;
    private final SceneRouter router;
    private final String userId;
    private final GameDiscoveryService discoveryService;

    public GameLobbyController(GameLobbyView view, SceneRouter router, String userId) {
        this.view = view;
        this.router = router;
        this.userId = userId;
        this.discoveryService = GameDiscoveryService.getInstance();

        // Wire up handlers
        view.setOnJoinSession(this::handleJoinSession);
        view.setOnRefresh(this::refreshGamesList);
        view.setOnBack(this::handleBackToMenu);

        // Start discovery
        startDiscovery();
    }

    /**
     * Start listening for game announcements
     */
    private void startDiscovery() {
        discoveryService.startListening();
        
        // Add listener to update UI when games are discovered
        discoveryService.addUpdateListener(() -> {
            Platform.runLater(this::refreshGamesList);
        });
        
        // Initial refresh
        refreshGamesList();
    }

    /**
     * Refresh the list of available games
     */
    private void refreshGamesList() {
        Map<String, GameAnnouncement> discovered = discoveryService.getDiscoveredGames();
        System.out.println("[LOBBY] Refreshing games list. Found " + discovered.size() + " games.");
        
        Map<String, String> displayGames = new LinkedHashMap<>();
        
        for (Map.Entry<String, GameAnnouncement> entry : discovered.entrySet()) {
            GameAnnouncement game = entry.getValue();
            String display = String.format("Game: %s... - Host: %s - Address: %s:%d",
                game.sessionId.substring(0, Math.min(8, game.sessionId.length())),
                game.hostId,
                game.address,
                game.port
            );
            displayGames.put(entry.getKey(), display);
            System.out.println("[LOBBY] Added game: " + display);
        }
        
        view.updateGamesList(displayGames);
    }

    private void handleJoinSession(String input) {
        System.out.println("[LOBBY] Attempting to join: " + input);
        
        // Input is the display string from the list, extract IP:port
        String[] parts = input.split(":");
        if (parts.length != 2) {
            view.setStatus("Invalid game selection");
            return;
        }
        
        try {
            String host = parts[0].trim();
            int port = Integer.parseInt(parts[1].trim());
            
            System.out.println("[LOBBY] Connecting to discovered game at " + host + ":" + port);
            router.joinGameByAddress(userId, host, port);
            
        } catch (NumberFormatException e) {
            view.setStatus("Invalid port number in game announcement");
        } catch (Exception e) {
            view.setStatus("Connection failed: " + e.getMessage());
            System.err.println("[LOBBY] Connection error: " + e.getMessage());
        }
    }

    private void handleBackToMenu() {
        discoveryService.stopListening();
        router.showMenu();
    }
}
