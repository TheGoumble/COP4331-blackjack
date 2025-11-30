package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import strategy.BlackjackStrategy;
import strategy.StrategyFactory;

import java.util.function.Consumer;

/**
 * Component for selecting blackjack game variants/strategies
 * 
 * @author Group 12
 */
public class StrategySelector extends VBox {
    
    private final ComboBox<String> variantComboBox;
    private final Label rulesLabel;
    private Consumer<BlackjackStrategy> onStrategyChange = strategy -> {};
    private BlackjackStrategy currentStrategy;
    
    public StrategySelector() {
        setSpacing(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(15));
        setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.3); " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        
        // Title
        Label titleLabel = new Label("Game Variant");
        titleLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #FFD700; " +
            "-fx-font-family: 'Arial';"
        );
        
        // Dropdown for variants
        variantComboBox = new ComboBox<>();
        variantComboBox.getItems().addAll(StrategyFactory.getAvailableVariants());
        variantComboBox.setValue("Classic Las Vegas"); // Default
        variantComboBox.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-background-color: white; " +
            "-fx-text-fill: black; " +
            "-fx-border-color: #FFD700; " +
            "-fx-border-width: 1px;"
        );
        variantComboBox.setPrefWidth(250);
        
        // Rules description
        rulesLabel = new Label();
        rulesLabel.setWrapText(true);
        rulesLabel.setMaxWidth(300);
        rulesLabel.setAlignment(Pos.CENTER);
        rulesLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Arial';"
        );
        
        // Update rules when variant changes
        variantComboBox.setOnAction(e -> updateStrategy());
        
        // Initial update
        updateStrategy();
        
        getChildren().addAll(titleLabel, variantComboBox, rulesLabel);
    }
    
    private void updateStrategy() {
        String variantName = variantComboBox.getValue();
        currentStrategy = StrategyFactory.getStrategy(variantName);
        rulesLabel.setText(currentStrategy.getRulesDescription());
        onStrategyChange.accept(currentStrategy);
    }
    
    public void setOnStrategyChange(Consumer<BlackjackStrategy> handler) {
        this.onStrategyChange = handler;
    }
    
    public BlackjackStrategy getCurrentStrategy() {
        return currentStrategy;
    }
    
    public void setStrategy(String variantName) {
        variantComboBox.setValue(variantName);
        updateStrategy();
    }
    
    public void disable() {
        variantComboBox.setDisable(true);
    }
    
    public void enable() {
        variantComboBox.setDisable(false);
    }
}
