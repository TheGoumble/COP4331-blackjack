package strategy;

/**
 * European blackjack rules
 * - 3:2 blackjack payout
 * - Dealer hits on soft 17
 * - Double only on 9, 10, or 11
 * - No surrender
 * - Only 1 split allowed
 * - Cannot resplit aces
 * Dealer does NOT peek for blackjack (no hole card)
 * 
 * STRATEGY PATTERN: Concrete Strategy implementation
 * FACTORY PATTERN: Concrete Product created by StrategyFactory
 * 
 * @author Group 12
 */
public class EuropeanStrategy extends AbstractBlackjackStrategy {
    
    @Override
    public String getVariantName() {
        return "European Blackjack";
    }
    
    @Override
    public String getRulesDescription() {
        return "3:2 blackjack • Dealer hits soft 17 • No hole card • Limited double down";
    }
    
    @Override
    public double getWinPayoutMultiplier() {
        return 1.0;
    }
    
    @Override
    public double getBlackjackPayoutMultiplier() {
        return 1.5; // 3:2 payout
    }
    
    @Override
    public boolean dealerHitsOnSoft17() {
        return true; // European rules: dealer hits soft 17
    }
    
    @Override
    public boolean canDoubleAfterSplit() {
        return false; // More restrictive
    }
    
    @Override
    public boolean allowsSurrender() {
        return false; // No surrender in European blackjack
    }
    
    @Override
    public int getMaxSplits() {
        return 1; // Only one split allowed
    }
    
    @Override
    public boolean canResplitAces() {
        return false;
    }
    
    @Override
    public boolean dealerPeeksForBlackjack() {
        return false; // No hole card - dealer doesn't peek
    }
}
