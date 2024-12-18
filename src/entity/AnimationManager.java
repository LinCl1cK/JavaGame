package entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AnimationManager {
    private static final int SPRITE_WIDTH = 64, SPRITE_HEIGHT = 64;
    private static final int RUNNING_FRAMES = 8;
    private static final int IDLE_FRAMES = 6;

    private BufferedImage spriteSheet, idleSpriteSheet;
    private BufferedImage[] downFrames, upFrames, leftFrames, rightFrames;
    private BufferedImage[] downIdleFrames, upIdleFrames, leftIdleFrames, rightIdleFrames;

    public AnimationManager(String character) {
        loadSpriteSheets(character);
        loadFrames();
        loadIdleFrames();
    }

    private void loadSpriteSheets(String character) {
        try {
            spriteSheet = ImageIO.read(new File("src/assets/Slime character/PNG/" + character + "/" + character + "_Run_full.png"));
            idleSpriteSheet = ImageIO.read(new File("src/assets/Slime character/PNG/" + character + "/" + character + "_Idle_full.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFrames() {
        downFrames = new BufferedImage[RUNNING_FRAMES];
        upFrames = new BufferedImage[RUNNING_FRAMES];
        leftFrames = new BufferedImage[RUNNING_FRAMES];
        rightFrames = new BufferedImage[RUNNING_FRAMES];

        for (int i = 0; i < RUNNING_FRAMES; i++) {
            downFrames[i] = spriteSheet.getSubimage(i * SPRITE_WIDTH, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
            upFrames[i] = spriteSheet.getSubimage(i * SPRITE_WIDTH, SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
            leftFrames[i] = spriteSheet.getSubimage(i * SPRITE_WIDTH, 2 * SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
            rightFrames[i] = spriteSheet.getSubimage(i * SPRITE_WIDTH, 3 * SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
        }
    }

    private void loadIdleFrames() {
        downIdleFrames = new BufferedImage[IDLE_FRAMES];
        upIdleFrames = new BufferedImage[IDLE_FRAMES];
        leftIdleFrames = new BufferedImage[IDLE_FRAMES];
        rightIdleFrames = new BufferedImage[IDLE_FRAMES];

        for (int i = 0; i < IDLE_FRAMES; i++) {
            downIdleFrames[i] = idleSpriteSheet.getSubimage(i * SPRITE_WIDTH, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
            upIdleFrames[i] = idleSpriteSheet.getSubimage(i * SPRITE_WIDTH, SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
            leftIdleFrames[i] = idleSpriteSheet.getSubimage(i * SPRITE_WIDTH, 2 * SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
            rightIdleFrames[i] = idleSpriteSheet.getSubimage(i * SPRITE_WIDTH, 3 * SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
        }
    }

    public BufferedImage getFrame(boolean isMoving, String direction, int frameIndex) {
        BufferedImage[] frames = isMoving ? getMovingFrames(direction) : getIdleFrames(direction);
        return frames[frameIndex % frames.length]; // Use modulo to ensure the frameIndex is within bounds
    }

    private BufferedImage[] getMovingFrames(String direction) {
        switch (direction) {
            case "DOWN":
                return downFrames;
            case "UP":
                return upFrames;
            case "LEFT":
                return leftFrames;
            case "RIGHT":
                return rightFrames;
            default:
                return downFrames;
        }
    }

    private BufferedImage[] getIdleFrames(String direction) {
        switch (direction) {
            case "DOWN":
                return downIdleFrames;
            case "UP":
                return upIdleFrames;
            case "LEFT":
                return leftIdleFrames;
            case "RIGHT":
                return rightIdleFrames;
            default:
                return downIdleFrames;
        }
    }
}
