package main;

import entity.Player;
import java.awt.Color;
import java.awt.Dimension;
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
    Thread gameThread; // when game thread is called, it automatically runs the 'run' method
    public Player player = new Player(this, keyH);
    TileManager tileManager = new TileManager(this);

    // Game Intro
    private boolean isInIntro = true;
    private BufferedImage introImage;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Load map layers and tilesets
        tileManager.loadMap();

        try {
            introImage = ImageIO.read(getClass().getResourceAsStream("/resources/Intro/Nigeru Sur.png")); 
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
        if (!isInIntro) { 
            player.update(delta); 
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // Upgrade version of drawing

        if (isInIntro) {
            g2.drawImage(introImage, 0, 0, screenWidth, screenHeight, null); 
        } else {
            // Draw base tile layer
            tileManager.drawBaseLayer(g2, player.worldX, player.worldY);

            tileManager.drawPlayerLayer(g2, player.worldX, player.worldY);

            // Draw player
            player.draw(g2);

            // Draw additional tile layer
            tileManager.drawAdditionalLayer(g2, player.worldX, player.worldY);
        }

        g2.dispose();
    }

    public void showGamePanel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showGamePanel'");
    }

    // Check if any key is pressed to skip intro
    public void checkIntroSkip() {
        if (keyH.anyKeyPressed()) {
            isInIntro = false;  // Skip intro and start the game
        }
    }
}
