package controller;

import app.SceneRouter;
import view.MenuPageView;
/**
 *
 * @author Bridjet Walker
 */
public class MenuController {
    public MenuController(MenuPageView view, SceneRouter router){
        view.hostBtn.setOnAction(e -> router.goHost());
        view.joinBtn.setOnAction(e -> router.goJoin());
    }
}