package observer;

import javafx.application.Platform;

/**
 * Observer for CloseApp button
 * Closes the application
 * 
 * OBSERVER PATTERN: This is a Concrete Observer
 * 
 * @author Javier Vargas, Group 12
 */
public class CloseAppObserver implements MenuObserver {
    
    @Override
    public void update(String action, String userId) {
        if ("CLOSE_APP".equals(action)) {
            Platform.exit();
            System.exit(0);
        }
    }
}
