package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Lobby view for browsing and joining game sessions
 * 
 * @author Javier Vargas, Luca Lombardo Group 12
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
        setStyle("-fx-background-color: #2d5016;");

        // Title
        titleLabel.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
        
        VBox topSection = new VBox(10, titleLabel);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(30, 0, 20, 0));
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
        gamesLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");

        gamesListView.setPrefHeight(250);
        gamesListView.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #cccccc; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px;"
        );

        HBox buttonsRow = new HBox(15, joinSelectedButton, refreshButton);
        buttonsRow.setAlignment(Pos.CENTER);
        buttonsRow.setPadding(new Insets(15, 0, 0, 0));

        VBox contentBox = new VBox(15, gamesLabel, gamesListView, buttonsRow);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30, 40, 30, 40));
        contentBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);"
        );
        contentBox.setMaxWidth(700);
        
        VBox wrapper = new VBox(contentBox);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(20));
        
        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #2d5016; -fx-background-color: #2d5016;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        VBox box = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return box;
    }

    private VBox createBottomSection() {
        Label instructionLabel = new Label("Games are discovered via online matchmaking");
        instructionLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-style: italic;");

        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-font-family: 'Arial';");

        // Direct connect section for internet play
        Label orLabel = new Label("OR Enter Game Code / IP:Port");
        orLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
        
        directConnectField.setPromptText("Game Code (e.g., ABC123) or IP:Port (e.g., 203.0.113.5:54321)");
        directConnectField.setPrefWidth(400);
        directConnectField.setStyle(
            "-fx-background-color: white; " +
            "-fx-text-fill: black; " +
            "-fx-prompt-text-fill: gray; " +
            "-fx-border-color: #cccccc; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 8;"
        );
        
        HBox directConnectBox = new HBox(10, directConnectField, directConnectButton);
        directConnectBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(12, instructionLabel, statusLabel, orLabel, directConnectBox, backButton);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20, 20, 30, 20));

        // Style buttons
        styleButton(joinSelectedButton, "#2d5016");
        styleButton(refreshButton, "#2d5016");
        styleButton(directConnectButton, "#2d5016");
        styleButton(backButton, "#8B0000");

        return box;
    }

    private void styleButton(Button button, String color) {
        button.setStyle(
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 25 10 25; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: derive(" + color + ", 20%); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 25 10 25; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 3, 3); " +
            "-fx-scale-x: 1.03; " +
            "-fx-scale-y: 1.03;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 25 10 25; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        ));
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
