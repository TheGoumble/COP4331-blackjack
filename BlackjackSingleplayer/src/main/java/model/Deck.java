package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
/**
 * MVC PATTERN: This is a Model (game entity)
 *
 * @author Bridjet Walker
 */
public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private final Random random = new Random();

    public Deck() {
        reset();
    }

    public final void reset() {
        cards.clear();
        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                cards.add(new Card(s, r));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    public Card deal() {
        if (cards.size() < 15) { // auto reshuffle when low
            reset();
        }
        return cards.remove(cards.size() - 1);
    }
}