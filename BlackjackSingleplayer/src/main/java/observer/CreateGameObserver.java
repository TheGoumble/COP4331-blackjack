package observer;

import app.SceneRouter;
import network.DesignatedHost;
import network.BlackjackPeer;
import network.ApiClient;

/**
 * Observer for CreateGame button
 * Starts a new game session as host
 * 
 * @author Javier Vargas, Group 12
 */
public class CreateGameObserver implements MenuObserver {
    private final SceneRouter router;
    private final ApiClient apiClient;
    private DesignatedHost host;
    private BlackjackPeer peer;
    private String sessionId;
    private String gameCode;
    
    public CreateGameObserver(SceneRouter router) {
        this.router = router;
        this.apiClient = new ApiClient();
    }
    
    @Override
    public void update(String action, String userId) {
        if ("CREATE_LOCAL_GAME".equals(action)) {
            startLocalGameSession(userId);
        } else if ("CREATE_ONLINE_GAME".equals(action)) {
            startOnlineGameSession(userId);
        }
    }
    
    /**
     * Start a local multiplayer game session (no API registration)
     */
    private void startLocalGameSession(String userId) {
        // Generate unique session ID
        sessionId = "local_" + System.currentTimeMillis();
        
        // Get display name from router's current user
        String displayName = router.getCurrentUser() != null ? 
            router.getCurrentUser().getDisplayName() : userId;
        
        // Create host (P2P server) - no API registration
        host = new DesignatedHost(userId, displayName);
        
        // Create peer for this player and connect to host
        peer = new BlackjackPeer(userId);
        peer.setDisplayName(displayName);
        peer.connectToHost(host);
        
        System.out.println("===========================================");
        System.out.println("Created LOCAL game session: " + sessionId);
        System.out.println(">>> CONNECTION: " + host.getConnectionString() + " <<<");
        System.out.println("Share this address for direct connection!");
        System.out.println("===========================================");
        
        // Navigate to multiplayer table
        router.showMultiplayerTable(peer, host, sessionId);
    }
    
    /**
     * Start an online game session with API registration
     */
    private void startOnlineGameSession(String userId) {
        // Generate unique session ID and short game code
        sessionId = "game_" + System.currentTimeMillis();
        gameCode = generateGameCode();
        
        // Get display name from router's current user
        String displayName = router.getCurrentUser() != null ? 
            router.getCurrentUser().getDisplayName() : userId;
        
        // Create host (P2P server) - UPnP will attempt automatic port forwarding
        host = new DesignatedHost(userId, displayName);
        
        // Register game with API matchmaking server using public IP if available
        String publicIP = host.getPublicIP();
        String hostAddress;
        
        if (publicIP != null) {
            hostAddress = publicIP;
            System.out.println("[ONLINE] Using public IP: " + publicIP);
        } else {
            // Fallback to local IP (won't work over internet)
            hostAddress = host.getConnectionString().split(":")[0];
            System.out.println("[ONLINE] WARNING: Using local IP - internet play may not work!");
        }
        
        int hostPort = host.getPort();
        
        ApiClient.RegisterResponse response = apiClient.registerGame(
            gameCode, userId, displayName, hostAddress, hostPort);
        
        if (!response.success) {
            System.err.println("Failed to register game with API: " + response.error);
            // Continue anyway - can still direct connect
        }
        
        // Create peer for this player and connect to host
        peer = new BlackjackPeer(userId);
        
        // Set display name BEFORE connecting (reuse variable from above)
        peer.setDisplayName(displayName);
        
        peer.connectToHost(host);
        
        // Store game code in host for cleanup later
        host.setGameCode(gameCode);
        host.setApiClient(apiClient);
        
        System.out.println("===========================================");
        System.out.println("Created game session: " + sessionId);
        System.out.println(">>> GAME CODE: " + gameCode + " <<<");
        System.out.println("Registered at: " + hostAddress + ":" + hostPort);
        System.out.println("Share this code with players to join!");
        System.out.println("===========================================");
        
        // Navigate to multiplayer table
        router.showMultiplayerTable(peer, host, gameCode);
    }
    
    /**
     * Generate a short 6-character game code
     */
    private String generateGameCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Exclude similar looking chars
        StringBuilder code = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
    
    public DesignatedHost getHost() {
        return host;
    }
    
    public BlackjackPeer getPeer() {
        return peer;
    }
    
    public String getSessionId() {
        return sessionId;
    }
}
