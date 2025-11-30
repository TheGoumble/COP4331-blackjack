package controller;

import app.SceneRouter;
import model.User;
import observer.*;
import view.MenuPageView;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

/**
 * Menu controller using Observer pattern
 * 
 * @author Bridjet Walker, Javier Vargas, Group 12
 */
public class MenuController {

    private final MenuPageView view;
    private final SceneRouter router;
    private final MenuSubject menuSubject;
    private User currentUser;

    public MenuController(MenuPageView view, SceneRouter router, User existingUser) {
        this.view = view;
        this.router = router;
        this.menuSubject = new MenuSubject();
        this.currentUser = existingUser;

        // Register observers
        menuSubject.addMenuObserver(new CreateGameObserver(router));
        menuSubject.addMenuObserver(new JoinGameObserver(router));
        menuSubject.addMenuObserver(new TutorialObserver(router));
        menuSubject.addMenuObserver(new CloseAppObserver());

        // Ask for name only if not already set
        if (currentUser == null) {
            promptForName();
        }

        wireActions();
    }

    /**
     * Prompt for user name when menu first loads
     */
    private void promptForName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Name");
        dialog.setHeaderText("Welcome to Blackjack!");
        dialog.setContentText("Please enter your display name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String displayName = result.get().trim();
            String userId = "player_" + System.currentTimeMillis();
            currentUser = new User(userId, displayName);
        } else {
            // If user cancels, set a default guest user
            currentUser = new User("guest_" + System.currentTimeMillis(), "Guest");
        }
    }

    private void wireActions() {
        // Handle dropdown menu items
        view.singlePlayerItem.setOnAction(e -> {
            router.showSinglePlayerTable();
        });
        
        view.multiplayerItem.setOnAction(e -> {
            menuSubject.notifyMenuObserver("CREATE_GAME", currentUser.getUserId());
        });

        view.joinGameButton.setOnAction(e -> {
            menuSubject.notifyMenuObserver("JOIN_GAME", currentUser.getUserId());
        });

        view.tutorialButton.setOnAction(e -> {
            menuSubject.notifyMenuObserver("TUTORIAL", currentUser.getUserId());
        });

        view.closeButton.setOnAction(e -> {
            menuSubject.notifyMenuObserver("CLOSE_APP", currentUser.getUserId());
        });
    }

    public User getCurrentUser() {
        return currentUser;
    }
}