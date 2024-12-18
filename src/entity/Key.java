package entity;

public class Key {
    public enum KeyType { GOLD, SILVER }
    
    public KeyType type;
    public int x, y; // World position
    public boolean isCollected;

    public Key(KeyType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.isCollected = false;
    }
}
