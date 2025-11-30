package util;

/**
 * Central repository for CSS styling constants
 * Eliminates duplicate style strings across views
 * 
 * @author Group 12
 */
public class StyleConstants {
    
    // Colors
    public static final String GREEN_FELT = "#2d5016";
    public static final String DARK_GREEN = "#1a3d0f";
    public static final String GOLD = "#FFD700";
    public static final String LIGHT_GREEN = "#90EE90";
    public static final String WOOD_BROWN = "#8B4513";
    
    // Font families
    public static final String FONT_ARIAL = "'Arial'";
    
    // Common text styles
    public static final String TITLE_TEXT = 
        "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_FELT + "; -fx-font-family: " + FONT_ARIAL + ";";
    
    public static final String SUBTITLE_TEXT = 
        "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_FELT + "; -fx-font-family: " + FONT_ARIAL + ";";
    
    public static final String BODY_TEXT = 
        "-fx-font-size: 15px; -fx-text-fill: #333333; -fx-font-family: " + FONT_ARIAL + ";";
    
    public static final String SMALL_TEXT = 
        "-fx-font-size: 12px; -fx-text-fill: #555555; -fx-font-family: " + FONT_ARIAL + ";";
    
    public static final String WHITE_TEXT = 
        "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: " + FONT_ARIAL + ";";
    
    // Button styles
    public static final String BUTTON_BASE = 
        "-fx-font-size: 14px; " +
        "-fx-font-weight: bold; " +
        "-fx-background-color: white; " +
        "-fx-text-fill: " + GREEN_FELT + "; " +
        "-fx-padding: 10 20; " +
        "-fx-background-radius: 8; " +
        "-fx-cursor: hand; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 2);";
    
    public static final String BUTTON_HOVER = 
        "-fx-font-size: 14px; " +
        "-fx-font-weight: bold; " +
        "-fx-background-color: " + GOLD + "; " +
        "-fx-text-fill: " + GREEN_FELT + "; " +
        "-fx-padding: 10 20; " +
        "-fx-background-radius: 8; " +
        "-fx-cursor: hand; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.5), 10, 0, 0, 3);";
    
    public static final String DANGER_BUTTON_BASE = 
        "-fx-font-size: 14px; " +
        "-fx-font-weight: bold; " +
        "-fx-background-color: #DC143C; " +
        "-fx-text-fill: white; " +
        "-fx-padding: 10 20; " +
        "-fx-background-radius: 8; " +
        "-fx-cursor: hand; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 2);";
    
    public static final String DANGER_BUTTON_HOVER = 
        "-fx-font-size: 14px; " +
        "-fx-font-weight: bold; " +
        "-fx-background-color: #B22222; " +
        "-fx-text-fill: white; " +
        "-fx-padding: 10 20; " +
        "-fx-background-radius: 8; " +
        "-fx-cursor: hand; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(220,20,60,0.6), 10, 0, 0, 3);";
    
    // Panel styles
    public static final String WHITE_PANEL = 
        "-fx-background-color: white; " +
        "-fx-background-radius: 10; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 2, 2);";
    
    public static final String DARK_PANEL = 
        "-fx-background-color: rgba(0,0,0,0.5); " +
        "-fx-background-radius: 8; " +
        "-fx-border-color: rgba(255,215,0,0.5); " +
        "-fx-border-width: 2; " +
        "-fx-border-radius: 8;";
    
    public static final String HIGHLIGHTED_PANEL = 
        "-fx-background-color: rgba(255,215,0,0.4); " +
        "-fx-background-radius: 8; " +
        "-fx-border-color: " + GOLD + "; " +
        "-fx-border-width: 4; " +
        "-fx-border-radius: 8; " +
        "-fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.8), 12, 0, 0, 0);";
    
    // Separator
    public static final String SEPARATOR = 
        "-fx-background-color: rgba(255,255,255,0.3); -fx-pref-height: 2;";
    
    // Background
    public static final String GREEN_BACKGROUND = 
        "-fx-background-color: " + GREEN_FELT + ";";
    
    public static final String GRADIENT_BACKGROUND = 
        "-fx-background-color: linear-gradient(to bottom, " + DARK_GREEN + ", " + GREEN_FELT + ");";
    
    // Oval table
    public static final String OVAL_TABLE = 
        "-fx-background-color: " + DARK_GREEN + "; " +
        "-fx-background-radius: 350px / 220px; " +
        "-fx-border-color: " + WOOD_BROWN + "; " +
        "-fx-border-width: 8; " +
        "-fx-border-radius: 350px / 220px; " +
        "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5);";
    
    /**
     * Apply standard button styling with hover effects
     */
    public static void styleButton(javafx.scene.control.Button button) {
        button.setStyle(BUTTON_BASE);
        button.setOnMouseEntered(e -> button.setStyle(BUTTON_HOVER));
        button.setOnMouseExited(e -> button.setStyle(BUTTON_BASE));
    }
    
    /**
     * Apply danger button styling with hover effects
     */
    public static void styleDangerButton(javafx.scene.control.Button button) {
        button.setStyle(DANGER_BUTTON_BASE);
        button.setOnMouseEntered(e -> button.setStyle(DANGER_BUTTON_HOVER));
        button.setOnMouseExited(e -> button.setStyle(DANGER_BUTTON_BASE));
    }
    
    /**
     * Create a styled label with specific text style
     */
    public static javafx.scene.control.Label createStyledLabel(String text, String style) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setStyle(style);
        return label;
    }
}
