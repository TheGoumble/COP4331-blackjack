package controller;

import model.ActiveGame;
import model.User;
import view.DealerTableView;
/**
 *
 * @author Bridjet Walker
 */
public class DealerTableController {
    public DealerTableController(DealerTableView view, ActiveGame game, User me){
        view.setStatus("Welcome, " + me.name() + ". Players seated: " + game.players().size());
    }
}