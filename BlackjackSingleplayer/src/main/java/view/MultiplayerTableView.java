package view;

import model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Multiplayer table view showing all players and dealer
 * 
 * @author Javier Vargas, Group 12
 */
public class MultiplayerTableView extends BorderPane {

    private final Label dealerLabel = new Label("Dealer");
    private final HBox dealerCardsBox = new HBox(10);
    private final Label dealerTotalLabel = new Label("Total: 0");

    private final VBox playersBox = new VBox(10);
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

        // Players area in center
        ScrollPane playersScroll = new ScrollPane(playersBox);
        playersScroll.setFitToWidth(true);
        playersScroll.setStyle("-fx-background: transparent;");
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
        dealerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        dealerLabel.setTextFill(Color.WHITE);

        dealerCardsBox.setAlignment(Pos.CENTER);
        dealerCardsBox.setStyle("-fx-padding: 10;");

        dealerTotalLabel.setTextFill(Color.WHITE);

        VBox box = new VBox(10, dealerLabel, dealerCardsBox, dealerTotalLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #1a3d0a; -fx-background-radius: 10;");
        return box;
    }

    private VBox createControlsArea() {
        betField.setPromptText("Bet amount");
        betField.setPrefWidth(100);

        HBox actionButtons = new HBox(10, betField, betButton, hitButton, standButton, startRoundButton, backToMenuButton);
        actionButtons.setAlignment(Pos.CENTER);

        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        turnIndicatorLabel.setTextFill(Color.LIME);
        turnIndicatorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        gameCodeLabel.setTextFill(Color.LIGHTBLUE);
        gameCodeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        myBalanceLabel.setTextFill(Color.YELLOW);
        myBalanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        VBox box = new VBox(10, actionButtons, gameCodeLabel, turnIndicatorLabel, statusLabel, myBalanceLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        return box;
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
        dealerCardsBox.getChildren().clear();
        int total = 0;

        for (int i = 0; i < cards.size(); i++) {
            if (i == 1 && !showAll) {
                dealerCardsBox.getChildren().add(createHiddenCard());
            } else {
                Card card = cards.get(i);
                dealerCardsBox.getChildren().add(createCardSymbol(
                    card.suit().symbol(),
                    card.rank().symbol(),
                    card.suit().isRed() ? "red" : "black"
                ));
                total += card.baseValue();
            }
        }

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
     * Creates a visual card representation
     */
    private VBox createCardSymbol(String symbol, String value, String color) {
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + color + ";"
        );
        valueLabel.setAlignment(Pos.CENTER);
        
        Label symbolLabel = new Label(symbol);
        symbolLabel.setStyle(
            "-fx-font-size: 30px; " +
            "-fx-text-fill: " + color + ";"
        );
        symbolLabel.setAlignment(Pos.CENTER);
        
        VBox cardContent = new VBox(2, valueLabel, symbolLabel);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setStyle(
            "-fx-border-color: #333333; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 6; " +
            "-fx-background-color: white; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-min-width: 55px; " +
            "-fx-min-height: 80px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 4, 0, 2, 2);"
        );
        
        return cardContent;
    }

    /**
     * Creates a hidden (face-down) card
     */
    private VBox createHiddenCard() {
        Label hiddenLabel = new Label("?");
        hiddenLabel.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white;"
        );
        hiddenLabel.setAlignment(Pos.CENTER);
        
        VBox cardContent = new VBox(hiddenLabel);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setStyle(
            "-fx-border-color: #333333; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 6; " +
            "-fx-background-color: #4169E1; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-min-width: 55px; " +
            "-fx-min-height: 80px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 4, 0, 2, 2);"
        );
        
        return cardContent;
    }

    /**
     * Inner class for individual player display
     */
    private class PlayerPanel extends VBox {
        private final Label nameLabel;
        private final HBox cardsBox;
        private final Label balanceLabel;
        private final Label totalLabel;

        public PlayerPanel(String playerId, String displayName) {
            super(10);

            nameLabel = new Label(displayName);
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            nameLabel.setTextFill(Color.WHITE);

            cardsBox = new HBox(8);
            cardsBox.setAlignment(Pos.CENTER);
            cardsBox.setStyle("-fx-padding: 5;");

            totalLabel = new Label("Total: 0");
            totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            totalLabel.setTextFill(Color.LIGHTGREEN);

            balanceLabel = new Label("Balance: $0");
            balanceLabel.setTextFill(Color.WHITE);

            getChildren().addAll(nameLabel, cardsBox, totalLabel, balanceLabel);
            setAlignment(Pos.CENTER);
            setPadding(new Insets(10));
            setStyle("-fx-background-color: #3d5d2a; -fx-background-radius: 5; -fx-border-color: #ffffff; -fx-border-radius: 5;");
        }

        public void update(List<Card> cards, int balance, boolean isActive) {
            cardsBox.getChildren().clear();
            int total = 0;
            for (Card card : cards) {
                cardsBox.getChildren().add(createCardSymbol(
                    card.suit().symbol(),
                    card.rank().symbol(),
                    card.suit().isRed() ? "red" : "black"
                ));
                total += card.baseValue();
            }
            totalLabel.setText("Total: " + total);
            balanceLabel.setText("Balance: $" + balance);

            if (isActive) {
                setStyle("-fx-background-color: #4d6d3a; -fx-background-radius: 5; -fx-border-color: yellow; -fx-border-width: 3; -fx-border-radius: 5;");
                String baseName = nameLabel.getText().split(" ")[0];
                nameLabel.setText(baseName + " [YOUR TURN]");
                nameLabel.setTextFill(Color.YELLOW);
            } else {
                setStyle("-fx-background-color: #3d5d2a; -fx-background-radius: 5; -fx-border-color: #ffffff; -fx-border-radius: 5;");
                String baseName = nameLabel.getText().split(" ")[0];
                nameLabel.setText(baseName);
                nameLabel.setTextFill(Color.WHITE);
            }
        }
    }
}
