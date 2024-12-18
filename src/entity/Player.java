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
    private final int collisionAreaOffsetX = -17; // Adjust as needed
    private final int collisionAreaOffsetY = -15; // Adjust as needed
    private int collisionAreaWidth; 
    private int collisionAreaHeight;

    public Player(GamePanel gp, KeyHandler keyH, CollisionManager collisionManager, TileManager tileManager) {
        this.keyH = keyH;
        this.collisionManager = collisionManager;
        this.tileManager = tileManager;
        animationManager = new AnimationManager(character);

        // Initialize the collision bounds
        int tileSize = tileManager.getTileSize();
        this.collisionAreaWidth = tileSize - 10;
        this.collisionAreaHeight = tileSize - 10;
        this.collisionBounds = new Rectangle(worldX + collisionAreaOffsetX, worldY + collisionAreaOffsetY, collisionAreaWidth, collisionAreaHeight);

        // Use GamePanel getters for screen dimensions
        this.screenX = gp.getScreenWidth() / 2 - (tileSize * SCALE) / 2;
        this.screenY = gp.getScreenHeight() / 2 - (tileSize * SCALE) / 2;

        setDefaultValues();
    }

    public void setDefaultValues() {
        worldX = tileManager.getTileSize() * 47;
        worldY = tileManager.getTileSize() * 50;
        speed = 4; // Adjust speed according to the scale
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
        } else {
            // System.out.println("Collision at next position: (" + collisionBounds.x + ", " + collisionBounds.y + ")");
        }

        elapsedTime += delta;
        if (elapsedTime >= animationSpeed) {
            frameIndex = (frameIndex + 1) % (isMoving ? 8 : 6); // Use 8 frames for running and 6 for idle
            elapsedTime = 0;
        }
    }

    public void draw(Graphics2D g2) {
        int tileSize = tileManager.getTileSize(); // Fetch tileSize from TileManager
        BufferedImage frame = animationManager.getFrame(isMoving, direction, frameIndex);
        g2.drawImage(frame, screenX, screenY, (tileSize * SCALE), (tileSize * SCALE), null);
    }
}
