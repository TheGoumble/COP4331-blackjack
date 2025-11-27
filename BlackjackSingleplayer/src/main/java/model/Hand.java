package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 * @author Bridjet Walker
 */
public class Hand {
    private final List<CardInPlay> cards = new ArrayList<>();

    public void add(CardInPlay cip) {
        cards.add(cip);
    }

    public List<CardInPlay> cards() {
        return Collections.unmodifiableList(cards);
    }

    public void clear() {
        cards.clear();
    }

    public void revealAll() {
        for (CardInPlay c : cards) {
            c.setVisible(true);
        }
    }
}