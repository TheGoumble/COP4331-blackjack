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
    private final Label dealerCardsLabel = new Label();
    private final Label dealerTotalLabel = new Label();

    private final Label playerLabel = new Label("You:");
    private final Label playerCardsLabel = new Label();
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

        // Dealer area
        VBox dealerBox = new VBox(5, dealerLabel, dealerCardsLabel, dealerTotalLabel);
        dealerBox.setAlignment(Pos.CENTER);

        // Player area
        VBox playerBox = new VBox(5, playerLabel, playerCardsLabel, playerTotalLabel, balanceLabel, betInfoLabel);
        playerBox.setAlignment(Pos.CENTER);

        VBox center = new VBox(20, dealerBox, playerBox);
        center.setAlignment(Pos.CENTER);
        setCenter(center);

        // Controls
        betField.setPromptText("Bet amount");
        HBox controls = new HBox(10, betField, betButton, hitButton, standButton);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10));
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
        // dealer cards
        StringBuilder dealerCards = new StringBuilder();
        for (CardInPlay cip : game.getDealerHand().cards()) {
            if (dealerCards.length() > 0) dealerCards.append(", ");
            dealerCards.append(cip.toString());
        }
        dealerCardsLabel.setText(dealerCards.toString());
        dealerTotalLabel.setText("Dealer total: " + game.getDealerTotalVisible());

        // player cards
        StringBuilder playerCards = new StringBuilder();
        for (CardInPlay cip : game.getPlayerHand().cards()) {
            if (playerCards.length() > 0) playerCards.append(", ");
            playerCards.append(cip.toString());
        }
        playerCardsLabel.setText(playerCards.toString());
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
}