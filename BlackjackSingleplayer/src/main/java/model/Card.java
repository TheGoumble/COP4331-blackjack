package model;

/**
 *
 * @author Bridjet Walker
 */
public class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit suit() {
        return suit;
    }

    public Rank rank() {
        return rank;
    }

    public int baseValue() {
        return rank.value();
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}