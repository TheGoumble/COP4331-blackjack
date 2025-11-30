package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
/**
 *
 * @author Bridjet Walker, Luca Lombardo
 */
public class MenuPageView extends BorderPane {

    public final MenuButton createGameButton = new MenuButton("🎮 Create Blackjack Game");
    public final MenuItem singlePlayerItem = new MenuItem("Single Player");
    public final MenuItem multiplayerItem = new MenuItem("Multiplayer");
    public final Button joinGameButton = new Button("Join Game");
    public final Button tutorialButton = new Button("Tutorial");
    public final Button closeButton = new Button("Close Application");

    public MenuPageView() {
        setStyle("-fx-background-color: #2d5016;");

        // Title Section
        Label title = new Label("♠ BLACKJACK ♥");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
        
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 15, 0));

        // Decorative Cards at top
        HBox topCards = createDecorativeCards();
        topCards.setAlignment(Pos.CENTER);
        topCards.setPadding(new Insets(15, 0, 20, 0));

        // Menu Buttons Container
        VBox buttonContainer = new VBox(14);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(30, 40, 30, 40));
        buttonContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");
        buttonContainer.setMaxWidth(450);
        buttonContainer.setMinWidth(380);

        // Setup create game dropdown
        createGameButton.getItems().addAll(singlePlayerItem, multiplayerItem);
        styleMenuButton(createGameButton, "#2d5016");
        
        // Style other buttons
        styleMenuButton(joinGameButton, "#2d5016", "👥");
        styleMenuButton(tutorialButton, "#2d5016", "📖");
        styleMenuButton(closeButton, "#8B0000", "❌");

        buttonContainer.getChildren().addAll(
                createGameButton,
                joinGameButton,
                tutorialButton,
                closeButton
        );

        // Main content
        VBox mainContent = new VBox(12, titleBox, topCards, buttonContainer);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(30, 20, 30, 20));
        
        // Wrap in ScrollPane
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #2d5016; -fx-background-color: #2d5016;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        setCenter(scrollPane);
    }

    // Overloaded for MenuButton (dropdown)
    private void styleMenuButton(MenuButton button, String color) {
        button.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12 35 12 35; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        button.setMinWidth(350);
        button.setMaxWidth(350);
        
        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: derive(" + color + ", 20%); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12 35 12 35; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 3, 3); " +
            "-fx-scale-x: 1.03; " +
            "-fx-scale-y: 1.03;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12 35 12 35; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        ));
    }
    
    private void styleMenuButton(Button button, String color, String emoji) {
        button.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12 35 12 35; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        button.setText(emoji + " " + button.getText());
        button.setMinWidth(350);
        button.setMaxWidth(350);
        
        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: derive(" + color + ", 20%); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12 35 12 35; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 3, 3); " +
            "-fx-scale-x: 1.03; " +
            "-fx-scale-y: 1.03;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12 35 12 35; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        ));
    }

    private HBox createDecorativeCards() {
        HBox cardRow = new HBox(20);
        cardRow.setAlignment(Pos.CENTER);
        
        VBox cardA = createCardSymbol("♠", "A", "black");
        VBox cardK = createCardSymbol("♥", "K", "red");
        VBox cardQ = createCardSymbol("♦", "Q", "red");
        VBox cardJ = createCardSymbol("♣", "J", "black");
        
        cardRow.getChildren().addAll(cardA, cardK, cardQ, cardJ);
        return cardRow;
    }

    private VBox createCardSymbol(String symbol, String value, String color) {
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + color + "; " +
            "-fx-font-family: 'Arial';"
        );
        valueLabel.setAlignment(Pos.CENTER);
        
        Label symbolLabel = new Label(symbol);
        symbolLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-text-fill: " + color + ";"
        );
        symbolLabel.setAlignment(Pos.CENTER);
        
        VBox cardContent = new VBox(3, valueLabel, symbolLabel);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setStyle(
            "-fx-border-color: #333333; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 6; " +
            "-fx-background-color: white; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 8 12 8 12; " +
            "-fx-min-width: 55px; " +
            "-fx-min-height: 80px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 4, 0, 2, 2);"
        );
        
        return cardContent;
    }
}