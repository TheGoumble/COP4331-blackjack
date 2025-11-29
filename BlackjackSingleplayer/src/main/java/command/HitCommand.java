package command;

import model.GameEngine;

/**
 * Command to request a card (Hit action)
 * 
 * @author Javier Vargas, Group 12
 */
public class HitCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final String playerId;
    
    public HitCommand(String playerId) {
        this.playerId = playerId;
    }
    
    @Override
    public void execute(GameEngine engine) {
        engine.processHit(playerId);
    }
    
    @Override
    public String getPlayerId() {
        return playerId;
    }
    
    @Override
    public String toString() {
        return "HitCommand{playerId='" + playerId + "'}";
    }
}
