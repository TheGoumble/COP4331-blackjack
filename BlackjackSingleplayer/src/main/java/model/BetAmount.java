package model;

/**
 * MVC PATTERN: This is a Model (game state)
 *
 * @author Bridjet Walker
 */
public class BetAmount {
    private int mainBet;

    public int mainBet() {
        return mainBet;
    }

    public void setMainBet(int mainBet) {
        this.mainBet = mainBet;
    }

    public void clear() {
        mainBet = 0;
    }
}