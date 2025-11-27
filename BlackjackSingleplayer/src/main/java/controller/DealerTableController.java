package controller;

import model.ActiveGame;
import view.DealerTableView;
/**
 *
 * @author Bridjet Walker
 */
public class DealerTableController {

    private final ActiveGame game;
    private final DealerTableView view;

    public DealerTableController(ActiveGame game, DealerTableView view) {
        this.game = game;
        this.view = view;

        // Wire up handlers
        view.setOnBet(this::handleBet);
        view.setOnHit(this::handleHit);
        view.setOnStand(this::handleStand);

        // initial render
        view.render(game);
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