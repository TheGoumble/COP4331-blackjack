package command;

import model.GameEngine;
import java.io.Serializable;

/**
 * Command interface for Command Pattern
 * Represents player actions that can be executed by the GameEngine
 * 
 * COMMAND PATTERN: This is the Command interface
 * 
 * @author Javier Vargas, Group 12
 */
public interface Command extends Serializable {
    /**
     * Execute this command on the game engine
     * @param engine The game engine to execute the command on
     */
    void execute(GameEngine engine);
    
    /**
     * Get the player ID who issued this command
     * @return The player's user ID
     */
    String getPlayerId();
}
