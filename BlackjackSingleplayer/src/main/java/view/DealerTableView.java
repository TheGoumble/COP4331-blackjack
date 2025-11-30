package view;

import model.ActiveGame;
import model.CardInPlay;
import model.GameResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;
/**
 *
 * @author Bridjet Walker, Luca Lombardo
 */
public class DealerTableView extends BorderPane {

    private final Label dealerLabel = new Label("Dealer:");
    private final HBox dealerCardsBox = new HBox(10);
    private final Label dealerTotalLabel = new Label();

    private final Label playerLabel = new Label("You:");
    private final HBox playerCardsBox = new HBox(10);
    private final Label playerTotalLabel = new Label();
    private final Label balanceLabel = new Label("Balance: ");
    private final Label betInfoLabel = new Label("Current bet: 0");
    private final Label lastWinLabel = new Label("$0");

    private final Button betButton = new Button("Place Bet");
    private final Button clearBetButton = new Button("Clear Bet");
    private final Button hitButton = new Button("Hit");
    private final Button standButton = new Button("Stand");
    
    private final Label currentBetLabel = new Label("Current Bet: $0");
    private final Label potLabel = new Label("POT");
    private final VBox potChipDisplay = new VBox(5);
    private final Label potAmountLabel = new Label("$0");
    private final Label resultLabel = new Label("");
    private final Button newRoundButton = new Button("New Round");
    private final Button backToMenuButton = new Button("Back to Menu");

    private final Label statusLabel = new Label("Place a bet to start.");
    
    private int currentBetAmount = 0;
    private int potAmount = 0;

    private Consumer<Integer> onBet = amount -> {};
    private Runnable onHit = () -> {};
    private Runnable onStand = () -> {};
    private Runnable onBackToMenu = () -> {};

    public DealerTableView() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #2d5016;");

        // === Betting Controls ===
        Label betLabel = new Label("BETTING");
        betLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial';"
        );
        
        Label selectLabel = new Label("Select Chips:");
        selectLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial';"
        );
        
        // Chip buttons in vertical layout for left side
        VBox chipButtons = new VBox(8);
        chipButtons.setAlignment(Pos.CENTER);
        
        Button chip1 = createChipButton(1, "white", "#333333");
        Button chip5 = createChipButton(5, "#8B0000", "white");
        Button chip10 = createChipButton(10, "#1E90FF", "white");
        Button chip25 = createChipButton(25, "#228B22", "white");
        Button chip50 = createChipButton(50, "#FF8C00", "white");
        Button chip100 = createChipButton(100, "#2d2d2d", "white");
        
        chipButtons.getChildren().addAll(chip1, chip5, chip10, chip25, chip50, chip100);
        
        // Chip actions
        chip1.setOnAction(e -> addToBet(1));
        chip5.setOnAction(e -> addToBet(5));
        chip10.setOnAction(e -> addToBet(10));
        chip25.setOnAction(e -> addToBet(25));
        chip50.setOnAction(e -> addToBet(50));
        chip100.setOnAction(e -> addToBet(100));
        
        // Current bet display
        currentBetLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial';"
        );
        
        // Buttons
        styleButton(betButton);
        styleButton(clearBetButton);
        betButton.setPrefWidth(140);
        clearBetButton.setPrefWidth(140);
        
        VBox betActionButtons = new VBox(8, betButton, clearBetButton);
        betActionButtons.setAlignment(Pos.CENTER);
        
        // Left panel container
        VBox leftPanel = new VBox(15, 
            betLabel, 
            selectLabel,
            chipButtons, 
            currentBetLabel, 
            betActionButtons
        );
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setStyle(
            "-fx-background-color: rgba(0,0,0,0.3); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 20; " +
            "-fx-min-width: 200; " +
            "-fx-max-width: 200;"
        );
        leftPanel.setPadding(new Insets(20));
        
        setLeft(leftPanel);

        // Game Area ===
        // Dealer area
        dealerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
        dealerCardsBox.setAlignment(Pos.CENTER);
        dealerTotalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-family: 'Arial';");
        VBox dealerBox = new VBox(10, dealerLabel, dealerCardsBox, dealerTotalLabel);
        dealerBox.setAlignment(Pos.CENTER);
        dealerBox.setPadding(new Insets(15));

        // Pot display centered
        potLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial';"
        );
        
        potChipDisplay.setAlignment(Pos.CENTER);
        potChipDisplay.setMinHeight(110);
        potChipDisplay.setMaxHeight(110);
        potChipDisplay.setStyle(
            "-fx-background-color: rgba(0,0,0,0.4); " +
            "-fx-background-radius: 50%; " +
            "-fx-padding: 15; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 50%; " +
            "-fx-min-width: 110; " +
            "-fx-max-width: 110;"
        );
        
        potAmountLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial';"
        );
        
        // Result message (YOU WIN / YOU LOSE)
        resultLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-padding: 10;"
        );
        resultLabel.setVisible(false);
        
        VBox potBox = new VBox(5, potLabel, potChipDisplay, potAmountLabel, resultLabel);
        potBox.setAlignment(Pos.CENTER);
        potBox.setPadding(new Insets(5));
        
        // New Round button 
        styleButton(newRoundButton);
        newRoundButton.setPrefWidth(140);
        newRoundButton.setStyle(
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #FFD700; " +
            "-fx-text-fill: #2d5016; " +
            "-fx-padding: 12 20 12 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        newRoundButton.setVisible(false);
        
        newRoundButton.setOnMouseEntered(e -> newRoundButton.setStyle(
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #FFF700; " +
            "-fx-text-fill: #2d5016; " +
            "-fx-padding: 12 20 12 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.7), 8, 0, 3, 3);"
        ));
        
        newRoundButton.setOnMouseExited(e -> newRoundButton.setStyle(
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #FFD700; " +
            "-fx-text-fill: #2d5016; " +
            "-fx-padding: 12 20 12 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        ));
        
        // New Round button area 
        VBox newRoundBox = new VBox(newRoundButton);
        newRoundBox.setAlignment(Pos.CENTER);
        newRoundBox.setPadding(new Insets(5, 0, 0, 0));

        // Player area
        playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
        playerCardsBox.setAlignment(Pos.CENTER);
        playerTotalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-family: 'Arial';");
        VBox playerBox = new VBox(8, playerLabel, playerCardsBox, playerTotalLabel);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(8));

        // Game action buttons
        styleButton(hitButton);
        styleButton(standButton);
        hitButton.setPrefWidth(120);
        standButton.setPrefWidth(120);
        
        HBox gameActionButtons = new HBox(15, hitButton, standButton);
        gameActionButtons.setAlignment(Pos.CENTER);
        
        // Status
        statusLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-padding: 10;"
        );
        
        VBox centerArea = new VBox(10, dealerBox, potBox, playerBox, gameActionButtons, statusLabel);
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setPadding(new Insets(5));
        
        setCenter(centerArea);

        //  Balence Info  
        Label infoLabel = new Label("GAME INFO");
        infoLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-padding: 0 0 10 0;"
        );
        
        Label balanceHeader = new Label("Balance:");
        balanceHeader.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial';"
        );
        
        balanceLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #90EE90; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-padding: 5 0 10 0;"
        );
        
        Region separator1 = new Region();
        separator1.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-pref-height: 2;");
        separator1.setMaxWidth(160);
        
        Label betHeader = new Label("Current Table Bet:");
        betHeader.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-padding: 10 0 5 0;"
        );
        
        betInfoLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #87CEEB; " +
            "-fx-font-family: 'Arial';"
        );
        
        VBox balanceBox = new VBox(5, balanceHeader, balanceLabel);
        balanceBox.setAlignment(Pos.CENTER);
        
        VBox betBox = new VBox(5, betHeader, betInfoLabel);
        betBox.setAlignment(Pos.CENTER);
        
        Region separator2 = new Region();
        separator2.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-pref-height: 2;");
        separator2.setMaxWidth(160);
        
        Label winHeader = new Label("Last Win:");
        winHeader.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-padding: 10 0 5 0;"
        );
        
        lastWinLabel.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #90EE90; " +
            "-fx-font-family: 'Arial';"
        );
        
        VBox winBox = new VBox(5, winHeader, lastWinLabel);
        winBox.setAlignment(Pos.CENTER);
        
        Region separator3 = new Region();
        separator3.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-pref-height: 2;");
        separator3.setMaxWidth(160);
        
        // Back to Menu button
        styleButton(backToMenuButton);
        backToMenuButton.setPrefWidth(160);
        backToMenuButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #8B0000; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 15 10 15; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        
        backToMenuButton.setOnMouseEntered(e -> backToMenuButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #A52A2A; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 15 10 15; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(139,0,0,0.7), 8, 0, 3, 3);"
        ));
        
        backToMenuButton.setOnMouseExited(e -> backToMenuButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #8B0000; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 15 10 15; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        ));
        
        Region separator4 = new Region();
        separator4.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-pref-height: 2;");
        separator4.setMaxWidth(160);
        
        VBox rightPanel = new VBox(15, infoLabel, balanceBox, separator1, betBox, separator2, winBox, separator3, newRoundButton, separator4, backToMenuButton);
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setStyle(
            "-fx-background-color: rgba(0,0,0,0.4); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 25; " +
            "-fx-min-width: 220; " +
            "-fx-max-width: 220; " +
            "-fx-border-color: rgba(255,215,0,0.3); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        rightPanel.setPadding(new Insets(20));
        
        setRight(rightPanel);

        // Button actions
        hitButton.setDisable(true);
        standButton.setDisable(true);

        betButton.setOnAction(e -> {
            if (currentBetAmount > 0) {
                int betAmount = currentBetAmount;
                placeBetInPot(betAmount);
                onBet.accept(betAmount);
            } else {
                showMessage("Please select chips to bet.");
            }
        });
        
        clearBetButton.setOnAction(e -> clearBet());

        hitButton.setOnAction(e -> onHit.run());
        standButton.setOnAction(e -> onStand.run());
        
        newRoundButton.setOnAction(e -> {
            clearPot();
            resultLabel.setVisible(false);
            newRoundButton.setVisible(false);
            statusLabel.setText("Place a bet to start.");
        });
        
        backToMenuButton.setOnAction(e -> onBackToMenu.run());
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
    
    public void setOnBackToMenu(Runnable handler) {
        this.onBackToMenu = handler;
    }

    public void showMessage(String message) {
        statusLabel.setText(message);
    }

    public void render(ActiveGame game) {
        // dealer cards - visual representation
        dealerCardsBox.getChildren().clear();
        for (CardInPlay cip : game.getDealerHand().cards()) {
            if (cip.isVisible()) {
                dealerCardsBox.getChildren().add(createCardSymbol(
                    cip.card().suit().symbol(),
                    cip.card().rank().symbol(),
                    cip.card().suit().isRed() ? "red" : "black"
                ));
            } else {
                dealerCardsBox.getChildren().add(createHiddenCard());
            }
        }
        dealerTotalLabel.setText("Dealer total: " + game.getDealerTotalVisible());

        // player cards - visual representation
        playerCardsBox.getChildren().clear();
        for (CardInPlay cip : game.getPlayerHand().cards()) {
            playerCardsBox.getChildren().add(createCardSymbol(
                cip.card().suit().symbol(),
                cip.card().rank().symbol(),
                cip.card().suit().isRed() ? "red" : "black"
            ));
        }
        playerTotalLabel.setText("Your total: " + game.getPlayerTotalVisible());

        balanceLabel.setText("Balance:\n$" + game.getBalance());
        betInfoLabel.setText("In Play:\n$" + game.getCurrentBet());

        if (game.isGameOver()) {
            betButton.setDisable(true);
            clearBetButton.setDisable(true);
            hitButton.setDisable(true);
            standButton.setDisable(true);
            statusLabel.setText("Game over: no funds left.");
            clearPot();
            return;
        }

        // buttons + status
        if (!game.isRoundInProgress()) {
            hitButton.setDisable(true);
            standButton.setDisable(true);
            betButton.setDisable(false);
            clearBetButton.setDisable(false);

            GameResult res = game.getLastResult();
            switch (res) {
                case NONE -> {
                    statusLabel.setText("Place a bet to start.");
                    if (potAmount == 0) {
                        resultLabel.setVisible(false);
                        newRoundButton.setVisible(false);
                    }
                }
                case PLAYER_BLACKJACK -> {
                    resultLabel.setText("YOU WIN!");
                    resultLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #00FF00; -fx-font-family: 'Arial'; -fx-padding: 10;");
                    resultLabel.setVisible(true);
                    statusLabel.setText("");
                    int winnings = (int)(potAmount * 2.5); // Blackjack pays 3:2
                    lastWinLabel.setText("$" + winnings);
                    lastWinLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00FF00; -fx-font-family: 'Arial';");
                    newRoundButton.setVisible(true);
                }
                case PLAYER_WIN -> {
                    resultLabel.setText("YOU WIN!");
                    resultLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #00FF00; -fx-font-family: 'Arial'; -fx-padding: 10;");
                    resultLabel.setVisible(true);
                    statusLabel.setText("");
                    int winnings = potAmount * 2; // Normal win pays 2:1
                    lastWinLabel.setText("$" + winnings);
                    lastWinLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00FF00; -fx-font-family: 'Arial';");
                    newRoundButton.setVisible(true);
                }
                case DEALER_WIN -> {
                    resultLabel.setText("YOU LOSE");
                    resultLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #FF0000; -fx-font-family: 'Arial'; -fx-padding: 10;");
                    resultLabel.setVisible(true);
                    statusLabel.setText("");
                    lastWinLabel.setText("$0");
                    lastWinLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FF0000; -fx-font-family: 'Arial';");
                    clearPot();
                    newRoundButton.setVisible(true);
                }
                case PUSH -> {
                    resultLabel.setText("PUSH");
                    resultLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #FFA500; -fx-font-family: 'Arial'; -fx-padding: 10;");
                    resultLabel.setVisible(true);
                    statusLabel.setText("");
                    lastWinLabel.setText("$" + potAmount);
                    lastWinLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FFA500; -fx-font-family: 'Arial';");
                    newRoundButton.setVisible(true);
                }
            }
        } else {
            betButton.setDisable(true);
            clearBetButton.setDisable(true);
            hitButton.setDisable(!game.isPlayerTurn());
            standButton.setDisable(!game.isPlayerTurn());
            statusLabel.setText("Your turn: Hit or Stand.");
            newRoundButton.setVisible(false);
        }

        // allow new bets only when no round in progress
        betButton.setDisable(game.isRoundInProgress());
        clearBetButton.setDisable(game.isRoundInProgress());
    }

    /**
     * Creates a visual card representation
     */
    private VBox createCardSymbol(String symbol, String value, String color) {
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + color + ";"
        );
        valueLabel.setAlignment(Pos.CENTER);
        
        Label symbolLabel = new Label(symbol);
        symbolLabel.setStyle(
            "-fx-font-size: 40px; " +
            "-fx-text-fill: " + color + ";"
        );
        symbolLabel.setAlignment(Pos.CENTER);
        
        VBox cardContent = new VBox(3, valueLabel, symbolLabel);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setStyle(
            "-fx-border-color: #333333; " +
            "-fx-border-width: 3px; " +
            "-fx-border-radius: 8; " +
            "-fx-background-color: white; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 12 20 12 20; " +
            "-fx-min-width: 75px; " +
            "-fx-min-height: 110px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        
        return cardContent;
    }

    /**
     * Creates a hidden card
     */
    private VBox createHiddenCard() {
        Label hiddenLabel = new Label("?");
        hiddenLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white;"
        );
        hiddenLabel.setAlignment(Pos.CENTER);
        
        VBox cardContent = new VBox(hiddenLabel);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setStyle(
            "-fx-border-color: #333333; " +
            "-fx-border-width: 3px; " +
            "-fx-border-radius: 8; " +
            "-fx-background-color: #4169E1; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 12 20 12 20; " +
            "-fx-min-width: 75px; " +
            "-fx-min-height: 110px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        
        return cardContent;
    }

    /**
     * Creates a circular chip button with value
     */
    private Button createChipButton(int value, String bgColor, String textColor) {
        // Adjust font size based on value length for better visibility
        String fontSize = value >= 100 ? "11px" : "13px";
        String fontSizeHover = value >= 100 ? "12px" : "14px";
        
        Button chip = new Button("$" + value);
        chip.setStyle(
            "-fx-font-size: " + fontSize + "; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: " + textColor + "; " +
            "-fx-padding: 15; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 65px; " +
            "-fx-min-height: 65px; " +
            "-fx-max-width: 65px; " +
            "-fx-max-height: 65px; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 50%; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 6, 0, 2, 2);"
        );
        
        chip.setOnMouseEntered(e -> chip.setStyle(
            "-fx-font-size: " + fontSizeHover + "; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: " + textColor + "; " +
            "-fx-padding: 15; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 65px; " +
            "-fx-min-height: 65px; " +
            "-fx-max-width: 65px; " +
            "-fx-max-height: 65px; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 3px; " +
            "-fx-border-radius: 50%; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.7), 10, 0, 0, 0);"
        ));
        
        chip.setOnMouseExited(e -> chip.setStyle(
            "-fx-font-size: " + fontSize + "; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: " + textColor + "; " +
            "-fx-padding: 15; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 65px; " +
            "-fx-min-height: 65px; " +
            "-fx-max-width: 65px; " +
            "-fx-max-height: 65px; " +
            "-fx-cursor: hand; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 50%; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 6, 0, 2, 2);"
        ));
        
        return chip;
    }
    
    /**
     * Creates a display chip
     */
    private Label createDisplayChip(int value, String bgColor, String textColor) {
        Label chip = new Label("$" + value);
        chip.setStyle(
            "-fx-font-size: 10px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: " + textColor + "; " +
            "-fx-padding: 10; " +
            "-fx-background-radius: 50%; " +
            "-fx-min-width: 45px; " +
            "-fx-min-height: 45px; " +
            "-fx-max-width: 45px; " +
            "-fx-max-height: 45px; " +
            "-fx-alignment: center; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 50%; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 4, 0, 1, 1);"
        );
        return chip;
    }
    
   
    private void addToBet(int amount) {
        currentBetAmount += amount;
        currentBetLabel.setText("Current Bet: $" + currentBetAmount);
    }
    
    
    private void clearBet() {
        currentBetAmount = 0;
        currentBetLabel.setText("Current Bet: $0");
    }
    
    /**
     * Places bet into the pot 
     */
    private void placeBetInPot(int amount) {
        potAmount = amount;
        potChipDisplay.getChildren().clear();
        
        if (potAmount > 0) {
            // Create single large chip with bet amount
            Label potChip = new Label("$" + potAmount);
            potChip.setAlignment(Pos.CENTER);
            potChip.setMinSize(85, 85);
            potChip.setMaxSize(85, 85);
            potChip.setStyle(
                "-fx-font-size: 20px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: #FFD700; " +
                "-fx-text-fill: #000000; " +
                "-fx-background-radius: 50%; " +
                "-fx-border-color: #CC9900; " +
                "-fx-border-width: 3px; " +
                "-fx-border-radius: 50%; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 10, 0, 0, 0);"
            );
            potChipDisplay.getChildren().add(potChip);
        }
        
        potAmountLabel.setText("$" + potAmount);
        clearBet();
    }
  
    private void clearPot() {
        potAmount = 0;
        potChipDisplay.getChildren().clear();
        potAmountLabel.setText("$0");
    }
    
    private void styleButton(Button button) {
        button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: white; " +
            "-fx-text-fill: #2d5016; " +
            "-fx-padding: 8 20 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: lightgray; " +
            "-fx-text-fill: #2d5016; " +
            "-fx-padding: 8 20 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 3, 3);"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: white; " +
            "-fx-text-fill: #2d5016; " +
            "-fx-padding: 8 20 8 20; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 2, 2);"
        ));
    }
}