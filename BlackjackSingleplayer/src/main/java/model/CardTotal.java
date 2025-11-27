package model;

/**
 *
 * @author Bridjet Walker
 */
public class CardTotal {

    public int compute(Hand hand) {
        int total = 0;
        int aces = 0;

        for (CardInPlay cip : hand.cards()) {
            if (!cip.isVisible()) continue;
            Card c = cip.card();
            total += c.baseValue();
            if (c.rank() == Rank.ACE) aces++;
        }

        // downgrade aces from 11 to 1 if busting
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }
}