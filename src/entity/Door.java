package entity;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

public class Door {

    private GamePanel gp;
    private int worldX, worldY;
    private boolean isOpen = false;
    private Image doorImage;

    public Door(GamePanel gp, int x, int y) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        loadImage();
    }

    private void loadImage() {
        try {
            doorImage = ImageIO.read(new File("images/door.png")); // Load the door image
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        if (isOpen) {
            // If the door is open, we can draw an open door sprite
            g2.setColor(java.awt.Color.GREEN);  // Optional: Color it green to indicate it's open
        }
        g2.drawImage(doorImage, worldX, worldY, gp.tileSize, gp.tileSize, null);
    }

    public void open() {
        isOpen = true;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getX() {
        return worldX;
    }

    public int getY() {
        return worldY;
    }
}

