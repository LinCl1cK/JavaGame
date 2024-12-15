package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import entity.Player;
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
    public final int maxWorldCol = 66;
    public final int maxWorldRow = 60;
    public final int worldWidth = tileSize * maxScreenCol;
    public final int worldHeight = tileSize * maxScreenRow;

    // Refresh rate
    int FPS = 60;

   TileManager tileM = new TileManager(this);


    // Instances
    KeyHandler keyH = new KeyHandler();
    Thread gameThread; // when game thread is called, it automatically runs the 'run' method
    public Player player = new Player(this, keyH);
    TileManager tileManager = new TileManager(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Load map layers and tilesets
        tileManager.loadMap();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    /*public void run() {


        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        //game loop
        while( gameThread != null) {

            

            update();

            repaint();


            try {
            double remainingTime = nextDrawTime - System.nanoTime();
            remainingTime = remainingTime/1000000;

            if(remainingTime < 0) {
                remainingTime = 0;
            }

            
            Thread.sleep((long)remainingTime);

            nextDrawTime += drawInterval;

            }catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }    */
        
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
        player.update(delta);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // Upgrade version of drawing

        // Draw base tile layer
        tileManager.drawBaseLayer(g2, player.worldX, player.worldY);
        
        tileManager.drawPlayerTileLayer(g2, player.worldX, player.worldY);

        // Draw player
        player.draw(g2);
        
        // Draw additional tile layer
        tileManager.drawSecondaryLayer(g2, player.worldX, player.worldY);
    }
}
