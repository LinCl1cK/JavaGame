package tile;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import entity.Player;
import main.GamePanel;

public class TileManager {

    GamePanel gp;
    Player player;
    private int[][] baseTileLayer;
    private int[][] playerTileLayer;
    private int[][] secondaryTileLayer;
    private int[][] miscTileLayer;
    private BufferedImage baseTilesetImage;
    private BufferedImage additionalTilesetImage;

    // Collision variables
    public Tile[] tiles; 
    public int[][] collisionLayer;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tiles = new Tile[100]; // Adjust size based on the number of unique tiles
    }

    public void loadMap() {
        try {
            // Loading map
            baseTilesetImage = ImageIO.read(new File("src/assets/dungeon/map/Dungeon_Tileset.png"));
            baseTileLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_Tile Layer 1.csv");
            playerTileLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_Tile Layer 2.csv");
            secondaryTileLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_structures.csv");

            // Load collision layer
            collisionLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_Collision.csv");

            // Setup tiles with collision info
            setupTiles();

            printCollisionData();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[][] loadCSV(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int[][] layer;

            layer = new int[60][66];

            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (int col = 0; col < values.length; col++) {
                    layer[row][col] = Integer.parseInt(values[col]);
                }
                row++;
            }
            return layer;
        }
    }

    private void setupTiles() {
        // Initialize all tiles
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile();
            tiles[i].collision = false; // Default to walkable
        }
    
        // Set collision properties based on collisionLayer
        for (int row = 0; row < collisionLayer.length; row++) {
            for (int col = 0; col < collisionLayer[row].length; col++) {
                int tileIndex = collisionLayer[row][col];
                if (tileIndex >= 0 && tileIndex < tiles.length) {
                    tiles[tileIndex].collision = (tileIndex != 0); // Assuming tileIndex 0 is walkable
                }
            }
        }
    }
    
    public boolean isCollision(Rectangle playerArea) {
        for (int row = 0; row < collisionLayer.length; row++) {
            for (int col = 0; col < collisionLayer[row].length; col++) {
                int tileIndex = collisionLayer[row][col];
    
                if (tiles[tileIndex].collision) {
                    int tileX = col * gp.tileSize;
                    int tileY = row * gp.tileSize;
                    Rectangle tileArea = new Rectangle(tileX, tileY, gp.tileSize, gp.tileSize);
    
                    if (playerArea.intersects(tileArea)) {
                        System.out.println("Collision detected at: (" + tileX + ", " + tileY + ")");
                        return true;
                    }
                }
            }
        }
        return false;
    }
        
    // MovePlayer is now integrated in Player class, no need here

    public void drawBaseLayer(Graphics2D g2, int playerWorldX, int playerWorldY) {
        drawLayer(g2, baseTilesetImage, baseTileLayer, playerWorldX, playerWorldY);
    }

    public void drawMiscLayer(Graphics2D g2, int playerWorldX, int playerWorldY) {
        drawLayer(g2, additionalTilesetImage, miscTileLayer, playerWorldX, playerWorldY);
    }

    public void drawPlayerTileLayer(Graphics2D g2, int playerWorldX, int playerWorldY) {
        drawLayer(g2, baseTilesetImage, playerTileLayer, playerWorldX, playerWorldY);
    }

    public void drawSecondaryLayer(Graphics2D g2, int playerWorldX, int playerWorldY) {
        drawLayer(g2, baseTilesetImage, secondaryTileLayer, playerWorldX, playerWorldY);
    }

    private void drawLayer(Graphics g, BufferedImage tilesetImage, int[][] tileLayer, int playerWorldX, int playerWorldY) {
        if (tilesetImage != null && tileLayer != null) {
            for (int row = 0; row < tileLayer.length; row++) {
                for (int col = 0; col < tileLayer[row].length; col++) {
                    int tileIndex = tileLayer[row][col];
                    int tilesetX = (tileIndex % (tilesetImage.getWidth() / gp.originalTileSize)) * gp.originalTileSize;
                    int tilesetY = (tileIndex / (tilesetImage.getWidth() / gp.originalTileSize)) * gp.originalTileSize;

                    int screenX = col * gp.tileSize - playerWorldX + gp.screenWidth / 2;
                    int screenY = row * gp.tileSize - playerWorldY + gp.screenHeight / 2;

                    // Only draw tiles within the visible screen area
                    if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                        screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
                        g.drawImage(tilesetImage, screenX, screenY, screenX + gp.tileSize, screenY + gp.tileSize,
                                    tilesetX, tilesetY, tilesetX + gp.originalTileSize, tilesetY + gp.originalTileSize, gp);
                    }
                }
            }
        }
    }

    // Testing method to print collision data
    public void printCollisionData() {
        System.out.println("Collision Layer Data:");
        for (int row = 0; row < collisionLayer.length; row++) {
            for (int col = 0; col < collisionLayer[row].length; col++) {
                System.out.print(collisionLayer[row][col] + " ");
            }
            System.out.println();
        }
    
        System.out.println("\nTile Collision Properties:");
        for (int i = 0; i < tiles.length; i++) {
            System.out.println("Tile " + i + ": " + (tiles[i].collision ? "solid" : "walkable"));
        }
    }
    
    // Testing method to check collision at specific coordinates
    public boolean checkCollisionAt(int x, int y) {
        Rectangle testArea = new Rectangle(x, y, player.collisionArea.width, player.collisionArea.height);
        boolean collision = isCollision(testArea);
        System.out.println("Collision at (" + x + ", " + y + "): " + collision);
        return collision;
    }
}