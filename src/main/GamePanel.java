package main;

import entity.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
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

    Thread gameThread; // when game thread is called, it automatically runs the 'run' method

    // Game Intro
    private boolean isInIntro = true;
    private BufferedImage introImage;

    // Pause control
    private boolean isPaused = false;
    private boolean isGameOver = false; // To handle exiting the game
    private BufferedImage pauseImage;

    // Music Control
    private Clip backgroundMusic; // For the background music
    private boolean isMusicPlaying = false;
    private long musicPosition = 0; // Track the current position of the music

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

            // Load and play music in a loop
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/assets/GameMusic/Music.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            isMusicPlaying = true; // Initially music is playing
        } catch (Exception e) {
            e.printStackTrace();  // Make sure to print out if there is an error loading images or music
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
        // Handle pausing and unpausing the game
        if (keyH.pausePressed) {
            if (isPaused) {
                // If already paused, ESC will quit the game
                System.exit(0);  // Exit the game
            } else {
                // If not paused, ESC will pause the game
                isPaused = true;
                stopMusic(); // Stop the music when the game is paused
            }
            keyH.pausePressed = false;  // Reset pause flag after processing
        }

        // Handle resume on ENTER key
        if (keyH.enterPressedForResume && isPaused) {
            isPaused = false; // Resume game when Enter is pressed
            startMusic(); // Start the music when the game is resumed
            keyH.enterPressedForResume = false; // Reset resume flag
        }

        if (!isInIntro && !isPaused) { 
            player.update(delta); 
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // Upgrade version of drawing

        if (isInIntro) {
            g2.drawImage(introImage, 0, 0, screenWidth, screenHeight, null); 
        } else if (isPaused) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight); // Draw a pause screen
            g2.drawImage(pauseImage, 0, 0, screenWidth, screenHeight, null); // Draw the pause image
        } else {
            // Draw base tile layer
            tileManager.drawBaseLayer(g2, player.worldX, player.worldY);

            tileManager.drawPlayerTileLayer(g2, player.worldX, player.worldY);

            tileManager.drawMiscLayer(g2, player.worldX, player.worldY);

            // Draw player
            player.draw(g2);

            // Draw additional tile layer
            tileManager.drawSecondaryLayer(g2, player.worldX, player.worldY);
        }

        g2.dispose();
    }

    // Check if any key is pressed to skip intro
    public void checkIntroSkip() {
        if (keyH.anyKeyPressed()) {
            isInIntro = false;  // Skip intro and start the game
        }
    }

    // Method to stop music
    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            musicPosition = backgroundMusic.getMicrosecondPosition(); // Track position before stopping
            backgroundMusic.stop(); // Stop the music
        }
    }

    // Method to start/resume music
    public void startMusic() {
        if (backgroundMusic != null && !backgroundMusic.isRunning()) {
            backgroundMusic.setMicrosecondPosition(musicPosition); // Resume from the position where it stopped
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY); // Resume the music
        }
    }
}
