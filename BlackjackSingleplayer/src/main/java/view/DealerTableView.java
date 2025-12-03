package view;

import model.ActiveGame;
import model.CardInPlay;
import model.GameResult;
import util.CardType;
import util.CardVisuals;
import util.StyleConstants;
import view.components.ChipBettingPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;
/**
 * MVC PATTERN: This is a View (user interface)
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

    private final Button hitButton = new Button("Hit");
    private final Button standButton = new Button("Stand");
    
    private final ChipBettingPanel bettingPanel = new ChipBettingPanel();
    private final Label potLabel = new Label("POT");
    private final VBox potChipDisplay = new VBox(5);
    private final Label potAmountLabel = new Label("$0");
    private final Label resultLabel = new Label("");
    private final Button newRoundButton = new Button("New Round");
    private final Button backToMenuButton = new Button("Back to Menu");
    private final StrategySelector strategySelector = new StrategySelector();

    private final Label statusLabel = new Label("Place a bet to start.");
    
    private int potAmount = 0;

    private Consumer<Integer> onBet = amount -> {};
    private Runnable onHit = () -> {};
    private Runnable onStand = () -> {};
    private Runnable onBackToMenu = () -> {};

    public DealerTableView() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: " + StyleConstants.GREEN_FELT + ";");

        // === Betting Controls ===
        bettingPanel.setOnBet(amount -> {
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
            bettingPanel.clearBet();
            
            if (onBet != null) {
                onBet.accept(amount);
            }
        });
        
        // Left panel container
        VBox leftPanel = new VBox(15, strategySelector, bettingPanel);
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
        dealerLabel.setStyle(StyleConstants.TITLE_TEXT);
        dealerCardsBox.setAlignment(Pos.CENTER);
        dealerTotalLabel.setStyle(StyleConstants.BODY_TEXT);
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
        StyleConstants.styleButton(newRoundButton);
        newRoundButton.setPrefWidth(140);
        newRoundButton.setVisible(false);
        
        // New Round button area 
        VBox newRoundBox = new VBox(newRoundButton);
        newRoundBox.setAlignment(Pos.CENTER);
        newRoundBox.setPadding(new Insets(5, 0, 0, 0));

        // Player area
        playerLabel.setStyle(StyleConstants.TITLE_TEXT);
        playerCardsBox.setAlignment(Pos.CENTER);
        playerTotalLabel.setStyle(StyleConstants.BODY_TEXT);
        VBox playerBox = new VBox(8, playerLabel, playerCardsBox, playerTotalLabel);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(8));

        // Game action buttons
        StyleConstants.styleButton(hitButton);
        StyleConstants.styleButton(standButton);
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
        centerArea.setPadding(new Insets(20));
        
        // Create oval table background
        StackPane tableArea = new StackPane();
        tableArea.setStyle(
            "-fx-background-color: #1a3d0f; " + // Darker green for table
            "-fx-background-radius: 350px / 250px; " + // Creates oval shape (horizontal / vertical radius)
            "-fx-border-color: #8B4513; " + // Brown wood border
            "-fx-border-width: 10; " +
            "-fx-border-radius: 350px / 250px; " +
            "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5);" // Inner shadow for depth
        );
        tableArea.setMinSize(900, 650);
        tableArea.setMaxSize(900, 650);
        tableArea.getChildren().add(centerArea);
        
        // Wrap table in container for centering
        StackPane tableContainer = new StackPane(tableArea);
        tableContainer.setAlignment(Pos.CENTER);
        tableContainer.setPadding(new Insets(10));
        
        setCenter(tableContainer);

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
        StyleConstants.styleDangerButton(backToMenuButton);
        backToMenuButton.setPrefWidth(160);
        
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
                dealerCardsBox.getChildren().add(CardVisuals.createCard(
                    CardType.LARGE_VISIBLE,
                    cip.card().suit().symbol(),
                    cip.card().rank().symbol(),
                    cip.card().suit().isRed() ? "red" : "black"
                ));
            } else {
                dealerCardsBox.getChildren().add(CardVisuals.createCard(CardType.LARGE_HIDDEN, null, null, null));
            }
        }
        dealerTotalLabel.setText("Dealer total: " + game.getDealerTotalVisible());

        // player cards - visual representation
        playerCardsBox.getChildren().clear();
        for (CardInPlay cip : game.getPlayerHand().cards()) {
            playerCardsBox.getChildren().add(CardVisuals.createCard(
                CardType.LARGE_VISIBLE,
                cip.card().suit().symbol(),
                cip.card().rank().symbol(),
                cip.card().suit().isRed() ? "red" : "black"
            ));
        }
        playerTotalLabel.setText("Your total: " + game.getPlayerTotalVisible());

        balanceLabel.setText("Balance:\n$" + game.getBalance());
        betInfoLabel.setText("In Play:\n$" + game.getCurrentBet());
        
        // Update betting panel with current balance for ALL-IN chip
        bettingPanel.setPlayerBalance(game.getBalance());

        // Only trigger game over if not in a round (balance check after round ends)
        if (game.isGameOver() && !game.isRoundInProgress()) {
            bettingPanel.setButtonsEnabled(false);
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
            bettingPanel.setButtonsEnabled(true);

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
            bettingPanel.setButtonsEnabled(false);
            hitButton.setDisable(!game.isPlayerTurn());
            standButton.setDisable(!game.isPlayerTurn());
            statusLabel.setText("Your turn: Hit or Stand.");
            newRoundButton.setVisible(false);
        }

        // allow new bets only when no round in progress
        bettingPanel.setButtonsEnabled(!game.isRoundInProgress());
    }

    private void clearPot() {
        potAmount = 0;
        potChipDisplay.getChildren().clear();
        potAmountLabel.setText("$0");
    }
    
    public StrategySelector getStrategySelector() {
        return strategySelector;
    }
}