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
 * @author Bridjet Walker
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

    private final TextField betField = new TextField();
    private final Button betButton = new Button("Place Bet");
    private final Button hitButton = new Button("Hit");
    private final Button standButton = new Button("Stand");

    private final Label statusLabel = new Label("Place a bet to start.");

    private Consumer<Integer> onBet = amount -> {};
    private Runnable onHit = () -> {};
    private Runnable onStand = () -> {};

    public DealerTableView() {
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #2d5016;");

        // Dealer area
        dealerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        dealerCardsBox.setAlignment(Pos.CENTER);
        dealerTotalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        VBox dealerBox = new VBox(10, dealerLabel, dealerCardsBox, dealerTotalLabel);
        dealerBox.setAlignment(Pos.CENTER);
        dealerBox.setPadding(new Insets(20));

        // Player area
        playerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        playerCardsBox.setAlignment(Pos.CENTER);
        playerTotalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        balanceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: yellow;");
        betInfoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: lightblue;");
        VBox playerBox = new VBox(10, playerLabel, playerCardsBox, playerTotalLabel, balanceLabel, betInfoLabel);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setPadding(new Insets(20));

        VBox center = new VBox(30, dealerBox, playerBox);
        center.setAlignment(Pos.CENTER);
        setCenter(center);

        // Controls
        betField.setPromptText("Bet amount");
        styleButton(betButton);
        styleButton(hitButton);
        styleButton(standButton);
        HBox controls = new HBox(10, betField, betButton, hitButton, standButton);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10));
        
        statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");
        setBottom(new VBox(10, controls, statusLabel));

        hitButton.setDisable(true);
        standButton.setDisable(true);

        betButton.setOnAction(e -> {
            try {
                int amt = Integer.parseInt(betField.getText().trim());
                onBet.accept(amt);
            } catch (NumberFormatException ex) {
                showMessage("Invalid bet amount.");
            }
        });

        hitButton.setOnAction(e -> onHit.run());
        standButton.setOnAction(e -> onStand.run());
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

        balanceLabel.setText("Balance: " + game.getBalance());
        betInfoLabel.setText("Current bet: " + game.getCurrentBet());

        // clear bet text after each render so user can type a new one
        betField.clear();

        if (game.isGameOver()) {
            betButton.setDisable(true);
            hitButton.setDisable(true);
            standButton.setDisable(true);
            statusLabel.setText("Game over: no funds left.");
            return;
        }

        // buttons + status
        if (!game.isRoundInProgress()) {
            hitButton.setDisable(true);
            standButton.setDisable(true);

            GameResult res = game.getLastResult();
            switch (res) {
                case NONE -> statusLabel.setText("Place a bet to start.");
                case PLAYER_BLACKJACK -> statusLabel.setText("Blackjack! You win 3:2.");
                case PLAYER_WIN -> statusLabel.setText("You win!");
                case DEALER_WIN -> statusLabel.setText("Dealer wins.");
                case PUSH -> statusLabel.setText("Push. Bet returned.");
            }
        } else {
            betButton.setDisable(true);
            hitButton.setDisable(!game.isPlayerTurn());
            standButton.setDisable(!game.isPlayerTurn());
            statusLabel.setText("Your turn: Hit or Stand.");
        }

        // allow new bets only when no round in progress
        betButton.setDisable(game.isRoundInProgress());
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
     * Creates a hidden (face-down) card
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
     * Style a button with consistent appearance
     */
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