package model;

import java.util.*;

/**
 * Multi-player game engine that manages game state for all players
 * Handles deck, player hands, balances, and game logic
 * 
 * @author Javier Vargas, Group 12
 */
public class GameEngine {
    private final Deck deck;
    private final Map<String, Integer> playerBalances;
    private final Map<String, List<Card>> currentHands;
    private final Map<String, Integer> currentBets;
    private final List<Card> dealerHand;
    private final Set<String> standingPlayers;
    private final Set<String> bustedPlayers;
    
    private boolean roundInProgress;
    private boolean bettingPhase;
    
    public GameEngine() {
        this.deck = new Deck();
        this.playerBalances = new HashMap<>();
        this.currentHands = new HashMap<>();
        this.currentBets = new HashMap<>();
        this.dealerHand = new ArrayList<>();
        this.standingPlayers = new HashSet<>();
        this.bustedPlayers = new HashSet<>();
        this.roundInProgress = false;
        this.bettingPhase = false;
    }
    
    /**
     * Add a player to the game
     */
    public void addPlayer(String playerId, int startingBalance) {
        playerBalances.put(playerId, startingBalance);
        currentHands.put(playerId, new ArrayList<>());
        currentBets.put(playerId, 0);
    }
    
    /**
     * Remove a player from the game
     */
    public void removePlayer(String playerId) {
        playerBalances.remove(playerId);
        currentHands.remove(playerId);
        currentBets.remove(playerId);
        standingPlayers.remove(playerId);
    }
    
    /**
     * Process a bet from a player (only before round starts)
     */
    public void processBet(String playerId, int amount) {
        if (roundInProgress) {
            throw new IllegalStateException("Cannot bet during active round");
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
     * Start a new round after all bets are placed
     */
    public void startRound() {
        if (roundInProgress) {
            throw new IllegalStateException("Round already in progress");
        }
        
        // Clear previous hands
        for (String playerId : currentHands.keySet()) {
            currentHands.get(playerId).clear();
        }
        dealerHand.clear();
        standingPlayers.clear();
        bustedPlayers.clear();
        
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
        
        roundInProgress = true;
        
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
        
        standingPlayers.add(playerId);
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
        while (calculateHandValue(dealerHand) < 17) {
            dealerHand.add(deck.deal());
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
}
