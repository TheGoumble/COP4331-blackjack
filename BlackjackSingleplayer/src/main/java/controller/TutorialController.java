package controller;

import app.SceneRouter;
import view.TutorialView;
/**
 * MVC PATTERN: This is a Controller (handles tutorial navigation)
 *
 * @author Bridjet Walker, Luca Lombardo
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
        view.nextButton.setOnAction(e -> view.nextSlide());
        view.prevButton.setOnAction(e -> view.previousSlide());
    }
}