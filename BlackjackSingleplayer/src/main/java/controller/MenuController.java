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
     * Required - keeps prompting until valid name is entered
     */
    private void promptForName() {
        String displayName = null;
        
        while (displayName == null || displayName.trim().isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Enter Name - Required");
            dialog.setHeaderText("Welcome to Blackjack!");
            dialog.setContentText("Please enter your display name:");
            
            // Remove the cancel button to force input
            dialog.getDialogPane().lookupButton(javafx.scene.control.ButtonType.CANCEL).setVisible(false);

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                displayName = result.get().trim();
            } else {
                // Show warning if empty
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Name Required");
                alert.setHeaderText("You must enter a name to continue");
                alert.setContentText("Please enter your display name to play Blackjack.");
                alert.showAndWait();
            }
        }
        
        String userId = "player_" + System.currentTimeMillis();
        currentUser = new User(userId, displayName);
    }

    private void wireActions() {
        // Handle dropdown menu items
        view.singlePlayerItem.setOnAction(e -> {
            router.showSinglePlayerTable();
        });
        
        view.multiplayerItem.setOnAction(e -> {
            menuSubject.notifyMenuObserver("CREATE_GAME", currentUser.getUserId());
            router.setCurrentUser(currentUser); // Pass user to router
        });

        view.joinGameButton.setOnAction(e -> {
            menuSubject.notifyMenuObserver("JOIN_GAME", currentUser.getUserId());
            router.setCurrentUser(currentUser); // Pass user to router
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