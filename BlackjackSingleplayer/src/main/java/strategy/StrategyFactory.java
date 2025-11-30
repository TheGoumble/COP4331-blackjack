package strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Factory for creating and managing blackjack strategy instances
 * 
 * FACTORY PATTERN: This is the Factory (Creator)
 * The Factory creates and manages Product instances (BlackjackStrategy implementations)
 * 
 * @author Group 12
 */
public class StrategyFactory {
    
    private static final Map<String, BlackjackStrategy> strategies = new HashMap<>();
    
    static {
        // Register all available strategies
        registerStrategy(new ClassicStrategy());
        registerStrategy(new EuropeanStrategy());
        registerStrategy(new ModernCasinoStrategy());
    }
    
    /**
     * Register a strategy
     */
    private static void registerStrategy(BlackjackStrategy strategy) {
        strategies.put(strategy.getVariantName(), strategy);
    }
    
    /**
     * Get a strategy by variant name
     * @param variantName the name of the variant
     * @return the strategy, or Classic if not found
     */
    public static BlackjackStrategy getStrategy(String variantName) {
        return strategies.getOrDefault(variantName, new ClassicStrategy());
    }
    
    /**
     * Get all available variant names
     */
    public static Set<String> getAvailableVariants() {
        return strategies.keySet();
    }
    
    /**
     * Get the default strategy (Classic)
     */
    public static BlackjackStrategy getDefaultStrategy() {
        return new ClassicStrategy();
    }
}
