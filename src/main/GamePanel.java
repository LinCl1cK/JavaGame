package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import entity.Player;

public class GamePanel extends JPanel implements Runnable {

    // Screen Settings
    final int originalTileSize = 16; // 16 x 16 Tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48 x 48 Tile
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 px
    public final int screenHeight = tileSize * maxScreenRow; // 576 px

    private int[][] baseTileLayer;
    private int[][] additionalTileLayer;
    private BufferedImage baseTilesetImage;
    private BufferedImage additionalTilesetImage;

    // Refresh rate
    int FPS = 60;

    // Instances
    KeyHandler keyH = new KeyHandler();
    Thread gameThread; // when game thread is called, it automatically runs the 'run' method
    Player player = new Player(this, keyH);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Load map layers and tilesets
        loadMap();
    }

    private void loadMap() {
        try {
            // Load base tileset and tile layer
            baseTilesetImage = ImageIO.read(new File("images/map/SampleBase.png"));
            baseTileLayer = loadCSV("images/map/SampleMap_Tile Layer 1.csv");

            // Load additional tileset and tile layer
            additionalTilesetImage = ImageIO.read(new File("images/map/SampleLayer.png"));
            additionalTileLayer = loadCSV("images/map/SampleMap_Tile Layer 2.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[][] loadCSV(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        int[][] layer = new int[30][30]; // Adjust size based on your map dimensions
        int row = 0;

        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            for (int col = 0; col < values.length; col++) {
                layer[row][col] = Integer.parseInt(values[col]);
            }
            row++;
        }
        br.close();
        return layer;
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
        drawLayer(g2, baseTilesetImage, baseTileLayer);

        // Draw additional tile layer
        drawLayer(g2, additionalTilesetImage, additionalTileLayer);

        // Draw player
        player.draw(g2);
    }

    private void drawLayer(Graphics g, BufferedImage tilesetImage, int[][] tileLayer) {
        if (tilesetImage != null && tileLayer != null) {
            for (int row = 0; row < tileLayer.length; row++) {
                for (int col = 0; col < tileLayer[row].length; col++) {
                    int tileIndex = tileLayer[row][col];
                    int tilesetX = (tileIndex % (tilesetImage.getWidth() / originalTileSize)) * originalTileSize;
                    int tilesetY = (tileIndex / (tilesetImage.getWidth() / originalTileSize)) * originalTileSize;
                    g.drawImage(tilesetImage, col * tileSize, row * tileSize, col * tileSize + tileSize,
                                row * tileSize + tileSize, tilesetX, tilesetY, 
                                tilesetX + originalTileSize, tilesetY + originalTileSize, this);
                }
            }
        }
    }
}
