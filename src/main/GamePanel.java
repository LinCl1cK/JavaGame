package main;

import entity.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // Screen Settings
    public final int originalTileSize = 16; // 16 x 16 Tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48 x 48 Tile
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 px
    public final int screenHeight = tileSize * maxScreenRow; // 576 px

    // World Settings
    public final int maxWorldCol = 32;
    public final int maxWorldRow = 32;
    public final int worldWidth = tileSize * maxScreenCol;
    public final int worldHeight = tileSize * maxScreenRow;

    // Refresh rate
    int FPS = 60;

    // Instances
    KeyHandler keyH = new KeyHandler();
    TileManager tileManager; 
    public Player player;

    Thread gameThread; 

    // Game Intro
    private boolean isInIntro = true;
    private BufferedImage introImage;

    // Pause control
    private boolean isPaused = false;
    private boolean isGameOver = false; 
    private BufferedImage pauseImage;

    //gameCleared
    public boolean gameCleared = false; 

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Initialize TileManager and Player after dimensions are set
        tileManager = new TileManager(this);
        player = new Player(this, keyH, tileManager);
        

    
        // Load map layers and tilesets
        tileManager.loadMap();

        try {
            introImage = ImageIO.read(getClass().getResourceAsStream("/assets/resources/Intro/Nigeru Sur.png"));
            pauseImage = ImageIO.read(getClass().getResourceAsStream("/assets/resources/Intro/MENU.png")); 
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            checkIntroSkip();

            if (delta >= 1) {
                update(delta);
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update(double delta) {
        if (keyH.pausePressed) {
            if (isPaused) {
                // If already paused, ESC will quit the game
                System.exit(0);  
            } else {
                
                isPaused = true;
            }
            keyH.pausePressed = false;  
        }

        // Handle resume on ENTER key
        if (keyH.enterPressedForResume && isPaused) {
            isPaused = false; 
            keyH.enterPressedForResume = false;
        }

        if (!isInIntro && !isPaused) { 
            player.update(delta); 
        }
        if (gameCleared) {
           
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (isInIntro) {
            g2.drawImage(introImage, 0, 0, screenWidth, screenHeight, null); 
        } else if (isPaused) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.drawImage(pauseImage, 0, 0, screenWidth, screenHeight, null); 
            // Draw base tile layer
            tileManager.drawBaseLayer(g2, player.worldX, player.worldY);

            tileManager.drawPlayerTileLayer(g2, player.worldX, player.worldY);

            tileManager.drawMiscLayer(g2, player.worldX, player.worldY);

            // Draw player
            player.draw(g2);

            // Draw additional tile layer
            tileManager.drawSecondaryLayer(g2, player.worldX, player.worldY);

            if (gameCleared) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 32));
                g2.drawString("Game Cleared!", screenWidth / 2 - 100, screenHeight / 2);
            }
        }

        g2.dispose();
    }

    // Check if any key is pressed to skip intro
    public void checkIntroSkip() {
        if (keyH.anyKeyPressed()) {
            isInIntro = false;  
        }
        
    }
}
