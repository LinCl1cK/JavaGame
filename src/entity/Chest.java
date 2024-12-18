package entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Chest extends Entity {
    private BufferedImage[] spriteFrames; // Animation frames
    private final Rectangle collisionBounds;
    private boolean isOpened = false;

    public Chest(int worldX, int worldY, BufferedImage[] spriteFrames) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.spriteFrames = spriteFrames;
        this.collisionBounds = new Rectangle(worldX, worldY, 48, 48); // Adjust collision size as needed
    }

    public boolean isInteracted(Rectangle playerBounds) {
        if (collisionBounds.intersects(playerBounds)) {
            if (!isOpened) {
                System.out.println("Chest opened!");
                isOpened = true;
                return true;
            }
        }
        return false;
    }

    public int getWorldX() {
        return worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public BufferedImage[] getSpriteFrames() {
        return spriteFrames;
    }

    public Rectangle getCollisionBounds() {
        return collisionBounds;
    }
}