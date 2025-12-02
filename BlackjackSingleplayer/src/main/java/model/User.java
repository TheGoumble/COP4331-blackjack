package model;

/**
 * Represents a user/player in the system
 * 
 * MVC PATTERN: This is a Model (user entity)
 * 
 * @author Javier Vargas, Group 12
 */
public class User {
    private final String userId;
    private final String displayName;
    
    public User(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName + " (" + userId + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        return userId.equals(other.userId);
    }
    
    @Override
    public int hashCode() {
        return userId.hashCode();
    }
}
