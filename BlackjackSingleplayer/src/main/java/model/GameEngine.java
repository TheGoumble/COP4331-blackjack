package model;

import strategy.BlackjackStrategy;
import strategy.StrategyFactory;
import java.util.*;

/**
 * Multi-player game engine that manages game state for all players
 * Handles deck, player hands, balances, and game logic
 * Uses Strategy Pattern for different blackjack rule variants
 * 
 * MVC PATTERN: This is a Model (business logic)
 * STRATEGY PATTERN: This is the Context
 * 
 * @author Group 12
 */
public class GameEngine {
    private final Deck deck;
    private final Map<String, Integer> playerBalances;
    private final Map<String, List<Card>> currentHands;
    private final Map<String, Integer> currentBets;
    private final List<Card> dealerHand;
    private final Set<String> standingPlayers;
    private final Set<String> bustedPlayers;
    private final List<String> playerOrder; // Track join order for turns
    private final Set<String> spectators; // Players who joined mid-game
    private BlackjackStrategy strategy; // STRATEGY PATTERN: Reference to Strategy interface
    
    private boolean roundInProgress;
    private int currentPlayerIndex; // Index in playerOrder for whose turn it is
    
    public GameEngine() {
        this(StrategyFactory.getDefaultStrategy());
    }
    
    public GameEngine(BlackjackStrategy strategy) {
        this.deck = new Deck();
        this.playerBalances = new HashMap<>();
        this.currentHands = new HashMap<>();
        this.currentBets = new HashMap<>();
        this.dealerHand = new ArrayList<>();
        this.standingPlayers = new HashSet<>();
        this.bustedPlayers = new HashSet<>();
        this.playerOrder = new ArrayList<>();
        this.spectators = new HashSet<>();
        this.strategy = strategy;
        this.roundInProgress = false;
        this.currentPlayerIndex = 0;
    }
    
    /**
     * Add a player to the game
     */
    public void addPlayer(String playerId, int startingBalance) {
        playerBalances.put(playerId, startingBalance);
        currentHands.put(playerId, new ArrayList<>());
        currentBets.put(playerId, 0);
        
        // If round is in progress, add as spectator
        if (roundInProgress) {
            spectators.add(playerId);
            System.out.println("[GAME] Player " + playerId + " joined as spectator (round in progress)");
        } else {
            playerOrder.add(playerId); // Track join order
        }
    }
    
    /**
     * Remove a player from the game
     */
    public void removePlayer(String playerId) {
        playerBalances.remove(playerId);
        currentHands.remove(playerId);
        currentBets.remove(playerId);
        standingPlayers.remove(playerId);
        spectators.remove(playerId);
        
        // Remove from player order and adjust current turn if needed
        int removedIndex = playerOrder.indexOf(playerId);
        playerOrder.remove(playerId);
        
        // If removed player was before or at current turn, adjust index
        if (removedIndex >= 0 && removedIndex < currentPlayerIndex && roundInProgress) {
            currentPlayerIndex--;
            System.out.println("[GAME] Adjusted turn index after player removal");
        }
        // If removed player was the current player, the index now points to next player
        // which is correct behavior
    }
    
    /**
     * Process a bet from a player (during betting phase)
     */
    public void processBet(String playerId, int amount) {
        if (!roundInProgress) {
            throw new IllegalStateException("Round not started - host must start round first");
        }
        
        // Check if cards have already been dealt
        if (!dealerHand.isEmpty()) {
            throw new IllegalStateException("Cannot bet after cards are dealt");
        }
        
        if (!playerBalances.containsKey(playerId)) {
            throw new IllegalArgumentException("Player not found: " + playerId);
        }
        
        int balance = playerBalances.get(playerId);
        if (amount > balance) {
            throw new IllegalStateException("Insufficient balance");
        }
        
        if (amount < 1) {
            throw new IllegalArgumentException("Bet must be at least 1");
        }
        
        // Deduct bet from balance
        playerBalances.put(playerId, balance - amount);
        currentBets.put(playerId, amount);
    }
    
    /**
     * Start a new round - enables betting phase
     */
    public void startRound() {
        if (roundInProgress) {
            throw new IllegalStateException("Round already in progress");
        }
        
        // Clear previous hands and bets
        for (String playerId : currentHands.keySet()) {
            currentHands.get(playerId).clear();
        }
        dealerHand.clear();
        standingPlayers.clear();
        bustedPlayers.clear();
        currentBets.replaceAll((k, v) -> 0); // Reset all bets to 0
        
        // Promote spectators to active players at start of new round
        for (String spectator : spectators) {
            playerOrder.add(spectator);
            System.out.println("[GAME] Spectator " + spectator + " promoted to active player");
        }
        spectators.clear();
        
        // Mark round as in progress (betting phase)
        roundInProgress = true;
        currentPlayerIndex = 0; // Start with first player in join order
    }
    
    /**
     * Deal cards after all players have placed their bets
     */
    public void dealCards() {
        if (!roundInProgress) {
            throw new IllegalStateException("No round in progress");
        }
        
        // Check if all players have placed bets
        for (String playerId : currentBets.keySet()) {
            if (currentBets.get(playerId) == 0) {
                throw new IllegalStateException("All players must place bets before dealing");
            }
        }
        
        // Shuffle deck
        deck.shuffle();
        
        // Deal initial cards (2 to each player, 2 to dealer)
        for (String playerId : currentHands.keySet()) {
            currentHands.get(playerId).add(deck.deal());
        }
        dealerHand.add(deck.deal());
        
        for (String playerId : currentHands.keySet()) {
            currentHands.get(playerId).add(deck.deal());
        }
        dealerHand.add(deck.deal());
        
        // Check for dealer blackjack (instant win for dealer)
        if (dealerHand.size() == 2 && calculateHandValue(dealerHand) == 21) {
            // Dealer has blackjack - all players with non-blackjack lose immediately
            for (String playerId : currentHands.keySet()) {
                List<Card> hand = currentHands.get(playerId);
                if (hand.size() == 2 && calculateHandValue(hand) == 21) {
                    // Player also has blackjack - push
                    standingPlayers.add(playerId);
                } else {
                    // Player loses to dealer blackjack
                    standingPlayers.add(playerId);
                }
            }
        }
    }
    
    /**
     * Process a hit command - deal a card to the player
     */
    public void processHit(String playerId) {
        if (!roundInProgress) {
            throw new IllegalStateException("No round in progress");
        }
        
        if (!currentHands.containsKey(playerId)) {
            throw new IllegalArgumentException("Player not found: " + playerId);
        }
        
        // Check if it's this player's turn
        if (!isPlayersTurn(playerId)) {
            throw new IllegalStateException("Not your turn");
        }
        
        if (standingPlayers.contains(playerId)) {
            throw new IllegalStateException("Player has already stood");
        }
        
        if (bustedPlayers.contains(playerId)) {
            throw new IllegalStateException("Player has busted");
        }
        
        List<Card> hand = currentHands.get(playerId);
        hand.add(deck.deal());
        
        // Check for bust
        if (calculateHandValue(hand) > 21) {
            standingPlayers.add(playerId);
            bustedPlayers.add(playerId);
            advanceToNextPlayer(); // Auto-advance turn when busted
        }
    }
    
    /**
     * Process a stand command - player ends their turn
     */
    public void processStand(String playerId) {
        if (!roundInProgress) {
            throw new IllegalStateException("No round in progress");
        }
        
        if (!currentHands.containsKey(playerId)) {
            throw new IllegalArgumentException("Player not found: " + playerId);
        }
        
        // Check if it's this player's turn
        if (!isPlayersTurn(playerId)) {
            throw new IllegalStateException("Not your turn");
        }
        
        standingPlayers.add(playerId);
        advanceToNextPlayer(); // Advance to next player after standing
    }
    
    /**
     * Check if all players have finished their turns
     */
    public boolean allPlayersFinished() {
        return standingPlayers.size() == currentHands.size();
    }
    
    /**
     * Play dealer's turn (hits on 16 or less, stands on 17+)
     */
    public void playDealerTurn() {
        int dealerValue = calculateHandValue(dealerHand);
        while (dealerValue < 17) {
            dealerHand.add(deck.deal());
            dealerValue = calculateHandValue(dealerHand);
        }
    }
    
    /**
     * Calculate hand value (handles Ace as 1 or 11)
     */
    private int calculateHandValue(List<Card> hand) {
        int value = 0;
        int aces = 0;
        
        for (Card card : hand) {
            int cardValue = card.baseValue();
            if (cardValue == 11) {
                aces++;
            }
            value += cardValue;
        }
        
        // Adjust for Aces
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }
        
        return value;
    }
    
    /**
     * Determine winners and pay out
     */
    public Map<String, GameResult> determineResults() {
        Map<String, GameResult> results = new HashMap<>();
        int dealerValue = calculateHandValue(dealerHand);
        boolean dealerBust = dealerValue > 21;
        
        for (String playerId : currentHands.keySet()) {
            List<Card> hand = currentHands.get(playerId);
            int playerValue = calculateHandValue(hand);
            int bet = currentBets.get(playerId);
            
            GameResult result;
            
            if (playerValue > 21) {
                result = GameResult.DEALER_WIN; // Player bust
                // Bet already deducted
            } else if (hand.size() == 2 && playerValue == 21 && !(dealerHand.size() == 2 && dealerValue == 21)) {
                // Blackjack (pays 3:2 = bet + 1.5x bet)
                result = GameResult.PLAYER_BLACKJACK;
                int payout = bet + bet + (bet / 2); // Return bet + 1.5x bet
                playerBalances.put(playerId, playerBalances.get(playerId) + payout);
            } else if (hand.size() == 2 && playerValue == 21 && dealerHand.size() == 2 && dealerValue == 21) {
                // Both have blackjack - push
                result = GameResult.PUSH;
                playerBalances.put(playerId, playerBalances.get(playerId) + bet); // Return bet
            } else if (dealerBust) {
                result = GameResult.PLAYER_WIN;
                playerBalances.put(playerId, playerBalances.get(playerId) + bet * 2);
            } else if (playerValue > dealerValue) {
                result = GameResult.PLAYER_WIN;
                playerBalances.put(playerId, playerBalances.get(playerId) + bet * 2);
            } else if (playerValue == dealerValue) {
                result = GameResult.PUSH;
                playerBalances.put(playerId, playerBalances.get(playerId) + bet); // Return bet
            } else {
                result = GameResult.DEALER_WIN;
                // Bet already deducted
            }
            
            results.put(playerId, result);
        }
        
        roundInProgress = false;
        return results;
    }
    
    // Getters
    public Map<String, Integer> getPlayerBalances() {
        return new HashMap<>(playerBalances);
    }
    
    public Map<String, List<Card>> getCurrentHands() {
        Map<String, List<Card>> copy = new HashMap<>();
        for (Map.Entry<String, List<Card>> entry : currentHands.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }
    
    public List<Card> getDealerHand() {
        return new ArrayList<>(dealerHand);
    }
    
    public int getPlayerBalance(String playerId) {
        return playerBalances.getOrDefault(playerId, 0);
    }
    
    public List<Card> getPlayerHand(String playerId) {
        return new ArrayList<>(currentHands.getOrDefault(playerId, new ArrayList<>()));
    }
    
    public boolean isRoundInProgress() {
        return roundInProgress;
    }
    
    public Set<String> getPlayerIds() {
        return new HashSet<>(playerBalances.keySet());
    }
    
    public boolean hasPlayerBusted(String playerId) {
        return bustedPlayers.contains(playerId);
    }
    
    public int getPlayerBet(String playerId) {
        return currentBets.getOrDefault(playerId, 0);
    }
    
    public Map<String, Integer> getPlayerBets() {
        return new HashMap<>(currentBets);
    }
    
    /**
     * Get the ID of the player whose turn it currently is
     * Returns null if no round in progress or all players finished
     */
    public String getCurrentPlayer() {
        if (!roundInProgress || playerOrder.isEmpty()) {
            return null;
        }
        if (currentPlayerIndex >= playerOrder.size()) {
            return null; // All players finished
        }
        return playerOrder.get(currentPlayerIndex);
    }
    
    /**
     * Check if it's a specific player's turn
     */
    public boolean isPlayersTurn(String playerId) {
        String currentPlayer = getCurrentPlayer();
        return currentPlayer != null && currentPlayer.equals(playerId);
    }
    
    /**
     * Check if all players have placed their bets
     */
    public boolean allBetsPlaced() {
        if (!roundInProgress) {
            return false;
        }
        for (Integer bet : currentBets.values()) {
            if (bet == 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Advance to the next player's turn
     * Skips players who have already stood or busted
     */
    public void advanceToNextPlayer() {
        if (!roundInProgress) {
            return;
        }
        
        // Move to next player
        currentPlayerIndex++;
        
        // Skip players who have already stood
        while (currentPlayerIndex < playerOrder.size()) {
            String playerId = playerOrder.get(currentPlayerIndex);
            if (!standingPlayers.contains(playerId)) {
                return; // Found next active player
            }
            currentPlayerIndex++;
        }
        
        // If we've gone through all players, no current player (ready for dealer)
    }
    
    /**
     * Get the turn order (list of player IDs in join order)
     */
    public List<String> getPlayerOrder() {
        return new ArrayList<>(playerOrder);
    }
    
    /**
     * Check if a player is currently spectating
     */
    public boolean isSpectator(String playerId) {
        return spectators.contains(playerId);
    }
    
    /**
     * Get all spectators
     */
    public Set<String> getSpectators() {
        return new HashSet<>(spectators);
    }
    
    /**
     * Get the current strategy being used
     */
    public BlackjackStrategy getStrategy() {
        return strategy;
    }
    
    /**
     * Change the game strategy (can only be done between rounds)
     */
    public void setStrategy(BlackjackStrategy newStrategy) {
        if (roundInProgress) {
            throw new IllegalStateException("Cannot change strategy during an active round");
        }
        this.strategy = newStrategy;
        System.out.println("[GAME] Strategy changed to: " + strategy.getVariantName());
    }
}
