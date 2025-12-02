package observer;

/**
 * Observer interface for menu actions
 * Implements Observer pattern for menu button clicks
 * 
 * OBSERVER PATTERN: This is the Observer interface
 * 
 * @author Javier Vargas, Group 12
 */
public interface MenuObserver {
    /**
     * Called when a menu action is triggered
     * @param action The action that was triggered
     * @param userId The user ID who triggered the action
     */
    void update(String action, String userId);
}
