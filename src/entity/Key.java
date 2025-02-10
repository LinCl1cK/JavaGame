package entity;

public class Key {
    public enum KeyType { GOLD, SILVER }
    
    private KeyType type;
    private int x, y; // World position
    private boolean isCollected;

    public Key(KeyType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.isCollected = false;
    }
}
