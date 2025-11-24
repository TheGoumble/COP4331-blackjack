package model;

/**
 *
 * @author Bridjet Walker
 */
public class User {
    private final String name;
    private int balance = 10000; // default cap in spec

    public User(String name){
        this.name = name;
    }

    public String name(){ return name; }
    public int balance(){ return balance; }
}