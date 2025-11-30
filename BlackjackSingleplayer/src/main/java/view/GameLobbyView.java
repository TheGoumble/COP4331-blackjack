package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Lobby view for browsing and joining game sessions
 * 
 * @author Javier Vargas, Luca Lombardo Group 12
 */
public class GameLobbyView extends BorderPane {

    private final Label titleLabel = new Label("Lobby");
    private final FlowPane gamesGridPane = new FlowPane();
    private final TextField directConnectField = new TextField();
    private final Button refreshButton = new Button("Refresh");
    private final Button directConnectButton = new Button("Connect");
    private final Button backButton = new Button("Back to Menu");
    private final Label statusLabel = new Label("Loading available games...");
    private final Map<String, VBox> gameCards = new HashMap<>();
    
    private Consumer<String> onJoinSession = sessionId -> {};
    private Runnable onRefresh = () -> {};
    private Runnable onBack = () -> {};

    public GameLobbyView() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #1a3d0f, #2d5016);");

        // Title section with refresh button
        titleLabel.setStyle(
            "-fx-font-size: 36px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 6, 0, 0, 2);"
        );
        
        styleButton(refreshButton, "#2d5016");
        refreshButton.setPrefWidth(120);
        
        HBox titleBox = new HBox(15, titleLabel, refreshButton);
        titleBox.setAlignment(Pos.CENTER);
        
        VBox topSection = new VBox(10, titleBox, statusLabel);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(15, 0, 10, 0));
        setTop(topSection);

        // Center - Games grid
        VBox centerSection = createCenterSection();
        setCenter(centerSection);

        // Bottom - Direct connect and back button
        VBox bottomSection = createBottomSection();
        setBottom(bottomSection);

        // Wire up actions
        refreshButton.setOnAction(e -> onRefresh.run());
        directConnectButton.setOnAction(e -> handleDirectConnect());
        backButton.setOnAction(e -> onBack.run());
    }

    private VBox createCenterSection() {
        Label gamesLabel = new Label("Available Games");
        gamesLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 4, 0, 0, 2);"
        );

        // Games grid setup
        gamesGridPane.setHgap(15);
        gamesGridPane.setVgap(15);
        gamesGridPane.setAlignment(Pos.CENTER);
        gamesGridPane.setPadding(new Insets(15));
        gamesGridPane.setPrefWrapLength(900); // Wrap after ~900px width
        gamesGridPane.setStyle(
            "-fx-background-color: rgba(0,0,0,0.3); " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 18;"
        );

        VBox contentBox = new VBox(12, gamesLabel, gamesGridPane);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(10, 20, 10, 20));
        
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        VBox box = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return box;
    }

    private VBox createBottomSection() {
        statusLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 3, 0, 0, 1);"
        );

        // Direct connect section
        Label orLabel = new Label("━━━━━  Direct Connect  ━━━━━");
        orLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial';"
        );
        
        directConnectField.setPromptText("Game Code (ABC123) or IP:Port (192.168.1.5:54321)");
        directConnectField.setPrefWidth(400);
        directConnectField.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-background-color: white; " +
            "-fx-text-fill: black; " +
            "-fx-prompt-text-fill: #888888; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 10;"
        );
        
        styleButton(directConnectButton, "#2d5016");
        directConnectButton.setPrefWidth(110);
        
        HBox directConnectBox = new HBox(10, directConnectField, directConnectButton);
        directConnectBox.setAlignment(Pos.CENTER);
        directConnectBox.setPadding(new Insets(8));
        directConnectBox.setStyle(
            "-fx-background-color: rgba(0,0,0,0.4); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 12;"
        );

        styleButton(backButton, "#8B0000");
        backButton.setPrefWidth(160);

        VBox box = new VBox(10, orLabel, directConnectBox, backButton);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 20, 18, 20));

        return box;
    }

    private void styleButton(Button button, String color) {
        String baseStyle = 
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);";
        
        String hoverStyle = 
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: derive(" + color + ", 25%); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.7), 8, 0, 3, 3); " +
            "-fx-scale-x: 1.03; " +
            "-fx-scale-y: 1.03;";
        
        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }
    
    private VBox createGameCard(String gameCode, String displayName) {
        // Host name label
        Label hostLabel = new Label(displayName);
        hostLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #2d5016; " +
            "-fx-font-family: 'Arial';"
        );
        hostLabel.setWrapText(true);
        hostLabel.setMaxWidth(200);
        hostLabel.setAlignment(Pos.CENTER);
        
        // Game type icon/label
        Label gameTypeLabel = new Label("Blackjack");
        gameTypeLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #8B0000; " +
            "-fx-font-family: 'Arial';"
        );
        
        // Game code label
        Label codeLabel = new Label("Code: " + gameCode);
        codeLabel.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Courier New'; " +
            "-fx-background-color: #2d5016; " +
            "-fx-background-radius: 5; " +
            "-fx-padding: 5 10 5 10;"
        );
        
        // Join button
        Button joinButton = new Button("Join Game");
        joinButton.setPrefWidth(160);
        joinButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #2d5016; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 9 18 9 18; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 2, 2);"
        );
        
        joinButton.setOnMouseEntered(e -> joinButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: derive(#2d5016, 30%); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 9 18 9 18; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 8, 0, 3, 3); " +
            "-fx-scale-x: 1.03; " +
            "-fx-scale-y: 1.03;"
        ));
        
        joinButton.setOnMouseExited(e -> joinButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #2d5016; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 9 18 9 18; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 2, 2);"
        ));
        
        joinButton.setOnAction(e -> onJoinSession.accept(gameCode));
        
        // Card container
        VBox card = new VBox(12, hostLabel, gameTypeLabel, codeLabel, joinButton);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16));
        card.setPrefWidth(220);
        card.setPrefHeight(180);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 4);"
        );
        
        // Hover effect for card
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #f9f9f9; " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 4; " +
            "-fx-border-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.6), 12, 0, 0, 5); " +
            "-fx-scale-x: 1.02; " +
            "-fx-scale-y: 1.02;"
        ));
        
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 12; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 4);"
        ));
        
        return card;
    }

    private void handleDirectConnect() {
        String address = directConnectField.getText().trim().toUpperCase();
        if (!address.isEmpty()) {
            onJoinSession.accept(address);
            directConnectField.clear();
        } else {
            setStatus("Please enter a game code or host address");
        }
    }

    /**
     * Update the grid of available games
     */
    public void updateGamesList(Map<String, String> games) {
        gamesGridPane.getChildren().clear();
        gameCards.clear();
        
        if (games.isEmpty()) {
            // Show "no games" message
            Label noGamesLabel = new Label("No games available");
            noGamesLabel.setStyle(
                "-fx-font-size: 20px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #FFD700; " +
                "-fx-font-family: 'Arial';"
            );
            
            Label instructionLabel = new Label("Create a game or refresh to check for new games");
            instructionLabel.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Arial';"
            );
            
            VBox emptyBox = new VBox(10, noGamesLabel, instructionLabel);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(50));
            
            gamesGridPane.getChildren().add(emptyBox);
            setStatus("No games found - Click refresh to search again");
        } else {
            for (Map.Entry<String, String> entry : games.entrySet()) {
                String gameCode = entry.getKey();
                String displayText = entry.getValue();
                
                // Extract host name from format: "Name's Blackjack Game (Code: ABC123)"
                String hostName = displayText;
                if (displayText.contains("(Code:")) {
                    hostName = displayText.substring(0, displayText.indexOf("(Code:")).trim();
                }
                
                VBox gameCard = createGameCard(gameCode, hostName);
                gameCards.put(gameCode, gameCard);
                gamesGridPane.getChildren().add(gameCard);
            }
            setStatus(games.size() + " game(s) available - Click a card to join");
        }
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    public void setOnJoinSession(Consumer<String> handler) {
        this.onJoinSession = handler;
    }

    public void setOnRefresh(Runnable handler) {
        this.onRefresh = handler;
    }

    public void setOnBack(Runnable handler) {
        this.onBack = handler;
    }
}
