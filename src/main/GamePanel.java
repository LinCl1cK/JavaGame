package main;

import entity.ObjectAnimationManager;
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
    private ObjectAnimationManager objectAnimationManager;

    Thread gameThread;

    // Game States
    private boolean isInIntro = true;
    private boolean isInComicFull = false;
    private boolean isPaused = false;

    // Intro and Comic Variables
    private BufferedImage introImage;
    private BufferedImage comicImage; 
    private long comicStartTime; 
    private final int COMIC_DISPLAY_DURATION = 8000; 

    // Pause Variables
    private BufferedImage pauseImage;

    // Music Control
    private Clip backgroundMusic;
    private String musicPath = "src\\assets\\GameMusic\\Music.wav";
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
        
        // Initialize ObjectAnimationManager before Player
        objectAnimationManager = new ObjectAnimationManager(this);

        // Initialize TileManager first
        tileManager = new TileManager(this, objectAnimationManager);
        collisionManager = new CollisionManager(tileManager);
        objectAnimationManager.setTileManager(tileManager);
        
        // Now create Player with ObjectAnimationManager
        player = new Player(this, keyH, collisionManager, tileManager, objectAnimationManager);
        
        tileManager.loadMap();
        
        // Pass object data to ObjectAnimationManager
        objectAnimationManager.setMaps(
        tileManager.getObjectMap(),
        tileManager.getDoorMap(),
        tileManager.getChestMap(),
        tileManager.getKeyMap(),
        tileManager.getChests()  // ✅ Fix: Add chests
    );

        // Load assets
        try {
            introImage = ImageIO.read(getClass().getResourceAsStream("/assets/resources/Intro/Nigeru Sur.png"));
            pauseImage = ImageIO.read(getClass().getResourceAsStream("/assets/resources/Intro/MENU.png"));
            comicImage = ImageIO.read(getClass().getResourceAsStream("/assets/resources/Intro/SlimeKomikcut.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start background music
        SoundHandler.playMusic(musicPath);
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
            if (delta > 1.5) { 
                delta = 1;
            }
        }
    }

    public void update(double delta) {
        if (isInIntro) {
            if (keyH.anyKeyPressed()) {
                isInIntro = false;
                isInComicFull = true;
                comicStartTime = System.currentTimeMillis(); 
                keyH.resetAllKeys();
            }
            return;
        }

        if (isInComicFull) {
            long elapsedTime = System.currentTimeMillis() - comicStartTime;
            if (elapsedTime >= COMIC_DISPLAY_DURATION || keyH.anyKeyPressed()) {
                isInComicFull = false; 
                keyH.resetAllKeys();
            }
            return;
        }

        if (keyH.pausePressed) {
            isPaused = !isPaused;
            if (isPaused) {
                SoundHandler.stopMusic();
            } else {
                SoundHandler.resumeMusic();
            }
            keyH.pausePressed = false;
        }

        if (!isInIntro && !isPaused) {
            player.update(delta);
            objectAnimationManager.update(player); 
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (isInIntro) {
            g2.drawImage(introImage, 0, 0, screenWidth, screenHeight, null);
        } else if (isInComicFull) {
            g2.drawImage(comicImage, 0, 0, screenWidth, screenHeight, null);
        } else if (isPaused) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.drawImage(pauseImage, 0, 0, screenWidth, screenHeight, null);
        } else {
            tileManager.drawBaseLayer(g2, player.worldX, player.worldY);
            tileManager.drawPlayerTileLayer(g2, player.worldX, player.worldY);
            tileManager.drawMiscLayer(g2, player.worldX, player.worldY);
            tileManager.drawSecondaryLayer(g2, player.worldX, player.worldY);

            objectAnimationManager.drawObjectAnimations(g2, player.worldX, player.worldY);
            objectAnimationManager.drawChestAnimations(g2, player.worldX, player.worldY);
            objectAnimationManager.drawKeyAnimations(g2, player.worldX, player.worldY);
            objectAnimationManager.drawDoorAnimations(g2, player.worldX, player.worldY);

            player.draw(g2);

            g2.setColor(Color.WHITE);
            g2.drawString("Gold Keys: " + player.getGoldKeyCount(), 10, 30);
            g2.drawString("Silver Keys: " + player.getSilverKeyCount(), 10, 50);
        }

        g2.dispose();
    }
}
