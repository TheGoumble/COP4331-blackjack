package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bridjet Walker
 */

public class ActiveGame {
    private final Dealer dealer = new Dealer();
    private final List<User> players = new ArrayList<>(4);

    public void addPlayer(User u){
        if(players.size() < 4) players.add(u);
    }

    public List<User> players(){ return players; }
    public Dealer dealer(){ return dealer; }
}