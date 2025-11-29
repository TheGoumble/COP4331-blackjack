package command;

import model.GameEngine;

/**
 * Command to set a player's bet amount
 * 
 * @author Javier Vargas, Group 12
 */
public class SetBetCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final String playerId;
    private final int betAmount;
    
    public SetBetCommand(String playerId, int betAmount) {
        this.playerId = playerId;
        this.betAmount = betAmount;
    }
    
    @Override
    public void execute(GameEngine engine) {
        engine.processBet(playerId, betAmount);
    }
    
    @Override
    public String getPlayerId() {
        return playerId;
    }
    
    public int getBetAmount() {
        return betAmount;
    }
}
