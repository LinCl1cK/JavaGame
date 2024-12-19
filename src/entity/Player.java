package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import main.CollisionManager;
import main.GamePanel;
import main.KeyHandler;
import tile.TileManager;

public class Player extends Entity {
    private final KeyHandler keyH;
    private final TileManager tileManager;
    private final CollisionManager collisionManager;
    AnimationManager animationManager;

    //private List<Key> inventory = new ArrayList<>();
    //private List<Key> consumedKeys = new ArrayList<>();
    private int goldKeyCount = 0;
    private int silverKeyCount = 0;


    private final int screenX, screenY;
    private final int SCALE = 3; // Change this value to adjust the size
    private boolean isMoving = false;
    private int frameIndex = 0;
    private double elapsedTime = 0;
    private final double animationSpeed = 4.25;
    private String direction = "DOWN";

    private String character = "Slime1";

    // Define the player's collision area
    public final Rectangle collisionBounds;
    private final int collisionAreaOffsetX = -20; // Adjust as needed
    private final int collisionAreaOffsetY = -20; // Adjust as needed
    private int collisionAreaWidth; 
    private int collisionAreaHeight;

    public Player(GamePanel gp, KeyHandler keyH, CollisionManager collisionManager, TileManager tileManager) {
        this.keyH = keyH;
        this.collisionManager = collisionManager;
        this.tileManager = tileManager;
        animationManager = new AnimationManager(character);
        

        // Initialize the collision bounds
        int tileSize = tileManager.getTileSize();
        this.collisionAreaWidth = tileSize - 5;
        this.collisionAreaHeight = tileSize - 5;
        this.collisionBounds = new Rectangle(worldX + collisionAreaOffsetX, worldY + collisionAreaOffsetY, collisionAreaWidth, collisionAreaHeight);

        // Use GamePanel getters for screen dimensions
        this.screenX = gp.getScreenWidth() / 2 - (tileSize * SCALE) / 2;
        this.screenY = gp.getScreenHeight() / 2 - (tileSize * SCALE) / 2;

        setDefaultValues();
    }

    public void setDefaultValues() {
        worldX = tileManager.getTileSize() * 47;
        worldY = tileManager.getTileSize() * 50;
        speed = 6; // Adjust speed according to the scale
        collisionBounds.setLocation(worldX + collisionAreaOffsetX, worldY + collisionAreaOffsetY);
    }

    public void update(double delta) {
        int deltaX = 0, deltaY = 0;
        boolean wasMoving = isMoving;
        isMoving = false;
    
        if (keyH.upPressed) {
            direction = "UP";
            deltaY -= speed;
            isMoving = true;
        } else if (keyH.downPressed) {
            direction = "DOWN";
            deltaY += speed;
            isMoving = true;
        } else if (keyH.leftPressed) {
            direction = "LEFT";
            deltaX -= speed;
            isMoving = true;
        } else if (keyH.rightPressed) {
            direction = "RIGHT";
            deltaX += speed;
            isMoving = true;
        }
    
        // Update the collision bounds with the new position
        collisionBounds.setLocation(worldX + deltaX + collisionAreaOffsetX, worldY + deltaY + collisionAreaOffsetY);
    
        // Check for collision
        if (!collisionManager.isCollision(collisionBounds)) {
            worldX += deltaX;
            worldY += deltaY;
            collisionBounds.setLocation(worldX + collisionAreaOffsetX, worldY + collisionAreaOffsetY);
        }
    
        // Check for chest interaction (F key)
        if (keyH.fKeyPressed) {
            interactWithChest();
        }
    
        // Check for key collection
        tileManager.updateKeyCollection(this);
    
        elapsedTime += delta;
        if (elapsedTime >= animationSpeed) {
            frameIndex = (frameIndex + 1) % (isMoving ? 8 : 6); // Use 8 frames for running and 6 for idle
            elapsedTime = 0;
        }
    }

    public Rectangle getCollisionBounds() {
        return new Rectangle(worldX, worldY, tileManager.getTileSize(), tileManager.getTileSize());
    }
    
    // private void interactWithChest() {
    //     for (Chest chest : tileManager.getChests()) {  // Get all chests from TileManager
    //         if (chest != null && isPlayerNearChest(chest)) {  // Check if player is near the chest
    //             chest.isOpen();  // Open the chest
    //             System.out.println("Chest opened!");
    //             break;  // Stop after opening one chest
    //         }
    //     }
    // }
    public void interactWithChest() {
        for (Chest chest : tileManager.getChests()) {  // Get all chests from TileManager
            if (chest != null && isPlayerNearChest(chest) && !chest.isOpened()) {  // Check if player is near and chest isn't opened
                if (getSilverKeyCount() > 0) {  // Check if the player has a silver key
                    chest.isOpen();  // Open the chest
                    consumeSilverKey();  // Consume one silver key
                    System.out.println("Chest opened and a silver key was consumed!");
                } else {
                    System.out.println("Player needs a silver key to open this chest.");
                }
                break;  // Stop after opening one chest
            }
        }
    }

    private boolean isPlayerNearChest(Chest chest) {
        Rectangle chestBounds = new Rectangle(chest.getX(), chest.getY(), tileManager.getTileSize(), tileManager.getTileSize());
        return collisionBounds.intersects(chestBounds);  // Check if the player's collision bounds intersect with chest
    }
    
    public void draw(Graphics2D g2) {
        int tileSize = tileManager.getTileSize(); // Fetch tileSize from TileManager
        BufferedImage frame = animationManager.getFrame(isMoving, direction, frameIndex);
        g2.drawImage(frame, screenX, screenY, (tileSize * SCALE), (tileSize * SCALE), null);
    }

     public void addGoldKey() {
        goldKeyCount++;
    }

    public void addSilverKey() {
        silverKeyCount++;
    }
    
    public int getGoldKeyCount() {
        return goldKeyCount;
    }

    public int getSilverKeyCount() {
        return silverKeyCount;
    }

    public void consumeSilverKey() {
        if (silverKeyCount > 0) {
            silverKeyCount--;
        }
    }

    // Method to consume keys
    public void collectKey(int keyIndex) {
        if (keyIndex == 7) {
            goldKeyCount++;  // Collect a Gold key
        } else if (keyIndex == 8) {
            silverKeyCount++;  // Collect a Silver key
        }
    }


}
