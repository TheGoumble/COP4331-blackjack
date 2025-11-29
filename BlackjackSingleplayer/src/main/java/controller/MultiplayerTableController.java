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
    private final String sessionId;
    private final boolean isHost;
    private boolean roundInProgress = false;

    public MultiplayerTableController(BlackjackPeer peer, DesignatedHost host, MultiplayerTableView view, SceneRouter router, String sessionId) {
        this.peer = peer;
        this.host = host;
        this.view = view;
        this.router = router;
        this.sessionId = sessionId;
        this.isHost = (host != null && host.getHostId().equals(peer.getUserId()));

        // Set game code/connection info in view
        if (isHost && host != null) {
            view.setConnectionInfo(host.getConnectionString());
        } else {
            view.setGameCode(sessionId);
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
        
        // If this player is the host, shutdown server and stop broadcasting
        if (isHost && host != null) {
            network.GameDiscoveryService.getInstance().stopBroadcasting();
            host.shutdown();
        }
        
        // Navigate back to menu
        router.showMenu();
    }

    private void handleBet(int amount) {
        try {
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
                case PLAYER_JOINED:
                    view.showMessage( message.getData() + " joined the game!");
                    peer.requestGameState();
                    updateUI();
                    break;

                case PLAYER_LEFT:
                    view.showMessage("❌ " + message.getData() + " left the game");
                    view.removePlayer((String) message.getData());
                    break;

                case BET_PLACED:
                    view.showMessage("💰 " + message.getData() + " placed their bet");
                    break;

                case ROUND_STARTED:
                    roundInProgress = true;
                    view.showMessage("🎲 Round started! Place your bets and make your moves.");
                    view.setTurnIndicator("▶ BETTING PHASE - Place your bets!");
                    peer.requestGameState();
                    updateUI();
                    break;

                case PLAYER_HIT:
                    view.showMessage("🂠 " + message.getData() + " drew a card (HIT)");
                    peer.requestGameState();
                    updateUI();
                    break;

                case PLAYER_STAND:
                    view.showMessage("✋ " + message.getData() + " stands");
                    break;

                case DEALER_TURN:
                    view.showMessage("🎴 Dealer's turn...");
                    view.setTurnIndicator("🎴 DEALER'S TURN");
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
                        view.showMessage("🎯 Round ended! Check results.");
                        view.setTurnIndicator("⏸ Round Over");
                    }
                    peer.requestGameState();
                    updateUI();
                    break;

                case GAME_STATE_UPDATE:
                    updateUI();
                    break;

                default:
                    break;
            }
        });
    }
    
    private String getResultEmoji(model.GameResult result) {
        switch (result) {
            case PLAYER_WIN: return "🎉";
            case PLAYER_BLACKJACK: return "🎆";
            case PUSH: return "🤝";
            case DEALER_WIN: return "😔";
            default: return "🎯";
        }
    }

    private void updateUI() {
        // Update dealer
        List<Card> dealerHand = peer.getDealerHand();
        view.updateDealer(dealerHand, !roundInProgress);

        // Update all players
        Map<String, List<Card>> playerHands = peer.getPlayerHands();
        Map<String, Integer> playerBalances = peer.getPlayerBalances();

        for (String playerId : playerHands.keySet()) {
            List<Card> hand = playerHands.get(playerId);
            int balance = playerBalances.getOrDefault(playerId, 0);
            boolean isActive = playerId.equals(peer.getUserId()) && roundInProgress;
            
            String displayName = playerId.equals(peer.getUserId()) ? "You" : playerId;
            view.updatePlayer(playerId, displayName, hand, balance, isActive);
        }

        // Update my balance
        view.updateMyBalance(peer.getMyBalance());

        // Update button states based on game phase
        boolean canBet = !roundInProgress;
        boolean canHit = roundInProgress;
        boolean canStand = roundInProgress;
        boolean canStartRound = isHost && !roundInProgress;

        view.setButtonsEnabled(canBet, canHit, canStand, canStartRound);
        
        // Update turn indicator
        if (!roundInProgress) {
            if (isHost) {
                view.setTurnIndicator("⏸ Waiting - Click 'Start Round' to begin");
            } else {
                view.setTurnIndicator("⏸ Waiting for host to start round");
            }
        } else {
            view.setTurnIndicator("▶ Your Turn - Hit or Stand!");
        }
    }
}
