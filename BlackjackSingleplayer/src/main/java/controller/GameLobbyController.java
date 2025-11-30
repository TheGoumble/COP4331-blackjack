package controller;

import app.SceneRouter;
import network.ApiClient;
import network.ApiClient.GameInfo;
import view.GameLobbyView;

import java.util.LinkedHashMap;
import java.util.List;
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
    private final ApiClient apiClient;

    public GameLobbyController(GameLobbyView view, SceneRouter router, String userId) {
        this.view = view;
        this.router = router;
        this.userId = userId;
        this.apiClient = new ApiClient();

        // Wire up handlers
        view.setOnJoinSession(this::handleJoinSession);
        view.setOnRefresh(this::refreshGamesList);
        view.setOnBack(this::handleBackToMenu);

        // Initial refresh when page loads
        refreshGamesList();
    }

    /**
     * Refresh the list of available games from API
     */
    private void refreshGamesList() {
        List<GameInfo> games = apiClient.listGames();
        System.out.println("[LOBBY] Refreshing games list from API. Found " + games.size() + " game(s).");
        
        Map<String, String> displayGames = new LinkedHashMap<>();
        
        for (GameInfo game : games) {
            // Include player count in display
            String display = String.format("%s (%d/%d) - Code: %s",
                game.hostDisplayName,
                game.playerCount,
                game.maxPlayers,
                game.gameCode
            );
            displayGames.put(game.gameCode, display);
            
            // Debug log each game
            System.out.println("[LOBBY]   Game: " + game.gameCode + 
                             " - Host: " + game.hostDisplayName + 
                             " - Players: " + game.playerCount + "/" + game.maxPlayers);
        }
        
        view.updateGamesList(displayGames);
    }

    private void handleJoinSession(String input) {
        System.out.println("[LOBBY] Attempting to join: " + input);
        
        // Check if it's a 6-character game code
        if (input.length() == 6 && input.matches("[A-Z0-9]+")) {
            GameInfo game = apiClient.getGame(input);
            if (game != null) {
                System.out.println("[LOBBY] Game code found: " + input + " -> " + game.getConnectionString());
                connectToAddress(game.getConnectionString());
            } else {
                view.setStatus("Game code not found: " + input);
            }
            return;
        }
        
        // Otherwise treat as IP:port
        connectToAddress(input);
    }
    
    private void connectToAddress(String addressPort) {
        // Input is either from list or direct IP:port
        String[] parts = addressPort.split(":");
        if (parts.length != 2) {
            view.setStatus("Invalid format. Use Game Code or IP:Port");
            return;
        }
        
        try {
            String host = parts[0].trim();
            int port = Integer.parseInt(parts[1].trim());
            
            System.out.println("[LOBBY] Connecting to " + host + ":" + port);
            router.joinGameByAddress(userId, host, port);
            
        } catch (NumberFormatException e) {
            view.setStatus("Invalid port number");
        } catch (Exception e) {
            view.setStatus("Connection failed: " + e.getMessage());
            System.err.println("[LOBBY] Connection error: " + e.getMessage());
        }
    }

    private void handleBackToMenu() {
        // No cleanup needed - API is stateless
        router.showMenu();
    }
}
