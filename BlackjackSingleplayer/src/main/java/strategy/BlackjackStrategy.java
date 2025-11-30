package strategy;

import model.Hand;

/**
 * Strategy interface for different blackjack rule variations
 * 
 * STRATEGY PATTERN: This is the Strategy interface
 * FACTORY PATTERN: This is the Product interface
 * 
 * @author Group 12
 */
public interface BlackjackStrategy {
    
    /**
     * Get the name of this blackjack variant
     */
    String getVariantName();
    
    /**
     * Get description of the rules for this variant
     */
    String getRulesDescription();
    
    /**
     * Calculate payout multiplier for a win
     * @return payout multiplier (e.g., 1.0 for even money, 1.5 for 3:2)
     */
    double getWinPayoutMultiplier();
    
    /**
     * Calculate payout multiplier for a blackjack
     * @return payout multiplier (e.g., 1.5 for 3:2, 1.2 for 6:5)
     */
    double getBlackjackPayoutMultiplier();
    
    /**
     * Determine if dealer must hit on soft 17
     * @return true if dealer hits on soft 17, false if dealer stands
     */
    boolean dealerHitsOnSoft17();
    
    /**
     * Determine if player can double down after split
     * @return true if allowed, false otherwise
     */
    boolean canDoubleAfterSplit();
    
    /**
     * Determine if surrender is allowed
     * @return true if surrender is allowed, false otherwise
     */
    boolean allowsSurrender();
    
    /**
     * Get the maximum number of splits allowed per hand
     * @return number of splits (0 means no splits allowed)
     */
    int getMaxSplits();
    
    /**
     * Determine if player can resplit aces
     * @return true if allowed, false otherwise
     */
    boolean canResplitAces();
    
    /**
     * Determine if dealer peeks for blackjack with Ace showing
     * @return true if dealer peeks, false otherwise
     */
    boolean dealerPeeksForBlackjack();
    
    /**
     * Calculate the value of a hand according to this strategy's rules
     * @param hand the hand to evaluate
     * @return the hand value
     */
    int calculateHandValue(Hand hand);
    
    /**
     * Determine if this hand is a blackjack according to this variant's rules
     * @param hand the hand to check
     * @return true if it's a blackjack
     */
    boolean isBlackjack(Hand hand);
}
