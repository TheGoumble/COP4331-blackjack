package strategy;

/**
 * Modern casino blackjack with worse odds for players
 * - 6:5 blackjack payout (instead of 3:2)
 * - Dealer hits on soft 17
 * - No surrender
 * - Limited splits
 * - Cannot resplit aces
 * - Dealer peeks for blackjack
 * 
 * This variant is commonly found in modern casinos but less favorable to players
 * 
 * STRATEGY PATTERN: Concrete Strategy implementation
 * FACTORY PATTERN: Concrete Product created by StrategyFactory
 * 
 * @author Group 12
 */
public class ModernCasinoStrategy extends AbstractBlackjackStrategy {
    
    @Override
    public String getVariantName() {
        return "Modern Casino (6:5)";
    }
    
    @Override
    public String getRulesDescription() {
        return "6:5 blackjack • Dealer hits soft 17 • No surrender • Limited splits";
    }
    
    @Override
    public double getWinPayoutMultiplier() {
        return 1.0;
    }
    
    @Override
    public double getBlackjackPayoutMultiplier() {
        return 1.2; // 6:5 payout (worse for player)
    }
    
    @Override
    public boolean dealerHitsOnSoft17() {
        return true; // Worse for player
    }
    
    @Override
    public boolean canDoubleAfterSplit() {
        return true;
    }
    
    @Override
    public boolean allowsSurrender() {
        return false; // No surrender
    }
    
    @Override
    public int getMaxSplits() {
        return 1; // Limited splits
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
