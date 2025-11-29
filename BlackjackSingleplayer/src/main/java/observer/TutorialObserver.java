package observer;

import app.SceneRouter;

/**
 * Observer for Tutorial button
 * Shows the tutorial screen
 * 
 * @author Javier Vargas, Group 12
 */
public class TutorialObserver implements MenuObserver {
    private final SceneRouter router;
    
    public TutorialObserver(SceneRouter router) {
        this.router = router;
    }
    
    @Override
    public void update(String action, String userId) {
        if ("TUTORIAL".equals(action)) {
            router.showTutorial();
        }
    }
}
