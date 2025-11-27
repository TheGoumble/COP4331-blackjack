package model;

/**
 *
 * @author Bridjet Walker
 */
public class CardInPlay {
    private final Card card;
    private boolean visible;

    public CardInPlay(Card card, boolean visible) {
        this.card = card;
        this.visible = visible;
    }

    public Card card() {
        return card;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return visible ? card.toString() : "??";
    }
}