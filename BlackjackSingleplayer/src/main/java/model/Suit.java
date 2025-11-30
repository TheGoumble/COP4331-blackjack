package model;

/**
 *
 * @author Bridjet Walker
 */
public enum Suit {
    CLUBS, DIAMONDS, HEARTS, SPADES;
    
    /**
     * Get the Unicode symbol for this suit
     */
    public String symbol() {
        return switch (this) {
            case CLUBS -> "♣";
            case DIAMONDS -> "♦";
            case HEARTS -> "♥";
            case SPADES -> "♠";
        };
    }
    
    /**
     * Check if this suit is red (hearts or diamonds)
     */
    public boolean isRed() {
        return this == HEARTS || this == DIAMONDS;
    }
}