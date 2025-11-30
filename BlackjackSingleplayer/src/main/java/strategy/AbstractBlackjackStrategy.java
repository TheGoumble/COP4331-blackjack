package strategy;

import model.Card;
import model.CardInPlay;
import model.Hand;
import model.Rank;

/**
 * Abstract base class for blackjack strategies with common functionality
 * 
 * STRATEGY PATTERN: Optional abstract layer between Strategy and Concrete Strategies
 * FACTORY PATTERN: Abstract Product base class
 * 
 * @author Group 12
 */
public abstract class AbstractBlackjackStrategy implements BlackjackStrategy {
    
    @Override
    public int calculateHandValue(Hand hand) {
        int value = 0;
        int aces = 0;
        
        for (CardInPlay cip : hand.cards()) {
            Card card = cip.card();
            Rank rank = card.rank();
            if (rank == Rank.ACE) {
                aces++;
                value += 11;
            } else {
                value += rank.value();
            }
        }
        
        // Adjust for aces if busted
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }
        
        return value;
    }
    
    @Override
    public boolean isBlackjack(Hand hand) {
        // Standard blackjack: exactly 2 cards totaling 21
        return hand.cards().size() == 2 && calculateHandValue(hand) == 21;
    }
    
    /**
     * Determine if a hand is a soft hand (has an ace counted as 11)
     */
    protected boolean isSoftHand(Hand hand) {
        int value = 0;
        boolean hasAce = false;
        
        for (CardInPlay cip : hand.cards()) {
            Card card = cip.card();
            Rank rank = card.rank();
            if (rank == Rank.ACE) {
                hasAce = true;
                value += 11;
            } else {
                value += rank.value();
            }
        }
        
        // It's a soft hand if we have an ace counted as 11 and haven't busted
        return hasAce && value <= 21;
    }
}
