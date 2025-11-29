package observer;

import app.SceneRouter;
import network.DesignatedHost;
import network.BlackjackPeer;

/**
 * Observer for CreateGame button
 * Starts a new game session as host
 * 
 * @author Javier Vargas, Group 12
 */
public class CreateGameObserver implements MenuObserver {
    private final SceneRouter router;
    private DesignatedHost host;
    private BlackjackPeer peer;
    private String sessionId;
    
    public CreateGameObserver(SceneRouter router) {
        this.router = router;
    }
    
    @Override
    public void update(String action, String userId) {
        if ("CREATE_GAME".equals(action)) {
            startGameSession(userId);
        }
    }
    
    /**
     * Start a new game session with this user as host
     */
    private void startGameSession(String userId) {
        // Generate unique session ID
        sessionId = "game_" + System.currentTimeMillis();
        
        // Create host
        host = new DesignatedHost(userId);
        
        // Start broadcasting this game on the network
        network.GameDiscoveryService.getInstance().startBroadcasting(
            sessionId, userId, host.getConnectionString().split(":")[0], host.getPort());
        
        // Create peer for this player and connect to host
        peer = new BlackjackPeer(userId);
        peer.connectToHost(host);
        
        System.out.println("Created game session: " + sessionId);
        
        // Navigate to multiplayer table
        router.showMultiplayerTable(peer, host, sessionId);
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
