package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Lobby view for browsing and joining game sessions
 * 
 * @author Javier Vargas, Group 12
 */
public class GameLobbyView extends BorderPane {

    private final Label titleLabel = new Label("Game Lobby");
    private final ListView<String> gamesListView = new ListView<>();
    private final TextField directConnectField = new TextField();
    private final Button joinSelectedButton = new Button("Join Selected Game");
    private final Button refreshButton = new Button("Refresh List");
    private final Button directConnectButton = new Button("Direct Connect");
    private final Button backButton = new Button("Back to Menu");
    private final Label statusLabel = new Label("Searching for games on local network...");
    
    private Consumer<String> onJoinSession = sessionId -> {};
    private Runnable onRefresh = () -> {};
    private Runnable onBack = () -> {};

    public GameLobbyView() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");

        // Title
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);
        
        VBox topSection = new VBox(10, titleLabel);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(0, 0, 20, 0));
        setTop(topSection);

        // Center - Active games list
        VBox centerSection = createCenterSection();
        setCenter(centerSection);

        // Bottom - Status and back button
        VBox bottomSection = createBottomSection();
        setBottom(bottomSection);

        // Wire up actions
        joinSelectedButton.setOnAction(e -> handleJoinSelected());
        refreshButton.setOnAction(e -> onRefresh.run());
        directConnectButton.setOnAction(e -> handleDirectConnect());
        backButton.setOnAction(e -> onBack.run());
        
        // Double-click to join
        gamesListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                handleJoinSelected();
            }
        });
    }

    private VBox createCenterSection() {
        Label gamesLabel = new Label("Available Games on Local Network:");
        gamesLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gamesLabel.setTextFill(Color.WHITE);

        gamesListView.setPrefHeight(300);
        gamesListView.setStyle(
            "-fx-background-color: #0f3460; " +
            "-fx-border-color: #e94560; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5;"
        );

        HBox buttonsRow = new HBox(10, joinSelectedButton, refreshButton);
        buttonsRow.setAlignment(Pos.CENTER);

        VBox box = new VBox(10, gamesLabel, gamesListView, buttonsRow);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        return box;
    }

    private VBox createBottomSection() {
        Label instructionLabel = new Label("Games are discovered via online matchmaking");
        instructionLabel.setFont(Font.font("Arial", 12));
        instructionLabel.setTextFill(Color.LIGHTGRAY);

        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statusLabel.setTextFill(Color.YELLOW);

        // Direct connect section for internet play
        Label orLabel = new Label("- OR Enter Game Code / IP:Port -");
        orLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        orLabel.setTextFill(Color.LIGHTGRAY);
        
        directConnectField.setPromptText("Game Code (e.g., ABC123) or IP:Port (e.g., 203.0.113.5:54321)");
        directConnectField.setPrefWidth(450);
        directConnectField.setStyle(
            "-fx-background-color: #0f3460; " +
            "-fx-text-fill: white; " +
            "-fx-prompt-text-fill: gray; " +
            "-fx-border-color: #e94560; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5;"
        );
        
        HBox directConnectBox = new HBox(10, directConnectField, directConnectButton);
        directConnectBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15, instructionLabel, statusLabel, orLabel, directConnectBox, backButton);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20, 0, 0, 0));

        // Style buttons
        styleButton(joinSelectedButton, "#e94560");
        styleButton(refreshButton, "#0f3460");
        styleButton(directConnectButton, "#e94560");
        styleButton(backButton, "#533483");

        return box;
    }

    private void styleButton(Button button, String color) {
        button.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setOpacity(0.8));
        button.setOnMouseExited(e -> button.setOpacity(1.0));
    }

    private void handleJoinSelected() {
        String selected = gamesListView.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.equals("No games found - refresh to check for new games") && !selected.equals("Searching...")) {
            // Extract connection string from display format
            String connectionString = extractConnectionString(selected);
            if (connectionString != null) {
                onJoinSession.accept(connectionString);
            }
        } else {
            setStatus("Please select a game from the list");
        }
    }
    
    private void handleDirectConnect() {
        String address = directConnectField.getText().trim().toUpperCase();
        if (!address.isEmpty()) {
            onJoinSession.accept(address);
            directConnectField.clear();
        } else {
            setStatus("Please enter game code or host address");
        }
    }
    
    private String extractConnectionString(String listItem) {
        // Extract from format: "Game: game_123... - Host: player_123 - Address: 192.168.1.100:54321"
        if (listItem.contains("Address: ")) {
            int start = listItem.indexOf("Address: ") + 9;
            return listItem.substring(start).trim();
        }
        return null;
    }

    /**
     * Update the list of available games
     */
    public void updateGamesList(Map<String, String> games) {
        gamesListView.getItems().clear();
        
        if (games.isEmpty()) {
            gamesListView.getItems().add("No games found - refresh to check for new games");
            setStatus("No games discovered yet. Make sure host has created a game.");
        } else {
            for (Map.Entry<String, String> entry : games.entrySet()) {
                gamesListView.getItems().add(entry.getValue());
            }
            setStatus(games.size() + " game(s) available - double-click to join");
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
