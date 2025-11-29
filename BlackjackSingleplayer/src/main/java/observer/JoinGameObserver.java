package observer;

import app.SceneRouter;

/**
 * Observer for JoinGame button
 * Shows the game lobby where players can browse and join sessions
 * 
 * @author Javier Vargas, Group 12
 */
public class JoinGameObserver implements MenuObserver {
    private final SceneRouter router;
    
    public JoinGameObserver(SceneRouter router) {
        this.router = router;
    }
    
    @Override
    public void update(String action, String userId) {
        if ("JOIN_GAME".equals(action)) {
            // Navigate to game lobby
            router.showGameLobby(userId);
        }
    }
}
