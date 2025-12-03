package util;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * CREATOR: CardVisuals class
 * Contains the single FACTORY METHOD
 * @author Group 12
 */
public class CardVisuals {
    
    /**
     * FACTORY METHOD - Single method that creates all card types
     * This is the core of the Factory Method pattern
     * 
     * @param type The type of card to create
     * @param symbol Card symbol (ignored for hidden cards)
     * @param value Card value (ignored for hidden cards)
     * @param color Card color (ignored for hidden cards)
     * @return VBox representing the CONCRETE PRODUCT
     */
    public static VBox createCard(CardType type, String symbol, String value, String color) {
        switch (type) {
            case STANDARD_VISIBLE:
                return createStandardVisibleCard(symbol, value, color);
            case LARGE_VISIBLE:
                return createLargeVisibleCard(symbol, value, color);
            case STANDARD_HIDDEN:
                return createStandardHiddenCard();
            case LARGE_HIDDEN:
                return createLargeHiddenCard();
            default:
                throw new IllegalArgumentException("Unknown card type: " + type);
        }
    }
    
    /**
     * Creates a visual card representation with standard styling
     */
    private static VBox createStandardVisibleCard(String symbol, String value, String color) {
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
        
        VBox cardContent = new VBox(2, valueLabel, symbolLabel);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setStyle(
            "-fx-border-color: #333333; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 6; " +
            "-fx-background-color: white; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 8 12 8 12; " +
            "-fx-min-width: 45px; " +
            "-fx-min-height: 65px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 4, 0, 1, 1);"
        );
        
        return cardContent;
    }
    
    /**
     * Creates a large card symbol for dealer/single player views
     */
    private static VBox createLargeVisibleCard(String symbol, String value, String color) {
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
     * Creates a hidden (face-down) card for dealer
     */
    private static VBox createStandardHiddenCard() {
        Label hiddenLabel = new Label("?");
        hiddenLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white;"
        );
        hiddenLabel.setAlignment(Pos.CENTER);
        
        VBox cardContent = new VBox(hiddenLabel);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setStyle(
            "-fx-border-color: #333333; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 6; " +
            "-fx-background-color: #4169E1; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 8 12 8 12; " +
            "-fx-min-width: 45px; " +
            "-fx-min-height: 65px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 4, 0, 1, 1);"
        );
        
        return cardContent;
    }
    
    /**
     * Creates a large hidden card for dealer/single player views
     */
    private static VBox createLargeHiddenCard() {
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
}
