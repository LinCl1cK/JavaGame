package entity;

import java.awt.image.BufferedImage;

public class Chest {
    private int x, y; // Chest's position
    private boolean isOpened = false; 
    private int animationFrame; // Current animation frame
    private int frameCounter; // Tracks time spent on a frame
    private final int maxFrames; // Total frames for the animation
    private final int frameDuration; // Frames to display each animation frame
    private BufferedImage[][] chestAnimations; // Store animations independently

    public Chest(int x, int y, BufferedImage[][] chestAnimations) {
        this.x = x;
        this.y = y;
        this.chestAnimations = chestAnimations;
        this.animationFrame = 0;
        this.frameCounter = 0;
        this.maxFrames = chestAnimations[0].length; // Ensure maxFrames matches the number of frames per row
        this.frameDuration = 10; // Adjust as needed
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void open() {
        if (!isOpened) {
            isOpened = true;
            animationFrame = 0;
            frameCounter = 0;
        }
    }

    public void update() {
        frameCounter++;

        if (frameCounter >= frameDuration) {
            frameCounter = 0;
            animationFrame++;

            // **If closed, cycle through the first row (closed animation)**
            if (!isOpened) {
                if (animationFrame >= chestAnimations[0].length) {
                    animationFrame = 0; // Loop closed animation frames
                }
            } 
            // **If opened, cycle through the second row (opening animation)**
            else {
                if (animationFrame >= chestAnimations[1].length) {
                    animationFrame = chestAnimations[1].length - 1; // Stay on last frame when fully opened
                }
            }
        }
    }

    public BufferedImage getCurrentFrame() {
        return isOpened ? chestAnimations[1][animationFrame] : chestAnimations[0][animationFrame];
    }
}
