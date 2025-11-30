package network;

import java.io.Serializable;

/**
 * Base message for game state updates
 * 
 * @author Javier Vargas, Group 12
 */
public class GameUpdateMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final MessageType type;
    private final Object data;
    private final long timestamp;
    
    public enum MessageType {
        PLAYER_JOINED,
        PLAYER_LEFT,
        PLAYERS_LIST,
        BET_PLACED,
        ROUND_STARTED,
        CARD_DEALT,
        PLAYER_HIT,
        PLAYER_STAND,
        TURN_CHANGED,
        DEALER_TURN,
        ROUND_ENDED,
        GAME_STATE_UPDATE
    }
    
    public GameUpdateMessage(MessageType type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public MessageType getType() {
        return type;
    }
    
    public Object getData() {
        return data;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "GameUpdateMessage{type=" + type + ", timestamp=" + timestamp + "}";
    }
}
