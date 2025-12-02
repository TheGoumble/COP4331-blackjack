package controller;

import app.SceneRouter;
import model.ActiveGame;
import view.DealerTableView;
/**
 * MVC PATTERN: This is a Controller (handles game logic and user actions)
 *
 * @author Bridjet Walker
 */
public class DealerTableController {

    private final ActiveGame game;
    private final DealerTableView view;
    private final SceneRouter router;

    public DealerTableController(ActiveGame game, DealerTableView view, SceneRouter router) {
        this.game = game;
        this.view = view;
        this.router = router;

        // Wire up handlers
        view.setOnBet(this::handleBet);
        view.setOnHit(this::handleHit);
        view.setOnStand(this::handleStand);
        view.setOnBackToMenu(this::handleBackToMenu);

        // initial render
        view.render(game);
    }
    
    private void handleBackToMenu() {
        // Navigate back to menu
        router.showMenu();
    }

    private void handleBet(int amount) {
        try {
            game.startNewRound(amount);
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showMessage(e.getMessage());
        }
        view.render(game);
    }

    private void handleHit() {
        game.playerHit();
        view.render(game);
    }

    private void handleStand() {
        game.playerStand();
        view.render(game);
    }
}