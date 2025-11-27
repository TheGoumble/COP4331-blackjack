package controller;

import app.SceneRouter;
import view.MenuPageView;
/**
 *
 * @author Bridjet Walker
 */
public class MenuController {

    private final MenuPageView view;
    private final SceneRouter router;

    public MenuController(MenuPageView view, SceneRouter router) {
        this.view = view;
        this.router = router;

        wireActions();
    }

    private void wireActions() {
        view.createGameButton.setOnAction(e -> router.showSinglePlayerTable());

        // For now, Join does the same as Create (single-player only).
        view.joinGameButton.setOnAction(e -> router.showSinglePlayerTable());

        view.tutorialButton.setOnAction(e -> router.showTutorial());

        view.closeButton.setOnAction(e -> router.closeApp());
    }
}