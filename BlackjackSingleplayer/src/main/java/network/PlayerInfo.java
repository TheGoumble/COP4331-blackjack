package network;

import java.io.Serializable;

/**
 * Encapsulates player information for network transmission
 * Contains both userId and displayName
 * 
 * @author Group 12
 */
public class PlayerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String userId;
    private final String displayName;
    
    public PlayerInfo(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return "PlayerInfo{userId='" + userId + "', displayName='" + displayName + "'}";
    }
}
