package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {

    private static final int FRAME_COLS = 8;  // 8 frames for movement animations
    private static final int IDLE_FRAME_COLS = 6;  // 6 frames for idle animations
    private static final int SPRITE_WIDTH = 64, SPRITE_HEIGHT = 64;
    private static final int SCALE = 3; // Scale factor matching your tile size
    private double animationSpeed = 4.25; // Adjust this value to control the animation speed
    private double elapsedTime = 0;
    private int frameIndex = 0;

    private BufferedImage[] downFrames, leftFrames, rightFrames, upFrames;
    private BufferedImage[] downIdleFrames, leftIdleFrames, rightIdleFrames, upIdleFrames;
    private BufferedImage spriteSheet, idleSpriteSheet;

    private enum Direction { DOWN, LEFT, RIGHT, UP }
    private Direction currentDirection = Direction.DOWN;
    private boolean isMoving = false;
    private boolean wasMoving = false; // To track previous state

    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        loadSpriteSheets();
        loadFrames();
        loadIdleFrames();
        setDefaultValues();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 4;
    }

    private void loadSpriteSheets() {
        try {
            spriteSheet = ImageIO.read(new File("images/Slime1_Run_full.png"));
            idleSpriteSheet = ImageIO.read(new File("images/Slime1_Idle_full.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFrames() {
        downFrames = new BufferedImage[FRAME_COLS];
        upFrames = new BufferedImage[FRAME_COLS];
        leftFrames = new BufferedImage[FRAME_COLS];
        rightFrames = new BufferedImage[FRAME_COLS];

        for (int i = 0; i < FRAME_COLS; i++) {
            downFrames[i] = spriteSheet.getSubimage(i * SPRITE_WIDTH, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
            upFrames[i] = spriteSheet.getSubimage(i * SPRITE_WIDTH, SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
            leftFrames[i] = spriteSheet.getSubimage(i * SPRITE_WIDTH, 2 * SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
            rightFrames[i] = spriteSheet.getSubimage(i * SPRITE_WIDTH, 3 * SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
        }
    }

    private void loadIdleFrames() {
        int idleFrameCols = idleSpriteSheet.getWidth() / SPRITE_WIDTH;  // 6 columns

        downIdleFrames = new BufferedImage[idleFrameCols];
        upIdleFrames = new BufferedImage[idleFrameCols];
        leftIdleFrames = new BufferedImage[idleFrameCols];
        rightIdleFrames = new BufferedImage[idleFrameCols];

        for (int i = 0; i < idleFrameCols; i++) {
            downIdleFrames[i] = idleSpriteSheet.getSubimage(i * SPRITE_WIDTH, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
            upIdleFrames[i] = idleSpriteSheet.getSubimage(i * SPRITE_WIDTH, SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
            leftIdleFrames[i] = idleSpriteSheet.getSubimage(i * SPRITE_WIDTH, 2 * SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
            rightIdleFrames[i] = idleSpriteSheet.getSubimage(i * SPRITE_WIDTH, 3 * SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);
        }
    }

    public void update(double delta) {
        isMoving = false;

        if (keyH.upPressed) {
            currentDirection = Direction.UP;
            y -= speed;
            isMoving = true;
        } else if (keyH.downPressed) {
            currentDirection = Direction.DOWN;
            y += speed;
            isMoving = true;
        } else if (keyH.leftPressed) {
            currentDirection = Direction.LEFT;
            x -= speed;
            isMoving = true;
        } else if (keyH.rightPressed) {
            currentDirection = Direction.RIGHT;
            x += speed;
            isMoving = true;
        }

        if (wasMoving != isMoving) {
            frameIndex = 0; // Reset frame index when switching between moving and idle
        }

        elapsedTime += delta;
        if (elapsedTime >= animationSpeed) {
            if (isMoving) {
                frameIndex = (frameIndex + 1) % FRAME_COLS;
            } else {
                frameIndex = (frameIndex + 1) % IDLE_FRAME_COLS;
            }
            elapsedTime = 0;
        }

        wasMoving = isMoving;
    }

    public void draw(Graphics2D g2) {
        BufferedImage currentFrame;
        if (isMoving) {
            switch (currentDirection) {
                case DOWN:
                    currentFrame = downFrames[frameIndex];
                    break;
                case LEFT:
                    currentFrame = leftFrames[frameIndex];
                    break;
                case RIGHT:
                    currentFrame = rightFrames[frameIndex];
                    break;
                case UP:
                    currentFrame = upFrames[frameIndex];
                    break;
                default:
                    currentFrame = downIdleFrames[0];
                    break;
            }
        } else {
            switch (currentDirection) {
                case DOWN:
                    currentFrame = downIdleFrames[frameIndex];
                    break;
                case LEFT:
                    currentFrame = leftIdleFrames[frameIndex];
                    break;
                case RIGHT:
                    currentFrame = rightIdleFrames[frameIndex];
                    break;
                case UP:
                    currentFrame = upIdleFrames[frameIndex];
                    break;
                default:
                    currentFrame = downIdleFrames[0];
                    break;
            }
        }

        g2.drawImage(currentFrame, x, y, SPRITE_WIDTH * SCALE, SPRITE_HEIGHT * SCALE, null);
    }
}