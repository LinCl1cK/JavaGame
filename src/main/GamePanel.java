package main;

import entity.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.JPanel;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // Screen Settings
    public final int originalTileSize = 16; // 16 x 16 Tile
    public final int scale = 3;

    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = originalTileSize * scale * maxScreenCol; // 768 px
    public final int screenHeight = originalTileSize * scale * maxScreenRow; // 576 px

    // World Settings
    public final int maxWorldCol = 66;
    public final int maxWorldRow = 60;

    // Refresh rate
    int FPS = 60;

    // Instances
    KeyHandler keyH = new KeyHandler();
    TileManager tileManager;
    CollisionManager collisionManager;
    public Player player;

    Thread gameThread;

    // Game States
    private boolean isInIntro = true;    // Intro state
    private boolean isInComicFull = false; // Full comic display state
    private boolean isPaused = false;

    // Intro and Comic Variables
    private BufferedImage introImage;
    private BufferedImage comicImage; // Full comic image
    private long comicStartTime;      // Time when comic display starts
    private final int COMIC_DISPLAY_DURATION = 8000; // Comic duration in milliseconds

    // Pause Variables
    private BufferedImage pauseImage;

    // Music Control
    private Clip backgroundMusic;
    private boolean isMusicPlaying = false;
    private long musicPosition = 0;

    public int getScreenWidth() {
        return screenWidth;
    }
    
    public int getScreenHeight() {
        return screenHeight;
    }    

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Initialize instances in the correct order
        tileManager = new TileManager(this);
        collisionManager = new CollisionManager(tileManager);
        player = new Player(this, keyH, collisionManager, tileManager);

        tileManager.loadMap();

        // Load assets
        try {
            introImage = ImageIO.read(getClass().getResourceAsStream("/assets/resources/Intro/Nigeru Sur.png"));
            pauseImage = ImageIO.read(getClass().getResourceAsStream("/assets/resources/Intro/MENU.png"));
            comicImage = ImageIO.read(getClass().getResourceAsStream("/assets/resources/Intro/SlimeKomikcut.png"));

            // Load and play background music
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/assets/GameMusic/Music.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            isMusicPlaying = true;

        } catch (Exception e) {
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

        // Set comic start time right after the intro
        comicStartTime = System.currentTimeMillis();

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update(delta);
                repaint();
                delta--;
            }
            if (delta > 1.5) {  // Maximum delta threshold to prevent jumps
                delta = 1;
            }
        }
    }

    public void update(double delta) {
        // Skip intro screen
        if (isInIntro) {
            if (keyH.anyKeyPressed()) {
                System.out.println("Intro skipped. ");
                isInIntro = false;
                isInComicFull = true;
                comicStartTime = System.currentTimeMillis(); // Set comic start time
                keyH.resetAllKeys();
            }
            return;
        }

        // Show comic image for a fixed duration
        if (isInComicFull) {
            long elapsedTime = System.currentTimeMillis() - comicStartTime;
            if (elapsedTime >= COMIC_DISPLAY_DURATION || keyH.anyKeyPressed()) {
                System.out.println("Comic skipped or ended.");
                isInComicFull = false; // Exit comic display and start the game
                keyH.resetAllKeys();
            }
            return;
        }

        // Pause handling
        if (keyH.pausePressed) {
            if (isPaused) {
                System.out.println("Game exited.");
                System.exit(0); // Quit game on second ESC
               
            } else {
                // If not paused, ESC will pause the game
                isPaused = true; //pause the game
                System.out.println("Game paused. Music stopped.");
                SoundHandler.stopMusic();

            }
            keyH.pausePressed = false;  // Reset pause flag after processing
        }

        // Handle resume on ENTER key
        if (keyH.enterPressedForResume && isPaused) {
            isPaused = false; // Resume game when Enter is pressed
            System.out.println("Game resumed. Music started.");
            SoundHandler.resumeMusic();  // Resume the music when the game is resumed
            keyH.enterPressedForResume = false; // Reset resume flag
            keyH.enterPressedForResume = false; // Reset resume flag
        }

        if (!isInIntro && !isPaused) { 
            player.update(delta); 
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (isInIntro) {
            g2.drawImage(introImage, 0, 0, screenWidth, screenHeight, null);
        } else if (isInComicFull) {
            // Display the full comic image
            g2.drawImage(comicImage, 0, 0, screenWidth, screenHeight, null);
        } else if (isPaused) {
            // Draw pause screen
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.drawImage(pauseImage, 0, 0, screenWidth, screenHeight, null);
        } else {
            // Draw game layers
            tileManager.drawBaseLayer(g2, player.worldX, player.worldY);
            tileManager.drawPlayerTileLayer(g2, player.worldX, player.worldY);
            tileManager.drawMiscLayer(g2, player.worldX, player.worldY);
            tileManager.drawSecondaryLayer(g2, player.worldX, player.worldY);

            // Draw player
            player.draw(g2);

            // Draw objects and doors with animations
            tileManager.drawObjectsAndDoors(g2, player.worldX, player.worldY);
            //tileManager.drawDoorAnimations(g2, player.worldX, player.worldY);
        }

        g2.dispose();
    }

    // Music Controls
    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            musicPosition = backgroundMusic.getMicrosecondPosition();
            backgroundMusic.stop();
            backgroundMusic.flush(); // Clear any buffered data
            System.out.println("Music stopped at position: " + musicPosition);
        }
    }

    public void startMusic() {
        if (backgroundMusic != null && !backgroundMusic.isRunning()) {
            backgroundMusic.setMicrosecondPosition(musicPosition);
            backgroundMusic.start();
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            
        }
    }
}
