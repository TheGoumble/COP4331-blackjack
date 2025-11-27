package controller;

import app.SceneRouter;
import view.TutorialView;
/**
 *
 * @author Bridjet Walker
 */
public class TutorialController {

    private final TutorialView view;
    private final SceneRouter router;

    public TutorialController(TutorialView view, SceneRouter router) {
        this.view = view;
        this.router = router;

        wireActions();
    }

    private void wireActions() {
        view.exitButton.setOnAction(e -> router.showMenu());
    }
}