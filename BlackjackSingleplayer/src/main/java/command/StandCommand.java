package command;

import model.GameEngine;

/**
 * Command for player to stand (end their turn)
 * 
 * COMMAND PATTERN: This is a Concrete Command
 * 
 * @author Javier Vargas, Group 12
 */
public class StandCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final String playerId;
    
    public StandCommand(String playerId) {
        this.playerId = playerId;
    }
    
    @Override
    public void execute(GameEngine engine) {
        engine.processStand(playerId);
    }
    
    @Override
    public String getPlayerId() {
        return playerId;
    }
}
