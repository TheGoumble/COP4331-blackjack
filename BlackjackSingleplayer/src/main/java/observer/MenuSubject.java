package observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject that notifies menu observers of actions
 * 
 * OBSERVER PATTERN: This is the Subject
 * 
 * @author Javier Vargas, Group 12
 */
public class MenuSubject {
    private final List<MenuObserver> observers;
    
    public MenuSubject() {
        this.observers = new ArrayList<>();
    }
    
    /**
     * Register a menu observer
     */
    public void addMenuObserver(MenuObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Unregister a menu observer
     */
    public void removeMenuObserver(MenuObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notify all observers of a menu action
     */
    public void notifyMenuObserver(String action, String userId) {
        for (MenuObserver observer : observers) {
            observer.update(action, userId);
        }
    }
}
