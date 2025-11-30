package strategy;

/**
 * Classic Las Vegas blackjack rules
 * - 3:2 blackjack payout
 * - Dealer stands on soft 17
 * - Double after split allowed
 * - Surrender allowed
 * - 3 splits maximum
 * - Cannot resplit aces
 * - Dealer peeks for blackjack
 * 
 * STRATEGY PATTERN: Concrete Strategy implementation
 * FACTORY PATTERN: Concrete Product created by StrategyFactory
 * 
 * @author Group 12
 */
public class ClassicStrategy extends AbstractBlackjackStrategy {
    
    @Override
    public String getVariantName() {
        return "Classic Las Vegas";
    }
    
    @Override
    public String getRulesDescription() {
        return "3:2 blackjack • Dealer stands on soft 17 • Double after split • Surrender allowed";
    }
    
    @Override
    public double getWinPayoutMultiplier() {
        return 1.0; // Even money (1:1)
    }
    
    @Override
    public double getBlackjackPayoutMultiplier() {
        return 1.5; // 3:2 payout
    }
    
    @Override
    public boolean dealerHitsOnSoft17() {
        return false; // Dealer stands on soft 17
    }
    
    @Override
    public boolean canDoubleAfterSplit() {
        return true;
    }
    
    @Override
    public boolean allowsSurrender() {
        return true;
    }
    
    @Override
    public int getMaxSplits() {
        return 3;
    }
    
    @Override
    public boolean canResplitAces() {
        return false;
    }
    
    @Override
    public boolean dealerPeeksForBlackjack() {
        return true;
    }
}
