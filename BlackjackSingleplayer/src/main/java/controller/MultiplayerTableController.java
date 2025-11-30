package controller;

import app.SceneRouter;
import command.*;
import model.Card;
import network.BlackjackPeer;
import network.DesignatedHost;
import network.GameUpdateMessage;
import view.MultiplayerTableView;

import java.util.List;
import java.util.Map;

/**
 * Controller for multiplayer table
 * Handles player commands and updates UI based on game state
 * 
 * @author Javier Vargas, Group 12
 */
public class MultiplayerTableController {

    private final BlackjackPeer peer;
    private final DesignatedHost host;
    private final MultiplayerTableView view;
    private final SceneRouter router;
    private final boolean isHost;
    private boolean roundInProgress = false;

    public MultiplayerTableController(BlackjackPeer peer, DesignatedHost host, MultiplayerTableView view, SceneRouter router, String gameCode) {
        this.peer = peer;
        this.host = host;
        this.view = view;
        this.router = router;
        this.isHost = (host != null && host.getHostId().equals(peer.getUserId()));

        // Set game code/connection info in view
        if (isHost && host != null) {
            // Set game code first
            view.setGameCode(gameCode);
            // Then set connection info which will use the game code
            view.setConnectionInfo(host.getConnectionString());
        } else {
            // Client just shows they're connected
            view.setGameCode("Connected");
        }

        // Wire up handlers
        view.setOnBet(this::handleBet);
        view.setOnHit(this::handleHit);
        view.setOnStand(this::handleStand);
        view.setOnStartRound(this::handleStartRound);
        view.setOnBackToMenu(this::handleBackToMenu);

        // Listen for game updates
        peer.addUpdateListener(this::handleGameUpdate);

        // Initial render
        updateUI();
    }

    private void handleBackToMenu() {
        // Disconnect from game
        peer.disconnect();
        
        // If this player is the host, shutdown server (unregisters from API)
        if (isHost && host != null) {
            host.shutdown();
        }
        
        // Navigate back to menu
        router.showMenu();
    }

    private void handleBet(int amount) {
        try {
            // Check if cards have been dealt (betting phase only)
            List<Card> dealerCards = peer.getDealerHand();
            if (!dealerCards.isEmpty()) {
                view.showMessage("Cannot bet after cards are dealt!");
                return;
            }
            
            // Check if this player has already bet
            Map<String, Integer> playerBets = peer.getPlayerBets();
            int currentBet = playerBets.getOrDefault(peer.getUserId(), 0);
            if (currentBet > 0) {
                view.showMessage("You have already placed your bet ($" + currentBet + ")!");
                return;
            }
            
            SetBetCommand cmd = new SetBetCommand(peer.getUserId(), amount);
            peer.sendCommand(cmd);
            view.showMessage("Bet placed: $" + amount);
        } catch (Exception e) {
            view.showMessage("Error: " + e.getMessage());
        }
    }

    private void handleHit() {
        try {
            HitCommand cmd = new HitCommand(peer.getUserId());
            peer.sendCommand(cmd);
            view.showMessage("Hit!");
        } catch (Exception e) {
            view.showMessage("Error: " + e.getMessage());
        }
    }

    private void handleStand() {
        try {
            StandCommand cmd = new StandCommand(peer.getUserId());
            peer.sendCommand(cmd);
            view.showMessage("Standing...");
        } catch (Exception e) {
            view.showMessage("Error: " + e.getMessage());
        }
    }

    private void handleStartRound() {
        if (isHost && host != null) {
            try {
                host.startRound();
                roundInProgress = true;
                view.showMessage("Round started!");
                updateUI();
            } catch (Exception e) {
                view.showMessage("Error: " + e.getMessage());
            }
        }
    }

    private void handleGameUpdate(GameUpdateMessage message) {
        javafx.application.Platform.runLater(() -> {
            switch (message.getType()) {
                case PLAYERS_LIST:
                    // Received list of existing players
                    view.showMessage("Connected! Loading players...");
                    updateUI();
                    break;
                    
                case PLAYER_JOINED:
                    view.showMessage( message.getData() + " joined the game!");
                    peer.requestGameState();
                    updateUI();
                    break;

                case PLAYER_LEFT:
                    view.showMessage(message.getData() + " left the game");
                    view.removePlayer((String) message.getData());
                    break;

                case BET_PLACED:
                    view.showMessage(message.getData() + " placed their bet");
                    break;

                case ROUND_STARTED:
                    roundInProgress = true;
                    view.showMessage("Round started! Place your bets.");
                    view.setTurnIndicator("BETTING PHASE - All players place bets!");
                    peer.requestGameState();
                    updateUI();
                    break;

                case PLAYER_HIT:
                    view.showMessage(message.getData() + " drew a card (HIT)");
                    peer.requestGameState();
                    updateUI();
                    break;

                case PLAYER_STAND:
                    view.showMessage(message.getData() + " stands");
                    break;
                
                case TURN_CHANGED:
                    // Turn has changed - update UI to reflect whose turn
                    updateUI();
                    break;

                case DEALER_TURN:
                    view.showMessage("Dealer's turn...");
                    view.setTurnIndicator("DEALER'S TURN");
                    peer.requestGameState();
                    updateUI();
                    break;

                case ROUND_ENDED:
                    roundInProgress = false;
                    @SuppressWarnings("unchecked")
                    Map<String, model.GameResult> results = 
                        (Map<String, model.GameResult>) message.getData();
                    
                    if (results.containsKey(peer.getUserId())) {
                        model.GameResult result = results.get(peer.getUserId());
                        String emoji = getResultEmoji(result);
                        view.showMessage(emoji + " Round ended! You: " + result);
                        view.setTurnIndicator(emoji + " Result: " + result);
                    } else {
                        view.showMessage("Round ended! Check results.");
                        view.setTurnIndicator("Round Over");
                    }
                    peer.requestGameState();
                    updateUI();
                    break;
                    
                case SPECTATOR_MODE:
                    view.showMessage("⏸️ You joined mid-game. You'll play in the next round!");
                    view.setTurnIndicator("SPECTATING - Waiting for next round...");
                    updateUI();
                    break;

                case GAME_STATE_UPDATE:
                    updateUI();
                    break;
                    
                case HOST_DISCONNECTED:
                    // Host has disconnected - show error and return to menu
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR
                    );
                    alert.setTitle("Host Disconnected");
                    alert.setHeaderText("Connection Lost");
                    alert.setContentText("The host has disconnected from the game. Returning to menu...");
                    alert.showAndWait();
                    
                    // Return to menu
                    peer.disconnect();
                    router.showMenu();
                    break;

                default:
                    break;
            }
        });
    }
    
    private String getResultEmoji(model.GameResult result) {
        switch (result) {
            case PLAYER_WIN: return "[WIN]";
            case PLAYER_BLACKJACK: return "[BLACKJACK]";
            case PUSH: return "[PUSH]";
            case DEALER_WIN: return "[LOSE]";
            default: return "[RESULT]";
        }
    }

    private void updateUI() {
        // Update dealer
        List<Card> dealerHand = peer.getDealerHand();
        view.updateDealer(dealerHand, !roundInProgress);

        // Update all players
        Map<String, List<Card>> playerHands = peer.getPlayerHands();
        Map<String, Integer> playerBalances = peer.getPlayerBalances();
        Map<String, Integer> playerBets = peer.getPlayerBets();
        Map<String, String> playerDisplayNames = peer.getPlayerDisplayNames();

        for (String playerId : playerHands.keySet()) {
            List<Card> hand = playerHands.get(playerId);
            int balance = playerBalances.getOrDefault(playerId, 0);
            int betAmount = playerBets.getOrDefault(playerId, 0);
            boolean isActive = playerId.equals(peer.getUserId()) && roundInProgress;
            
            // Use display name - show "You" for current player, otherwise use their display name
            String displayName;
            if (playerId.equals(peer.getUserId())) {
                displayName = "You (" + peer.getDisplayName() + ")";
            } else {
                // Get display name from synced map, fallback to userId if not found
                displayName = playerDisplayNames.getOrDefault(playerId, playerId);
            }
            view.updatePlayer(playerId, displayName, hand, balance, isActive);
            view.updatePlayerBet(playerId, betAmount);
        }

        // Update my balance
        view.updateMyBalance(peer.getMyBalance());

        // Check if spectating
        boolean isSpectating = peer.isSpectating();
        
        // Update button states based on game phase and turn
        List<Card> dealerCards = peer.getDealerHand();
        boolean cardsDealt = !dealerCards.isEmpty();
        boolean isMyTurn = peer.isMyTurn();
        
        // Spectators cannot interact with the game
        if (isSpectating) {
            view.setButtonsEnabled(false, false, false, false);
            view.setTurnIndicator("⏸️ SPECTATING - You'll play in the next round");
            return;
        }
        
        // Can bet during round but before cards are dealt
        boolean canBet = roundInProgress && !cardsDealt;
        
        // Can hit/stand only after cards dealt and during player's turn
        boolean canHit = roundInProgress && cardsDealt && isMyTurn;
        boolean canStand = roundInProgress && cardsDealt && isMyTurn;
        
        // Can start round when not in progress
        boolean canStartRound = isHost && !roundInProgress;

        view.setButtonsEnabled(canBet, canHit, canStand, canStartRound);
        
        // Update turn indicator
        if (!roundInProgress) {
            if (isHost) {
                view.setTurnIndicator("Waiting - Click 'Start Round' to begin");
            } else {
                view.setTurnIndicator("Waiting for host to start round");
            }
        } else if (!cardsDealt) {
            // Betting phase
            view.setTurnIndicator("BETTING PHASE - All players place bets!");
        } else {
            // Playing phase - show whose turn it is
            String currentTurnPlayer = peer.getCurrentTurnPlayer();
            if (currentTurnPlayer == null) {
                view.setTurnIndicator("All players finished - Waiting for dealer");
            } else if (currentTurnPlayer.equals(peer.getUserId())) {
                view.setTurnIndicator("YOUR TURN - Hit or Stand!");
            } else {
                // Show other player's name
                Map<String, String> displayNames = peer.getPlayerDisplayNames();
                String turnPlayerName = displayNames.getOrDefault(currentTurnPlayer, currentTurnPlayer);
                view.setTurnIndicator(turnPlayerName + "'s Turn - Please wait...");
            }
        }
    }
}
