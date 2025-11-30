package view;

import model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Multiplayer table view showing all players and dealer
 * 
 * @author Javier Vargas, Luca Lombardo Group 12
 */
public class MultiplayerTableView extends BorderPane {

    private final Label dealerLabel = new Label("Dealer");
    private final TextArea dealerCardsArea = new TextArea();
    private final Label dealerTotalLabel = new Label("Total: 0");

    private final FlowPane playersBox = new FlowPane(15, 15);
    private final Map<String, PlayerPanel> playerPanels = new HashMap<>();

    private final TextField betField = new TextField();
    private final Button betButton = new Button("Place Bet");
    private final Button hitButton = new Button("Hit");
    private final Button standButton = new Button("Stand");
    private final Button startRoundButton = new Button("Start Round");
    private final Button backToMenuButton = new Button("Back to Menu");

    private final Label statusLabel = new Label("Waiting for players...");
    private final Label turnIndicatorLabel = new Label("");
    private final Label gameCodeLabel = new Label("");
    private final Label myBalanceLabel = new Label("Your Balance: $0");

    private String currentGameCode = "";
    
    private Consumer<Integer> onBet = amount -> {};
    private Runnable onHit = () -> {};
    private Runnable onStand = () -> {};
    private Runnable onStartRound = () -> {};
    private Runnable onBackToMenu = () -> {};

    public MultiplayerTableView() {
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #2d5016;"); // Green felt table

        // Dealer area at top
        VBox dealerBox = createDealerArea();
        setTop(dealerBox);

        // Players area in center - grid layout
        playersBox.setAlignment(Pos.CENTER);
        playersBox.setPadding(new Insets(20));
        playersBox.setStyle("-fx-background-color: transparent;");
        
        ScrollPane playersScroll = new ScrollPane(playersBox);
        playersScroll.setFitToWidth(true);
        playersScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        setCenter(playersScroll);

        // Controls at bottom
        VBox controls = createControlsArea();
        setBottom(controls);

        // Wire up button actions
        betButton.setOnAction(e -> handleBetClick());
        hitButton.setOnAction(e -> onHit.run());
        standButton.setOnAction(e -> onStand.run());
        startRoundButton.setOnAction(e -> onStartRound.run());
        backToMenuButton.setOnAction(e -> onBackToMenu.run());

        hitButton.setDisable(true);
        standButton.setDisable(true);
    }

    private VBox createDealerArea() {
        dealerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");

        dealerCardsArea.setEditable(false);
        dealerCardsArea.setPrefHeight(60);
        dealerCardsArea.setStyle(
            "-fx-control-inner-background: white; " +
            "-fx-background-color: white; " +
            "-fx-border-color: #cccccc; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-font-size: 14px;"
        );

        dealerTotalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");

        VBox box = new VBox(10, dealerLabel, dealerCardsArea, dealerTotalLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        );
        box.setMaxWidth(600);
        VBox wrapper = new VBox(box);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(15));
        return wrapper;
    }

    private VBox createControlsArea() {
        betField.setPromptText("Bet amount");
        betField.setPrefWidth(100);
        betField.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-padding: 8; " +
            "-fx-background-color: white; " +
            "-fx-border-color: #cccccc; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        );
        
        styleButton(betButton, "#2d5016");
        styleButton(hitButton, "#2d5016");
        styleButton(standButton, "#2d5016");
        styleButton(startRoundButton, "#2d5016");
        styleButton(backToMenuButton, "#8B0000");

        HBox actionButtons = new HBox(10, betField, betButton, hitButton, standButton, startRoundButton, backToMenuButton);
        actionButtons.setAlignment(Pos.CENTER);

        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
        turnIndicatorLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-font-family: 'Arial';");
        gameCodeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
        myBalanceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-font-family: 'Arial';");

        VBox box = new VBox(10, actionButtons, gameCodeLabel, turnIndicatorLabel, statusLabel, myBalanceLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        return box;
    }
    
    private void styleButton(Button button, String color) {
        button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 20 8 20; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: derive(" + color + ", 20%); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 20 8 20; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 3, 3);"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 20 8 20; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        ));
    }

    private void handleBetClick() {
        try {
            int amt = Integer.parseInt(betField.getText().trim());
            onBet.accept(amt);
            betField.clear();
        } catch (NumberFormatException ex) {
            showMessage("Invalid bet amount.");
        }
    }

    /**
     * Update dealer's display
     */
    public void updateDealer(List<Card> cards, boolean showAll) {
        StringBuilder sb = new StringBuilder();
        int total = 0;

        for (int i = 0; i < cards.size(); i++) {
            if (i == 1 && !showAll) {
                sb.append("[Hidden]");
            } else {
                Card card = cards.get(i);
                sb.append(card.toString());
                total += card.baseValue();
            }
            if (i < cards.size() - 1) sb.append(", ");
        }

        dealerCardsArea.setText(sb.toString());
        dealerTotalLabel.setText(showAll ? "Total: " + total : "Total: ?");
    }

    /**
     * Add or update a player panel
     */
    public void updatePlayer(String playerId, String displayName, List<Card> cards, int balance, boolean isActive) {
        PlayerPanel panel = playerPanels.get(playerId);
        if (panel == null) {
            panel = new PlayerPanel(playerId, displayName);
            playerPanels.put(playerId, panel);
            playersBox.getChildren().add(panel);
        }
        panel.update(cards, balance, isActive);
    }

    /**
     * Remove a player panel
     */
    public void removePlayer(String playerId) {
        PlayerPanel panel = playerPanels.remove(playerId);
        if (panel != null) {
            playersBox.getChildren().remove(panel);
        }
    }

    /**
     * Update my balance display
     */
    public void updateMyBalance(int balance) {
        myBalanceLabel.setText("Your Balance: $" + balance);
    }

    public void showMessage(String message) {
        statusLabel.setText(message);
    }

    public void setButtonsEnabled(boolean bet, boolean hit, boolean stand, boolean startRound) {
        betButton.setDisable(!bet);
        hitButton.setDisable(!hit);
        standButton.setDisable(!stand);
        startRoundButton.setDisable(!startRound);
    }

    public void setOnBet(Consumer<Integer> handler) {
        this.onBet = handler;
    }

    public void setOnHit(Runnable handler) {
        this.onHit = handler;
    }

    public void setOnStand(Runnable handler) {
        this.onStand = handler;
    }

    public void setOnStartRound(Runnable handler) {
        this.onStartRound = handler;
    }

    public void setOnBackToMenu(Runnable handler) {
        this.onBackToMenu = handler;
    }
    
    public void setConnectionInfo(String connectionString) {
        try {
            String[] parts = connectionString.split(":");
            if (parts.length == 2) {
                String localIP = parts[0];
                String port = parts[1];
                // Update display with current game code
                updateHostDisplay(localIP, port);
            } else {
                gameCodeLabel.setText("HOST | Connection: " + connectionString);
            }
        } catch (Exception e) {
            gameCodeLabel.setText("HOST | Port: " + connectionString);
        }
    }
    
    public void setGameCode(String gameCode) {
        this.currentGameCode = gameCode;
        // Don't overwrite full display, just update it
        if (gameCode.equals("Connected")) {
            gameCodeLabel.setText("CLIENT | Connected to game");
        } else {
            // This is a host with a game code - will be updated by setConnectionInfo
            updateHostDisplay(null, null);
        }
    }
    
    private void updateHostDisplay(String localIP, String port) {
        if (currentGameCode.isEmpty()) {
            gameCodeLabel.setText("HOST | Loading game code...");
        } else if (localIP != null && port != null) {
            gameCodeLabel.setText(String.format(
                "GAME CODE: %s | LAN: %s:%s | Internet: YourPublicIP:%s",
                currentGameCode, localIP, port, port
            ));
        } else {
            gameCodeLabel.setText("GAME CODE: " + currentGameCode);
        }
    }
    
    public void setTurnIndicator(String message) {
        turnIndicatorLabel.setText(message);
    }

    /**
     * Inner class for individual player display
     */
    private static class PlayerPanel extends VBox {
        private final Label nameLabel;
        private final Label cardsLabel;
        private final Label balanceLabel;
        private final Label totalLabel;

        public PlayerPanel(String playerId, String displayName) {
            super(6);

            nameLabel = new Label(displayName);
            nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
            nameLabel.setWrapText(true);
            nameLabel.setMaxWidth(160);
            nameLabel.setAlignment(Pos.CENTER);

            cardsLabel = new Label("No cards");
            cardsLabel.setWrapText(true);
            cardsLabel.setMaxWidth(160);
            cardsLabel.setMinHeight(55);
            cardsLabel.setMaxHeight(55);
            cardsLabel.setAlignment(Pos.CENTER);
            cardsLabel.setStyle(
                "-fx-background-color: #f9f9f9; " +
                "-fx-border-color: #cccccc; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 6px; " +
                "-fx-background-radius: 6px; " +
                "-fx-font-family: 'Arial'; " +
                "-fx-font-size: 11px; " +
                "-fx-padding: 5;"
            );

            totalLabel = new Label("Total: 0");
            totalLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
            totalLabel.setAlignment(Pos.CENTER);

            balanceLabel = new Label("$0");
            balanceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555; -fx-font-family: 'Arial';");
            balanceLabel.setAlignment(Pos.CENTER);

            getChildren().addAll(nameLabel, cardsLabel, totalLabel, balanceLabel);
            setAlignment(Pos.CENTER);
            setPadding(new Insets(12));
            setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #dddddd; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 2, 2);"
            );
            setMinWidth(180);
            setMaxWidth(180);
            setMinHeight(160);
            setMaxHeight(160);
        }

        public void update(List<Card> cards, int balance, boolean isActive) {
            StringBuilder sb = new StringBuilder();
            int total = 0;
            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);
                sb.append(card.toString());
                if (i < cards.size() - 1) sb.append(", ");
                total += card.baseValue();
            }
            
            if (cards.isEmpty()) {
                cardsLabel.setText("No cards");
            } else {
                cardsLabel.setText(sb.toString());
            }
            
            totalLabel.setText("Total: " + total);
            balanceLabel.setText("$" + balance);

            if (isActive) {
                setStyle(
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #FFD700; " +
                    "-fx-border-width: 3px; " +
                    "-fx-border-radius: 10; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.5), 10, 0, 0, 0);"
                );
                String baseName = nameLabel.getText().split(" \\[")[0];
                nameLabel.setText(baseName + " [TURN]");
                nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-font-family: 'Arial';");
            } else {
                setStyle(
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: #dddddd; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 10; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 2, 2);"
                );
                String baseName = nameLabel.getText().split(" \\[")[0];
                nameLabel.setText(baseName);
                nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2d5016; -fx-font-family: 'Arial';");
            }
        }
    }
}
