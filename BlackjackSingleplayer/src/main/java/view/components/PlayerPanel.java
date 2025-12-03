package view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Card;
import util.CardType;
import util.CardVisuals;

import java.util.List;

/**
 * Individual player panel showing cards, balance, and bet
 * 
 * @author Group 12
 */
public class PlayerPanel extends VBox {
    private final Label playerEmoji;
    private final Label nameLabel;
    private final VBox potChipDisplay;
    private final Label potAmountLabel;
    private final HBox cardsBox;
    private final Label totalLabel;
    private final Label balanceLabel;

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
        setPadding(new javafx.geometry.Insets(6));
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

        if (cards.isEmpty()) {
            Label emptyLabel = new Label("No cards");
            emptyLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #888888;");
            cardsBox.getChildren().add(emptyLabel);
        } else {
            for (Card card : cards) {
                VBox cardVisual = CardVisuals.createCard(
                    CardType.STANDARD_VISIBLE,
                    card.suit().symbol(),
                    card.rank().symbol(),
                    card.suit().isRed() ? "red" : "black"
                );
                cardVisual.setScaleX(0.6);
                cardVisual.setScaleY(0.6);
                cardsBox.getChildren().add(cardVisual);
                total += card.baseValue();
            }
        }

        totalLabel.setText("Total: " + total);
    }

    public void updateBalance(int balance) {
        balanceLabel.setText("$" + balance);
    }

    public void updatePot(int amount) {
        potAmountLabel.setText("$" + amount);
        
        // Update pot chip display
        potChipDisplay.getChildren().clear();
        if (amount > 0) {
            int numChips = Math.min(amount / 100, 5);
            for (int i = 0; i < numChips; i++) {
                Label chip = new Label("🔴");
                chip.setStyle(
                    "-fx-font-size: " + (8 - i) + "px; " +
                    "-fx-text-fill: #FFD700; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 2, 0, 0, 1);"
                );
                potChipDisplay.getChildren().add(chip);
            }
        }
    }

    public String getDisplayName() {
        return nameLabel.getText();
    }
}
