package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.awt.Rectangle;
import main.GamePanel;
import main.KeyHandler;
import tile.TileManager;

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
    TileManager tileManager;

    public String character = "slime3";

    // World positions for the player
    public int worldX, worldY;

    // Screen positions for the player
    public final int screenX;
    public final int screenY;

    // Collision area
    public Rectangle collisionArea;
    private int collisionAreaOffsetX = -18; // Adjust as needed
    private int collisionAreaOffsetY = -15; // Adjust as needed
    private int collisionAreaWidth; 
    private int collisionAreaHeight;
    
    public Player(GamePanel gp, KeyHandler keyH, TileManager tileManager) {
        this.gp = gp;
        this.keyH = keyH;
        this.tileManager = tileManager; // Initialize the tileManager
    
        // Center the player on the screen
        this.screenX = gp.screenWidth / 2 - (SPRITE_WIDTH * SCALE) / 2;
        this.screenY = gp.screenHeight / 2 - (SPRITE_HEIGHT * SCALE) / 2;
    
        loadSpriteSheets();
        loadFrames();
        loadIdleFrames();
        setDefaultValues();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 47;
        worldY = gp.tileSize * 50;
        speed = 15; // Adjust speed according to scale

        collisionAreaWidth = gp.tileSize - 10;
        collisionAreaHeight = gp.tileSize - 10;
        collisionArea = new Rectangle(worldX + collisionAreaOffsetX, worldY + collisionAreaOffsetY, collisionAreaWidth, collisionAreaHeight);
    }

    public void update(double delta) {
        isMoving = false;
    
        int deltaX = 0;
        int deltaY = 0;
    
        if (keyH.upPressed) {
            currentDirection = Direction.UP;
            deltaY -= speed;
            isMoving = true;
        } else if (keyH.downPressed) {
            currentDirection = Direction.DOWN;
            deltaY += speed;
            isMoving = true;
        } else if (keyH.leftPressed) {
            currentDirection = Direction.LEFT;
            deltaX -= speed;
            isMoving = true;
        } else if (keyH.rightPressed) {
            currentDirection = Direction.RIGHT;
            deltaX += speed;
            isMoving = true;
        }
    
        // Update the collision area
        Rectangle nextPosition = new Rectangle(worldX + deltaX + collisionAreaOffsetX, worldY + deltaY + collisionAreaOffsetY, collisionAreaWidth, collisionAreaHeight);
    
        // Check for collisions before updating position
        if (!tileManager.isCollision(nextPosition)) {
            worldX += deltaX;
            worldY += deltaY;
            collisionArea.setLocation(worldX + collisionAreaOffsetX, worldY + collisionAreaOffsetY);
        } else {
            System.out.println("Collision at next position: (" + nextPosition.x + ", " + nextPosition.y + ")");
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
    
    private void loadSpriteSheets() {
        try {
            switch (character) {
                case "slime1":
                    spriteSheet = ImageIO.read(new File("images/Slime character/PNG/Slime 1/Slime1_Run_full.png"));
                    idleSpriteSheet = ImageIO.read(new File("images/Slime character/PNG/Slime 1/Slime1_Idle_full.png"));
                    break;
                case "slime2":
                    spriteSheet = ImageIO.read(new File("images/Slime character/PNG/Slime 2/Slime2_Run_full.png"));
                    idleSpriteSheet = ImageIO.read(new File("images/Slime character/PNG/Slime 2/Slime2_Idle_full.png"));
                    break;
                case "slime3":
                    spriteSheet = ImageIO.read(new File("images/Slime character/PNG/Slime 3/Slime3_Run_full.png"));
                    idleSpriteSheet = ImageIO.read(new File("images/Slime character/PNG/Slime 3/Slime3_Idle_full.png"));
                    break;
                default:
                    break;
            }

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
    
        g2.drawImage(currentFrame, screenX, screenY, SPRITE_WIDTH * SCALE, SPRITE_HEIGHT * SCALE, null);
    
        // Optionally draw collision area for debugging (collisionAreaOffsetX - 60), screenY - (collisionAreaOffsetY - 60)
        g2.drawRect(screenX - (collisionAreaOffsetX), screenY - (collisionAreaOffsetY), collisionAreaWidth, collisionAreaHeight);
    }
    
}
