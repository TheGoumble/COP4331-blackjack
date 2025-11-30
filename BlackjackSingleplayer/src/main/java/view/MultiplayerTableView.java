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
 * Multiplayer table view with chip-based betting and oval table
 * 
 * @author Javier Vargas, Luca Lombardo Group 12
 */
public class MultiplayerTableView extends BorderPane {

    // Dealer components
    private final Label dealerLabel = new Label("Dealer");
    private final HBox dealerCardsBox = new HBox(6);
    private final Label dealerTotalLabel = new Label("Total: 0");

    // Player tracking
    private final HBox playersContainer = new HBox(20);
    private final Map<String, PlayerPanel> playerPanels = new HashMap<>();

    // Chip betting components
    private final Label currentBetLabel = new Label("Current Bet: $0");
    private int currentBetAmount = 0;
    
    // Action buttons
    private final Button betButton = new Button("Place Bet");
    private final Button clearBetButton = new Button("Clear Bet");
    private final Button hitButton = new Button("Hit");
    private final Button standButton = new Button("Stand");
    private final Button startRoundButton = new Button("Start Round");
    private final Button backToMenuButton = new Button("Back to Menu");

    // Status labels
    private final Label statusLabel = new Label("Waiting for players...");
    private final Label turnIndicatorLabel = new Label("");
    private final Label gameCodeLabel = new Label("");
    private final Label myBalanceLabel = new Label("Your Balance: $0");

    private String currentGameCode = "";
    
    // Handlers
    private Consumer<Integer> onBet = amount -> {};
    private Runnable onHit = () -> {};
    private Runnable onStand = () -> {};
    private Runnable onStartRound = () -> {};
    private Runnable onBackToMenu = () -> {};

    public MultiplayerTableView() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #2d5016;");

        // Create oval table
        VBox tableContent = createTableContent();
        
        StackPane tableArea = new StackPane();
        tableArea.setStyle(
            "-fx-background-color: #1a3d0f; " +
            "-fx-background-radius: 350px / 220px; " +
            "-fx-border-color: #8B4513; " +
            "-fx-border-width: 8; " +
            "-fx-border-radius: 350px / 220px; " +
            "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5);"
        );
        tableArea.setMinSize(850, 550);
        tableArea.setMaxSize(850, 550);
        tableArea.getChildren().add(tableContent);
        
        StackPane tableContainer = new StackPane(tableArea);
        tableContainer.setAlignment(Pos.CENTER);
        tableContainer.setPadding(new Insets(10));
        
        setCenter(tableContainer);

        // Betting controls on left
        VBox bettingPanel = createBettingPanel();
        setLeft(bettingPanel);

        // Info panel on right
        VBox infoPanel = createInfoPanel();
        setRight(infoPanel);

        // Wire up actions
        hitButton.setOnAction(e -> onHit.run());
        standButton.setOnAction(e -> onStand.run());
        startRoundButton.setOnAction(e -> onStartRound.run());
        backToMenuButton.setOnAction(e -> onBackToMenu.run());
        betButton.setOnAction(e -> handleBetClick());
        clearBetButton.setOnAction(e -> clearBet());

        hitButton.setDisable(true);
        standButton.setDisable(true);
    }

    private VBox createTableContent() {
        // Dealer at top
        dealerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
        dealerCardsBox.setAlignment(Pos.CENTER);
        dealerTotalLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-family: 'Arial';");
        
        VBox dealerBox = new VBox(5, dealerLabel, dealerCardsBox, dealerTotalLabel);
        dealerBox.setAlignment(Pos.CENTER);
        dealerBox.setPadding(new Insets(10));

        // Players container at bottom
        playersContainer.setAlignment(Pos.CENTER);
        playersContainer.setPadding(new Insets(15));

        // Center content
        VBox content = new VBox(25, dealerBox, playersContainer);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(10));
        
        return content;
    }

    private VBox createBettingPanel() {
        Label betLabel = new Label("BETTING");
        betLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial';"
        );
        
        Label selectLabel = new Label("Select Chips:");
        selectLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial';"
        );
        
        // Chip buttons (smaller for multiplayer)
        VBox chipButtons = new VBox(6);
        chipButtons.setAlignment(Pos.CENTER);
        
        Button chip1 = createChipButton(1, "white", "#333333");
        Button chip5 = createChipButton(5, "#8B0000", "white");
        Button chip10 = createChipButton(10, "#1E90FF", "white");
        Button chip25 = createChipButton(25, "#228B22", "white");
        Button chip50 = createChipButton(50, "#FF8C00", "white");
        Button chip100 = createChipButton(100, "#2d2d2d", "white");
        
        chipButtons.getChildren().addAll(chip1, chip5, chip10, chip25, chip50, chip100);
        
        chip1.setOnAction(e -> addToBet(1));
        chip5.setOnAction(e -> addToBet(5));
        chip10.setOnAction(e -> addToBet(10));
        chip25.setOnAction(e -> addToBet(25));
        chip50.setOnAction(e -> addToBet(50));
        chip100.setOnAction(e -> addToBet(100));
        
        currentBetLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial';"
        );
        
        styleButton(betButton, "#2d5016");
        styleButton(clearBetButton, "#2d5016");
        betButton.setPrefWidth(120);
        clearBetButton.setPrefWidth(120);
        
        VBox betActionButtons = new VBox(6, betButton, clearBetButton);
        betActionButtons.setAlignment(Pos.CENTER);
        
        VBox panel = new VBox(8, betLabel, selectLabel, chipButtons, currentBetLabel, betActionButtons);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setStyle(
            "-fx-background-color: rgba(0,0,0,0.3); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 10; " +
            "-fx-min-width: 145; " +
            "-fx-max-width: 145;"
        );
        panel.setPadding(new Insets(10));
        
        return panel;
    }

    private VBox createInfoPanel() {
        Label infoLabel = new Label("GAME INFO");
        infoLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial';"
        );
        
        myBalanceLabel.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #90EE90; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-padding: 5;"
        );
        myBalanceLabel.setWrapText(true);
        myBalanceLabel.setMaxWidth(150);
        
        gameCodeLabel.setStyle(
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-padding: 5;"
        );
        gameCodeLabel.setWrapText(true);
        gameCodeLabel.setMaxWidth(150);
        
        turnIndicatorLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-padding: 5;"
        );
        turnIndicatorLabel.setWrapText(true);
        turnIndicatorLabel.setMaxWidth(150);
        
        statusLabel.setStyle(
            "-fx-font-size: 11px; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-padding: 5;"
        );
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(150);
        
        Region separator1 = new Region();
        separator1.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-pref-height: 2;");
        separator1.setMaxWidth(140);
        
        // Game action buttons
        styleButton(hitButton, "#2d5016");
        styleButton(standButton, "#2d5016");
        styleButton(startRoundButton, "#2d5016");
        hitButton.setPrefWidth(140);
        standButton.setPrefWidth(140);
        startRoundButton.setPrefWidth(140);
        
        VBox actionButtons = new VBox(6, hitButton, standButton, startRoundButton);
        actionButtons.setAlignment(Pos.CENTER);
        
        Region separator2 = new Region();
        separator2.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-pref-height: 2;");
        separator2.setMaxWidth(140);
        
        Region separator3 = new Region();
        separator3.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-pref-height: 2;");
        separator3.setMaxWidth(140);
        
        styleButton(backToMenuButton, "#8B0000");
        backToMenuButton.setPrefWidth(140);
        
        VBox panel = new VBox(8);
        panel.getChildren().addAll(
            infoLabel, 
            myBalanceLabel,
            separator1,
            gameCodeLabel,
            turnIndicatorLabel,
            statusLabel,
            separator2,
            actionButtons,
            separator3,
            backToMenuButton
        );
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setStyle(
            "-fx-background-color: rgba(0,0,0,0.4); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 10; " +
            "-fx-min-width: 155; " +
            "-fx-max-width: 155; " +
            "-fx-border-color: rgba(255,215,0,0.3); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        panel.setPadding(new Insets(10));
        
        return panel;
    }

    private Button createChipButton(int value, String bgColor, String textColor) {
        String fontSize = value >= 100 ? "9px" : "10px";
        String fontSizeHover = value >= 100 ? "10px" : "11px";
        
        Button chip = new Button("$" + value);
        chip.setStyle(
            "-fx-font-size: " + fontSize + "; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: " + textColor + "; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 50px; " +
            "-fx-min-height: 50px; " +
            "-fx-max-width: 50px; " +
            "-fx-max-height: 50px; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 50%; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);"
        );
        
        chip.setOnMouseEntered(e -> chip.setStyle(
            "-fx-font-size: " + fontSizeHover + "; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: " + textColor + "; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 50px; " +
            "-fx-min-height: 50px; " +
            "-fx-max-width: 50px; " +
            "-fx-max-height: 50px; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 3px; " +
            "-fx-border-radius: 50%; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.7), 8, 0, 0, 0);"
        ));
        
        chip.setOnMouseExited(e -> chip.setStyle(
            "-fx-font-size: " + fontSize + "; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: " + textColor + "; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 50px; " +
            "-fx-min-height: 50px; " +
            "-fx-max-width: 50px; " +
            "-fx-max-height: 50px; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 50%; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 2, 2);"
        ));
        
        return chip;
    }
    
    private void styleButton(Button button, String color) {
        button.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: derive(" + color + ", 20%); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 3, 3);"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        ));
    }

    private void addToBet(int amount) {
        currentBetAmount += amount;
        currentBetLabel.setText("Current Bet: $" + currentBetAmount);
    }
    
    private void clearBet() {
        currentBetAmount = 0;
        currentBetLabel.setText("Current Bet: $0");
    }

    private void handleBetClick() {
        if (currentBetAmount > 0) {
            onBet.accept(currentBetAmount);
            clearBet();
        } else {
            showMessage("Please select chips to bet.");
        }
    }

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

        if (showAll) {
            dealerTotalLabel.setText("Total: " + total);
        } else {
            dealerTotalLabel.setText("Total: ?");
        }
    }

    public void addOrUpdatePlayer(String playerId, String displayName, int balance, List<Card> cards) {
        PlayerPanel panel = playerPanels.get(playerId);
        if (panel == null) {
            panel = new PlayerPanel(playerId, displayName);
            playerPanels.put(playerId, panel);
            playersContainer.getChildren().add(panel);
        }
        panel.updateBalance(balance);
        panel.updateCards(cards);
    }

    public void removePlayer(String playerId) {
        PlayerPanel panel = playerPanels.remove(playerId);
        if (panel != null) {
            playersContainer.getChildren().remove(panel);
        }
    }

    public void showMessage(String message) {
        statusLabel.setText(message);
    }

    public void setMyBalance(int balance) {
        myBalanceLabel.setText("Balance: $" + balance);
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
                updateHostDisplay(localIP, port);
            } else {
                gameCodeLabel.setText("HOST | " + connectionString);
            }
        } catch (Exception e) {
            gameCodeLabel.setText("HOST | Port: " + connectionString);
        }
    }
    
    public void setGameCode(String gameCode) {
        this.currentGameCode = gameCode;
        if (gameCode.equals("Connected")) {
            gameCodeLabel.setText("CLIENT | Connected");
        } else {
            updateHostDisplay(null, null);
        }
    }
    
    private void updateHostDisplay(String localIP, String port) {
        if (currentGameCode.isEmpty()) {
            gameCodeLabel.setText("HOST | Loading...");
        } else if (localIP != null && port != null) {
            gameCodeLabel.setText(String.format("CODE: %s | %s:%s", currentGameCode, localIP, port));
        } else {
            gameCodeLabel.setText("CODE: " + currentGameCode);
        }
    }
    
    public void setTurnIndicator(String message) {
        turnIndicatorLabel.setText(message);
    }

    public void updatePlayer(String playerId, String displayName, List<Card> hand, int balance, boolean isActive) {
        addOrUpdatePlayer(playerId, displayName, balance, hand);
        highlightPlayer(playerId, isActive);
    }
    
    public void highlightPlayer(String playerId, boolean highlight) {
        PlayerPanel panel = playerPanels.get(playerId);
        if (panel != null && highlight) {
            panel.setStyle(
                "-fx-background-color: rgba(255,215,0,0.4); " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #FFD700; " +
                "-fx-border-width: 4; " +
                "-fx-border-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 12, 0, 0, 0);"
            );
        } else if (panel != null) {
            panel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.5); " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: rgba(255,215,0,0.5); " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8;"
            );
        }
    }
    
    public void updatePlayerBet(String playerId, int betAmount) {
        PlayerPanel panel = playerPanels.get(playerId);
        if (panel != null) {
            panel.updatePot(betAmount);
        }
    }
    
    public void updateMyBalance(int balance) {
        setMyBalance(balance);
    }
    
    public void setButtonsEnabled(boolean canBet, boolean canHit, boolean canStand, boolean canStartRound) {
        betButton.setDisable(!canBet);
        clearBetButton.setDisable(!canBet);
        hitButton.setDisable(!canHit);
        standButton.setDisable(!canStand);
        startRoundButton.setDisable(!canStartRound);
    }

    private VBox createCardSymbol(String symbol, String value, String color) {
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + color + ";"
        );
        valueLabel.setAlignment(Pos.CENTER);
        
        Label symbolLabel = new Label(symbol);
        symbolLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-text-fill: " + color + ";"
        );
        symbolLabel.setAlignment(Pos.CENTER);
        
        VBox cardContent = new VBox(1, valueLabel, symbolLabel);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setStyle(
            "-fx-border-color: #333333; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 5; " +
            "-fx-background-color: white; " +
            "-fx-background-radius: 5; " +
            "-fx-padding: 6 8 6 8; " +
            "-fx-min-width: 40px; " +
            "-fx-min-height: 60px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 1, 1);"
        );
        
        return cardContent;
    }

    private VBox createHiddenCard() {
        Label hiddenLabel = new Label("?");
        hiddenLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white;"
        );
        hiddenLabel.setAlignment(Pos.CENTER);
        
        VBox cardContent = new VBox(hiddenLabel);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setStyle(
            "-fx-border-color: #333333; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 5; " +
            "-fx-background-color: #4169E1; " +
            "-fx-background-radius: 5; " +
            "-fx-padding: 6 8 6 8; " +
            "-fx-min-width: 40px; " +
            "-fx-min-height: 60px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 1, 1);"
        );
        
        return cardContent;
    }

    /**
     * Player panel with emoji, pot chip, cards, and info
     */
    private class PlayerPanel extends VBox {
        private final Label playerEmoji;
        private final Label nameLabel;
        private final VBox potChipDisplay;
        private final Label potAmountLabel;
        private final HBox cardsBox;
        private final Label totalLabel;
        private final Label balanceLabel;
        private int potAmount = 0;

        public PlayerPanel(String playerId, String displayName) {
            super(3);

            // Player emoji
            playerEmoji = new Label("👤");
            playerEmoji.setStyle("-fx-font-size: 20px;");
            playerEmoji.setAlignment(Pos.CENTER);

            nameLabel = new Label(displayName);
            nameLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
            nameLabel.setWrapText(true);
            nameLabel.setMaxWidth(120);
            nameLabel.setAlignment(Pos.CENTER);

            // Pot chip display
            potChipDisplay = new VBox(2);
            potChipDisplay.setAlignment(Pos.CENTER);
            potChipDisplay.setMinHeight(50);
            potChipDisplay.setMaxHeight(50);
            potChipDisplay.setStyle(
                "-fx-background-color: rgba(0,0,0,0.3); " +
                "-fx-background-radius: 50%; " +
                "-fx-padding: 6; " +
                "-fx-border-color: #FFD700; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 50%; " +
                "-fx-min-width: 50; " +
                "-fx-max-width: 50;"
            );

            potAmountLabel = new Label("$0");
            potAmountLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-font-family: 'Arial';");
            potAmountLabel.setAlignment(Pos.CENTER);

            cardsBox = new HBox(2);
            cardsBox.setAlignment(Pos.CENTER);
            cardsBox.setMinHeight(70);
            cardsBox.setMaxHeight(70);
            cardsBox.setStyle("-fx-background-color: transparent;");

            totalLabel = new Label("Total: 0");
            totalLabel.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
            totalLabel.setAlignment(Pos.CENTER);

            balanceLabel = new Label("$0");
            balanceLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #90EE90; -fx-font-family: 'Arial';");
            balanceLabel.setAlignment(Pos.CENTER);

            getChildren().addAll(playerEmoji, nameLabel, potChipDisplay, potAmountLabel, cardsBox, totalLabel, balanceLabel);
            setAlignment(Pos.CENTER);
            setPadding(new Insets(6));
            setStyle(
                "-fx-background-color: rgba(0,0,0,0.5); " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: rgba(255,215,0,0.5); " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 8;"
            );
            setMinWidth(130);
            setMaxWidth(130);
        }

        public void updateCards(List<Card> cards) {
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
        }

        public void updateBalance(int balance) {
            balanceLabel.setText("$" + balance);
        }
        
        public void updatePot(int amount) {
            potAmount = amount;
            potChipDisplay.getChildren().clear();
            
            if (potAmount > 0) {
                Label potChip = new Label("$" + potAmount);
                potChip.setAlignment(Pos.CENTER);
                potChip.setMinSize(40, 40);
                potChip.setMaxSize(40, 40);
                potChip.setStyle(
                    "-fx-font-size: 10px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: #FFD700; " +
                    "-fx-text-fill: #000000; " +
                    "-fx-background-radius: 50%; " +
                    "-fx-border-color: #CC9900; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 50%; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 6, 0, 0, 0);"
                );
                potChipDisplay.getChildren().add(potChip);
            }
            
            potAmountLabel.setText("$" + potAmount);
        }
    }
}
