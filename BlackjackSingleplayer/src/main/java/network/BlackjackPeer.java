package network;

import model.Card;
import java.util.*;

/**
 * BlackjackPeer extends ClientPeer with local game state caching
 * Maintains a local copy of game state for UI rendering
 * 
 * @author Javier Vargas, Group 12
 */
public class BlackjackPeer extends ClientPeer {
    private Object localGameState;
    private Map<String, List<Card>> playerHands;
    private Map<String, Integer> playerBalances;
    private Map<String, String> playerDisplayNames; // Map userId to displayName
    private List<Card> dealerHand;
    private String currentTurnPlayer; // Track whose turn it is
    
    public BlackjackPeer(String userId) {
        super(userId);
        this.playerHands = new HashMap<>();
        this.playerBalances = new HashMap<>();
        this.playerDisplayNames = new HashMap<>();
        this.dealerHand = new ArrayList<>();
        this.currentTurnPlayer = null;
        
        // Register listener to update local state
        addUpdateListener(this::updateLocalState);
    }
    
    /**
     * Connect to a host by address
     */
    public void connectToHost(String hostAddress, int port) {
        super.connectToHost(hostAddress, port);
    }
    
    /**
     * Connect to a local host instance
     */
    @Override
    public void connectToHost(DesignatedHost host) {
        super.connectToHost(host);
    }
    
    /**
     * Update local game state when receiving updates from host
     */
    private void updateLocalState(GameUpdateMessage message) {
        switch (message.getType()) {
            case GAME_STATE_UPDATE:
                // Full state update
                if (message.getData() instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> state = (Map<String, Object>) message.getData();
                    updateFromFullState(state);
                }
                break;
                
            case PLAYERS_LIST:
                // Initialize with existing players
                if (message.getData() instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<PlayerInfo> players = (List<PlayerInfo>) message.getData();
                    for (PlayerInfo player : players) {
                        if (!player.getUserId().equals(getUserId())) {
                            playerBalances.put(player.getUserId(), 10000);
                            playerHands.put(player.getUserId(), new ArrayList<>());
                            playerDisplayNames.put(player.getUserId(), player.getDisplayName());
                        }
                    }
                }
                break;
                
            case PLAYER_JOINED:
                if (message.getData() instanceof PlayerInfo) {
                    PlayerInfo joinedPlayer = (PlayerInfo) message.getData();
                    playerBalances.put(joinedPlayer.getUserId(), 10000);
                    playerHands.put(joinedPlayer.getUserId(), new ArrayList<>());
                    playerDisplayNames.put(joinedPlayer.getUserId(), joinedPlayer.getDisplayName());
                }
                break;
                
            case PLAYER_LEFT:
                String leftPlayer = (String) message.getData();
                playerBalances.remove(leftPlayer);
                playerHands.remove(leftPlayer);
                break;
                
            case ROUND_STARTED:
                // Clear hands for new round
                for (String playerId : playerHands.keySet()) {
                    playerHands.get(playerId).clear();
                }
                dealerHand.clear();
                break;
                
            case DEALER_TURN:
                if (message.getData() instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Card> cards = (List<Card>) message.getData();
                    dealerHand = new ArrayList<>(cards);
                }
                break;
                
            case TURN_CHANGED:
                // Update whose turn it is
                currentTurnPlayer = (String) message.getData(); // Can be null if all players finished
                break;
                
            default:
                // Other message types handled by UI listeners
                break;
        }
    }
    
    /**
     * Update from a full game state snapshot
     */
    @SuppressWarnings("unchecked")
    private void updateFromFullState(Map<String, Object> state) {
        if (state.containsKey("playerHands")) {
            this.playerHands = (Map<String, List<Card>>) state.get("playerHands");
        }
        if (state.containsKey("playerBalances")) {
            this.playerBalances = (Map<String, Integer>) state.get("playerBalances");
        }
        if (state.containsKey("dealerHand")) {
            this.dealerHand = (List<Card>) state.get("dealerHand");
        }
    }
    
    /**
     * Request full game state from host
     * The host automatically broadcasts state updates, so this is handled passively
     */
    public void requestGameState() {
        // Game state updates are automatically broadcast by the host
        // after each command execution, so no explicit request is needed
        System.out.println("[PEER] Waiting for next game state broadcast from host");
    }
    
    // Getters for local game state
    public Object getLocalGameState() {
        return localGameState;
    }
    
    public Map<String, List<Card>> getPlayerHands() {
        return new HashMap<>(playerHands);
    }
    
    public Map<String, Integer> getPlayerBalances() {
        return new HashMap<>(playerBalances);
    }
    
    public List<Card> getDealerHand() {
        return new ArrayList<>(dealerHand);
    }
    
    public List<Card> getMyHand() {
        return playerHands.getOrDefault(getUserId(), new ArrayList<>());
    }
    
    public int getMyBalance() {
        return playerBalances.getOrDefault(getUserId(), 0);
    }
    
    public Map<String, String> getPlayerDisplayNames() {
        return new HashMap<>(playerDisplayNames);
    }
    
    public String getCurrentTurnPlayer() {
        return currentTurnPlayer;
    }
    
    public boolean isMyTurn() {
        return currentTurnPlayer != null && currentTurnPlayer.equals(getUserId());
    }
}
