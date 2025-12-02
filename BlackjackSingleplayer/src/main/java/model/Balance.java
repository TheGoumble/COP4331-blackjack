package model;

/**
 * MVC PATTERN: This is a Model (game state)
 *
 * @author Bridjet Walker
 */
public class Balance {
    private int amount;

    public Balance(int startAmount) {
        this.amount = startAmount;
    }

    public int amount() {
        return amount;
    }

    public boolean hasAtLeast(int x) {
        return amount >= x;
    }

    public void debit(int x) {
        if (x < 0) throw new IllegalArgumentException("negative debit");
        amount -= x;
    }

    public void credit(int x) {
        if (x < 0) throw new IllegalArgumentException("negative credit");
        amount += x;
    }
}