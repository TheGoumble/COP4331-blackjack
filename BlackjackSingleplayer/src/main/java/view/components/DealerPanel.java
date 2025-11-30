package view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Card;
import util.CardVisuals;

import java.util.List;

/**
 * Reusable dealer panel component showing dealer cards and total
 * 
 * @author Group 12
 */
public class DealerPanel extends VBox {
    private final Label dealerLabel;
    private final HBox dealerCardsBox;
    private final Label dealerTotalLabel;
    
    public DealerPanel() {
        super(10);
        
        dealerLabel = new Label("Dealer");
        dealerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Arial';");
        
        dealerCardsBox = new HBox(6);
        dealerCardsBox.setAlignment(Pos.CENTER);
        dealerCardsBox.setMinHeight(120);
        dealerCardsBox.setStyle("-fx-background-color: transparent;");
        
        dealerTotalLabel = new Label("Total: 0");
        dealerTotalLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-family: 'Arial';");
        
        getChildren().addAll(dealerLabel, dealerCardsBox, dealerTotalLabel);
        setAlignment(Pos.CENTER);
    }
    
    /**
     * Update dealer display with cards
     */
    public void updateDealer(List<Card> cards, boolean showAll) {
        dealerCardsBox.getChildren().clear();
        int total = 0;
        
        for (int i = 0; i < cards.size(); i++) {
            if (i == 1 && !showAll) {
                dealerCardsBox.getChildren().add(CardVisuals.createHiddenCard());
            } else {
                Card card = cards.get(i);
                dealerCardsBox.getChildren().add(CardVisuals.createCardSymbol(
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
     * Clear dealer cards
     */
    public void clear() {
        dealerCardsBox.getChildren().clear();
        dealerTotalLabel.setText("Total: 0");
    }
}
