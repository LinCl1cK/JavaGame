package entity;

import tile.TileManager;
import java.awt.image.BufferedImage;

public class Chest {
    private int x, y; // Chest's position
    private TileManager tileManager; // Reference to TileManager
    private int chestIndex; // Sprite row for this chest
    private boolean isOpen; // Chest state
    private boolean isOpened = false; 
    private int animationFrame; // Current animation frame
    private int frameCounter; // Tracks time spent on a frame
    private final int maxFrames; // Total frames for the animation
    private final int frameDuration; // Frames to display each animation frame

    public Chest(int x, int y, TileManager tileManager, int chestIndex) {
        this.x = x;
        this.y = y;
        this.tileManager = tileManager;
        this.chestIndex = chestIndex;
        this.isOpen = false;
        this.animationFrame = 0;
        this.frameCounter = 0;
        this.maxFrames = tileManager.objectAnimations[chestIndex].length; // Get the total frames
        this.frameDuration = 10; // Duration for each frame (adjust as needed)
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getChestIndex() {
        return chestIndex;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void isOpen() {
        if (!isOpened) {
            isOpened = true;
            animationFrame = 0; // Reset animation frame
            frameCounter = 0; // Reset frame counter
        }
    }

    public void update() {
        if (isOpen && animationFrame < maxFrames) {
            frameCounter++;
            if (frameCounter >= frameDuration) {
                frameCounter = 0; // Reset frame counter
                animationFrame++; // Move to the next frame
            }

            // Ensure chest remains on the last frame when animation completes
            if (animationFrame >= maxFrames) {
                animationFrame = maxFrames - 1; // Keep it at the last frame
            }
        }
    }

    public BufferedImage getCurrentFrame() {
        // Return the current frame for rendering
        return tileManager.objectAnimations[chestIndex][animationFrame];
    }
}
