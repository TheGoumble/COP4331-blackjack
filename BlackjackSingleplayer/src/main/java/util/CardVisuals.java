package util;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Class for creating consistent card visual elements
 * Eliminates code duplication across views
 * 
 * @author Group 12
 */
public class CardVisuals {
    
    /**
     * Creates a visual card representation with standard styling
     */
    public static VBox createCardSymbol(String symbol, String value, String color) {
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
    public static VBox createLargeCardSymbol(String symbol, String value, String color) {
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
    public static VBox createHiddenCard() {
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
    public static VBox createLargeHiddenCard() {
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
