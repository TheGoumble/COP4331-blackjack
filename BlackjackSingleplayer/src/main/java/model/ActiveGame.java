package model;

/**
 *
 * @author Bridjet Walker
 */
public class ActiveGame {

    private final Deck deck = new Deck();
    private final Hand playerHand = new Hand();
    private final Hand dealerHand = new Hand();
    private final Balance playerBalance = new Balance(10_000);
    private final BetAmount bet = new BetAmount();
    private final CardTotal totalCalc = new CardTotal();

    private boolean roundInProgress = false;
    private boolean playerTurn = false;
    private GameResult lastResult = GameResult.NONE;

    public void startNewRound(int betAmount) {
        if (isGameOver()) {
            throw new IllegalStateException("No funds left. Game over.");
        }
        if (roundInProgress) {
            throw new IllegalStateException("Round already in progress.");
        }
        if (betAmount < 1) {
            throw new IllegalArgumentException("Bet must be at least 1.");
        }
        if (!playerBalance.hasAtLeast(betAmount)) {
            throw new IllegalStateException("Insufficient balance for that bet.");
        }

        // take bet
        bet.setMainBet(betAmount);
        playerBalance.debit(betAmount);

        // reset hands
        playerHand.clear();
        dealerHand.clear();
        lastResult = GameResult.NONE;

        // deal initial cards: player, dealer, player, dealer(hidden)
        playerHand.add(new CardInPlay(deck.deal(), true));
        dealerHand.add(new CardInPlay(deck.deal(), true));
        playerHand.add(new CardInPlay(deck.deal(), true));
        dealerHand.add(new CardInPlay(deck.deal(), false)); // hole card hidden

        roundInProgress = true;
        playerTurn = true;

        // check for player blackjack
        if (isPlayerBlackjack()) {
            lastResult = GameResult.PLAYER_BLACKJACK;
            // payout 3:2 on bet (net profit 1.5 * bet)
            int win = (int) Math.round(bet.mainBet() * 1.5);
            // return bet + win
            playerBalance.credit(bet.mainBet() + win);
            playerTurn = false;
            roundInProgress = false;
            clearBet();
        }
    }

    public void playerHit() {
        if (!roundInProgress || !playerTurn) return;

        playerHand.add(new CardInPlay(deck.deal(), true));
        int playerTotal = totalCalc.compute(playerHand);

        if (playerTotal > 21) {
            // player busts
            lastResult = GameResult.DEALER_WIN;
            playerTurn = false;
            roundInProgress = false;
            revealDealerHand();
            clearBet();
        } else if (playerTotal == 21) {
            // auto stand and let dealer play
            playerStand();
        }
    }

    public void playerStand() {
        if (!roundInProgress || !playerTurn) return;

        playerTurn = false;
        dealerTurnAndResolve();
    }

    private void dealerTurnAndResolve() {
        revealDealerHand();

        int dealerTotal = totalCalc.compute(dealerHand);
        int playerTotal = totalCalc.compute(playerHand);

        // dealer draws to 17+
        while (dealerTotal < 17) {
            dealerHand.add(new CardInPlay(deck.deal(), true));
            dealerTotal = totalCalc.compute(dealerHand);
        }

        // resolve outcome
        if (dealerTotal > 21) {
            // dealer busts → player wins 1:1
            lastResult = GameResult.PLAYER_WIN;
            playerBalance.credit(bet.mainBet() * 2);
        } else if (playerTotal > dealerTotal) {
            lastResult = GameResult.PLAYER_WIN;
            playerBalance.credit(bet.mainBet() * 2);
        } else if (playerTotal < dealerTotal) {
            lastResult = GameResult.DEALER_WIN;
            // bet already lost
        } else {
            // push → return bet only
            lastResult = GameResult.PUSH;
            playerBalance.credit(bet.mainBet());
        }

        roundInProgress = false;
        clearBet();
    }

    private void revealDealerHand() {
        dealerHand.revealAll();
    }

    private boolean isPlayerBlackjack() {
        return totalCalc.compute(playerHand) == 21 && playerHand.cards().size() == 2;
    }

    private void clearBet() {
        bet.clear();
    }

    // --- getters for UI ---
    public Hand getPlayerHand() {
        return playerHand;
    }

    public Hand getDealerHand() {
        return dealerHand;
    }

    public int getPlayerTotalVisible() {
        return totalCalc.compute(playerHand);
    }

    public int getDealerTotalVisible() {
        return totalCalc.compute(dealerHand);
    }

    public int getBalance() {
        return playerBalance.amount();
    }

    public int getCurrentBet() {
        return bet.mainBet();
    }

    public boolean isRoundInProgress() {
        return roundInProgress;
    }

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public GameResult getLastResult() {
        return lastResult;
    }

    public boolean isGameOver() {
        return playerBalance.amount() < 1;
    }

    public void resetAfterRound() {
        clearBet();
        playerHand.clear();
        dealerHand.clear();
        lastResult = GameResult.NONE;
        roundInProgress = false;
        playerTurn = false;
    }
}