package network;

import command.Command;
import model.GameEngine;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Designated host that validates and executes commands
 * Acts as the authoritative server for the game
 * 
 * @author Javier Vargas, Luca Lombardo Group 12
 */
public class DesignatedHost {
    private final GameEngine gameEngine;
    private final List<ClientPeer> connectedClients;
    private final String hostId;
    private String displayName;
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private boolean running;
    private final int port;
    private String gameCode;
    private ApiClient apiClient;
    
    public DesignatedHost(String hostId, String displayName) {
        this.hostId = hostId;
        this.displayName = displayName != null ? displayName : hostId;
        this.gameEngine = new GameEngine();
        this.connectedClients = new CopyOnWriteArrayList<>();
        this.port = findAvailablePort();
        this.running = false;
        startServer();
    }
    
    /**
     * Find an available port for the server
     */
    private int findAvailablePort() {
        try (ServerSocket tempSocket = new ServerSocket(0)) {
            return tempSocket.getLocalPort();
        } catch (IOException e) {
            return 25565; // Default fallback port
        }
    }
    
    /**
     * Start the server and listen for connections
     */
    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("[HOST] Server started on " + hostAddress + ":" + port);
            
            // Start accepting connections in a background thread
            acceptThread = new Thread(this::acceptConnections);
            acceptThread.setDaemon(true);
            acceptThread.start();
            
        } catch (IOException e) {
            System.err.println("[HOST] Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Accept incoming client connections
     */
    private void acceptConnections() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[HOST] New client connection from " + clientSocket.getInetAddress());
                
                // Handle this client in a separate thread
                Thread clientThread = new Thread(() -> handleClientConnection(clientSocket));
                clientThread.setDaemon(true);
                clientThread.start();
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("[HOST] Error accepting connection: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Handle a client connection
     */
    private void handleClientConnection(Socket socket) {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            
            // Read the client's user ID and display name
            String userId = (String) in.readObject();
            String displayName = (String) in.readObject();
            System.out.println("[HOST] Client identified as: " + userId + " (" + displayName + ")");
            
            // Create a ClientPeer for this connection
            ClientPeer client = new ClientPeer(userId, socket, in, out);
            client.setDisplayName(displayName);
            registerClient(client);
            
            // Listen for commands from this client
            while (running && !socket.isClosed()) {
                try {
                    Object obj = in.readObject();
                    if (obj instanceof Command) {
                        validateAndExecute((Command) obj);
                    }
                } catch (EOFException | SocketException e) {
                    // Client disconnected
                    break;
                }
            }
            
            // Client disconnected
            unregisterClient(client);
            socket.close();
            
        } catch (Exception e) {
            System.err.println("[HOST] Error handling client: " + e.getMessage());
        }
    }
    
    /**
     * Register a client peer connection
     */
    public void registerClient(ClientPeer client) {
        connectedClients.add(client);
        
        // Add player to game engine (will be marked as spectator if round in progress)
        gameEngine.addPlayer(client.getUserId(), 10000);
        
        // Send existing players list to the new client
        List<PlayerInfo> existingPlayers = new ArrayList<>();
        existingPlayers.add(new PlayerInfo(hostId, getDisplayName())); // Always include host
        for (ClientPeer existingClient : connectedClients) {
            if (!existingClient.getUserId().equals(client.getUserId())) {
                existingPlayers.add(new PlayerInfo(existingClient.getUserId(), existingClient.getDisplayName()));
            }
        }
        client.receiveGameUpdate(new GameUpdateMessage(
            GameUpdateMessage.MessageType.PLAYERS_LIST,
            existingPlayers
        ));
        
        // If player joined mid-round, notify them they're spectating
        if (gameEngine.isSpectator(client.getUserId())) {
            client.receiveGameUpdate(new GameUpdateMessage(
                GameUpdateMessage.MessageType.SPECTATOR_MODE,
                "You joined mid-game. You'll play in the next round."
            ));
        }
        
        // Notify API of player join
        if (gameCode != null && apiClient != null) {
            apiClient.notifyPlayerJoin(gameCode);
        }
        
        // Notify all clients of new player
        broadcast(new GameUpdateMessage(
            GameUpdateMessage.MessageType.PLAYER_JOINED,
            new PlayerInfo(client.getUserId(), client.getDisplayName())
        ));
    }
    
    /**
     * Unregister a client peer connection
     */
    public void unregisterClient(ClientPeer client) {
        String playerId = client.getUserId();
        boolean wasInRound = gameEngine.isRoundInProgress();
        String currentPlayerBefore = gameEngine.getCurrentPlayer();
        
        connectedClients.remove(client);
        gameEngine.removePlayer(playerId);
        
        // Notify API of player leave
        if (gameCode != null && apiClient != null) {
            apiClient.notifyPlayerLeave(gameCode);
        }
        
        broadcast(new GameUpdateMessage(
            GameUpdateMessage.MessageType.PLAYER_LEFT,
            playerId
        ));
        
        // If player left during their turn, broadcast turn change
        if (wasInRound && playerId.equals(currentPlayerBefore)) {
            String newCurrentPlayer = gameEngine.getCurrentPlayer();
            broadcast(new GameUpdateMessage(
                GameUpdateMessage.MessageType.TURN_CHANGED,
                newCurrentPlayer
            ));
            System.out.println("[HOST] Player left during their turn, advancing to next player");
            
            // Check if all remaining players have finished
            if (gameEngine.allPlayersFinished()) {
                playDealerTurn();
            }
        }
    }
    
    /**
     * Validate and execute a command from a client
     */
    public void validateAndExecute(Command command) {
        try {
            // Execute command on game engine
            command.execute(gameEngine);
            
            // Determine message type based on command
            GameUpdateMessage.MessageType msgType = 
                command.getClass().getSimpleName().equals("HitCommand") ? 
                    GameUpdateMessage.MessageType.PLAYER_HIT :
                command.getClass().getSimpleName().equals("StandCommand") ?
                    GameUpdateMessage.MessageType.PLAYER_STAND :
                    GameUpdateMessage.MessageType.BET_PLACED;
            
            // Broadcast update to all clients
            broadcast(new GameUpdateMessage(msgType, command.getPlayerId()));
            
            // If this was a bet and all bets are now placed, deal cards
            if (msgType == GameUpdateMessage.MessageType.BET_PLACED && gameEngine.allBetsPlaced()) {
                dealCards();
            }
            
            // Broadcast updated game state so all clients see the changes
            broadcastGameState();
            
            // Broadcast turn change if turn advanced (for Hit/Stand commands)
            if (msgType == GameUpdateMessage.MessageType.PLAYER_HIT || 
                msgType == GameUpdateMessage.MessageType.PLAYER_STAND) {
                String currentPlayer = gameEngine.getCurrentPlayer();
                broadcast(new GameUpdateMessage(
                    GameUpdateMessage.MessageType.TURN_CHANGED,
                    currentPlayer // null if all players finished
                ));
            }
            
            // Check if all players have finished
            if (gameEngine.allPlayersFinished() && gameEngine.isRoundInProgress()) {
                playDealerTurn();
            }
            
        } catch (Exception e) {
            System.err.println("Command execution failed: " + e.getMessage());
        }
    }
    
    /**
     * Play dealer's turn and end the round
     */
    private void playDealerTurn() {
        gameEngine.playDealerTurn();
        broadcast(new GameUpdateMessage(
            GameUpdateMessage.MessageType.DEALER_TURN,
            gameEngine.getDealerHand()
        ));
        
        // Determine results
        var results = gameEngine.determineResults();
        broadcast(new GameUpdateMessage(
            GameUpdateMessage.MessageType.ROUND_ENDED,
            results
        ));
    }
    
    /**
     * Start a new round - enables betting phase
     */
    public void startRound() {
        gameEngine.startRound();
        broadcast(new GameUpdateMessage(
            GameUpdateMessage.MessageType.ROUND_STARTED,
            null
        ));
        
        // Broadcast initial game state (no cards yet, just show betting phase)
        broadcastGameState();
    }
    
    /**
     * Deal cards after all bets are placed
     */
    private void dealCards() {
        gameEngine.dealCards();
        
        // Broadcast that cards are being dealt
        broadcast(new GameUpdateMessage(
            GameUpdateMessage.MessageType.GAME_STATE_UPDATE,
            "Cards dealt!"
        ));
        
        // Broadcast whose turn it is (first player)
        String firstPlayer = gameEngine.getCurrentPlayer();
        broadcast(new GameUpdateMessage(
            GameUpdateMessage.MessageType.TURN_CHANGED,
            firstPlayer
        ));
        
        // Broadcast updated game state so all players see dealt cards
        broadcastGameState();
    }
    
    /**
     * Broadcast a message to all connected clients
     */
    public void broadcast(GameUpdateMessage message) {
        for (ClientPeer client : connectedClients) {
            client.receiveGameUpdate(message);
        }
    }
    
    /**
     * Broadcast current game state (hands, balances, bets, dealer hand) to all clients
     */
    private void broadcastGameState() {
        Map<String, Object> state = new HashMap<>();
        state.put("playerHands", gameEngine.getCurrentHands());
        state.put("playerBalances", gameEngine.getPlayerBalances());
        state.put("playerBets", gameEngine.getPlayerBets());
        state.put("dealerHand", gameEngine.getDealerHand());
        
        broadcast(new GameUpdateMessage(
            GameUpdateMessage.MessageType.GAME_STATE_UPDATE,
            state
        ));
    }
    
    /**
     * Shutdown the server
     */
    public void shutdown() {
        running = false;
        
        // Unregister from API
        if (gameCode != null && apiClient != null) {
            System.out.println("[HOST] Unregistering game from API: " + gameCode);
            apiClient.unregisterGame(gameCode);
        }
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (acceptThread != null) {
                acceptThread.interrupt();
            }
            // Disconnect all clients
            for (ClientPeer client : new ArrayList<>(connectedClients)) {
                client.disconnect();
            }
        } catch (IOException e) {
            System.err.println("[HOST] Error shutting down server: " + e.getMessage());
        }
    }
    
    public GameEngine getGameEngine() {
        return gameEngine;
    }
    
    public String getHostId() {
        return hostId;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public int getConnectedClientCount() {
        return connectedClients.size();
    }
    
    public int getPort() {
        return port;
    }
    
    public String getConnectionString() {
        try {
            return InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            return "localhost:" + port;
        }
    }
    
    /**
     * Set the game code for API cleanup
     */
    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }
    
    /**
     * Set API client for cleanup
     */
    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    /**
     * Get the game code
     */
    public String getGameCode() {
        return gameCode;
    }

}
