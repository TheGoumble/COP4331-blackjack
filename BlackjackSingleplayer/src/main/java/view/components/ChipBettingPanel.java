package view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import util.StyleConstants;

import java.util.function.Consumer;

/**
 * Casino Chip-based betting interface component
 * 
 * @author Group 12
 */
public class ChipBettingPanel extends VBox {
    private final Label betLabel;
    private final Label currentBetLabel;
    private final GridPane chipGrid;
    private final Button betButton;
    private final Button clearBetButton;
    
    private int currentBetAmount = 0;
    private int playerBalance = 0;
    private Consumer<Integer> onBet = amount -> {};
    
    private static final int[] CHIP_VALUES = {10, 25, 50, 100, 500};
    private static final String[] CHIP_COLORS = {"#808080", "#B22222", "#0000CD", "#228B22", "#8B008B"};
    
    private Button allInChip;
    
    public ChipBettingPanel() {
        super(15);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(15));
        setStyle(StyleConstants.WHITE_PANEL);
        
        // Header
        betLabel = new Label("💰 Place Your Bet");
        betLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + StyleConstants.GREEN_FELT + "; " +
            "-fx-font-family: 'Arial';"
        );
        
        Label selectLabel = new Label("Select chips to bet:");
        selectLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-text-fill: #666666; " +
            "-fx-font-family: 'Arial';"
        );
        
        // Chip grid
        chipGrid = new GridPane();
        chipGrid.setHgap(10);
        chipGrid.setVgap(10);
        chipGrid.setAlignment(Pos.CENTER);
        
        for (int i = 0; i < CHIP_VALUES.length; i++) {
            Button chip = createChipButton(CHIP_VALUES[i], CHIP_COLORS[i]);
            int row = i / 3;
            int col = i % 3;
            chipGrid.add(chip, col, row);
        }
        
        // Add ALL-IN chip
        allInChip = createAllInChip();
        chipGrid.add(allInChip, 2, 1); // Place in row 1, col 2
        
        // Current bet display
        currentBetLabel = new Label("Current Bet: $0");
        currentBetLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + StyleConstants.GOLD + "; " +
            "-fx-font-family: 'Arial';"
        );
        
        // Action buttons
        betButton = new Button("Place Bet");
        clearBetButton = new Button("Clear Bet");
        
        StyleConstants.styleButton(betButton);
        StyleConstants.styleDangerButton(clearBetButton);
        
        betButton.setOnAction(e -> placeBet());
        clearBetButton.setOnAction(e -> clearBet());
        
        getChildren().addAll(betLabel, selectLabel, chipGrid, currentBetLabel, betButton, clearBetButton);
    }
    
    private Button createChipButton(int value, String color) {
        Button chip = new Button("$" + value);
        chip.setMinSize(60, 60);
        chip.setMaxSize(60, 60);
        
        String baseStyle = 
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 50%; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 50%; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 2, 2);";
        
        String hoverStyle = 
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 50%; " +
            "-fx-border-color: " + StyleConstants.GOLD + "; " +
            "-fx-border-width: 4; " +
            "-fx-border-radius: 50%; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 8, 0, 0, 3); " +
            "-fx-scale-x: 1.05; -fx-scale-y: 1.05;";
        
        chip.setStyle(baseStyle);
        chip.setOnMouseEntered(e -> chip.setStyle(hoverStyle));
        chip.setOnMouseExited(e -> chip.setStyle(baseStyle));
        chip.setOnAction(e -> addChipToBet(value));
        
        return chip;
    }
    
    private Button createAllInChip() {
        Button chip = new Button("ALL\nIN");
        chip.setMinSize(60, 60);
        chip.setMaxSize(60, 60);
        
        String baseStyle = 
            "-fx-background-color: linear-gradient(to bottom, #FFD700, #FFA500); " +
            "-fx-text-fill: #8B0000; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 11px; " +
            "-fx-background-radius: 50%; " +
            "-fx-border-color: #FF0000; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 50%; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(255,0,0,0.6), 5, 0, 2, 2);";
        
        String hoverStyle = 
            "-fx-background-color: linear-gradient(to bottom, #FFD700, #FFA500); " +
            "-fx-text-fill: #8B0000; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 11px; " +
            "-fx-background-radius: 50%; " +
            "-fx-border-color: #FF0000; " +
            "-fx-border-width: 4; " +
            "-fx-border-radius: 50%; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(255,0,0,0.9), 10, 0, 0, 3); " +
            "-fx-scale-x: 1.1; -fx-scale-y: 1.1;";
        
        chip.setStyle(baseStyle);
        chip.setOnMouseEntered(e -> chip.setStyle(hoverStyle));
        chip.setOnMouseExited(e -> chip.setStyle(baseStyle));
        chip.setOnAction(e -> goAllIn());
        
        return chip;
    }
    
    private void goAllIn() {
        currentBetAmount = playerBalance;
        currentBetLabel.setText("Current Bet: $" + currentBetAmount + " (ALL-IN!)");
        currentBetLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FF0000; " +
            "-fx-font-family: 'Arial';"
        );
    }
    
    private void addChipToBet(int value) {
        currentBetAmount += value;
        currentBetLabel.setText("Current Bet: $" + currentBetAmount);
    }
    
    private void placeBet() {
        if (currentBetAmount > 0) {
            onBet.accept(currentBetAmount);
        }
    }
    
    public void clearBet() {
        currentBetAmount = 0;
        currentBetLabel.setText("Current Bet: $0");
        currentBetLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + StyleConstants.GOLD + "; " +
            "-fx-font-family: 'Arial';"
        );
    }
    
    public void setPlayerBalance(int balance) {
        this.playerBalance = balance;
    }
    
    public void setOnBet(Consumer<Integer> handler) {
        this.onBet = handler;
    }
    
    public void setButtonsEnabled(boolean enabled) {
        betButton.setDisable(!enabled);
        clearBetButton.setDisable(!enabled);
        chipGrid.setDisable(!enabled);
    }
    
    public int getCurrentBetAmount() {
        return currentBetAmount;
    }
}
