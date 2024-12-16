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
    private int[][] chestTileLayer;
    private BufferedImage baseTilesetImage;
    private BufferedImage additionalTilesetImage;

    private String map = "dungeon";

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
            if (map.equals("dungeon")) {
                baseTilesetImage = ImageIO.read(new File("images/map/dungeon/Dungeon_Tileset.png"));
                baseTileLayer = loadCSV("images/map/dungeon/DungeonMap01_Tile Layer 1.csv");
                playerTileLayer = loadCSV("images/map/dungeon/DungeonMap01_Tile Layer 2.csv");
                secondaryTileLayer = loadCSV("images/map/dungeon/DungeonMap01_structures.csv");

                // Load collision layer
                collisionLayer = loadCSV("images/map/dungeon/DungeonMap01_Collision.csv");

            } else if (map.equals("forest")) {
                baseTilesetImage = ImageIO.read(new File("images/map/forest/atlas.png"));
                additionalTilesetImage = ImageIO.read(new File("images/map/forest/Fantasy RPG (Toony) 32x32.png"));
                baseTileLayer = loadCSV("images/map/forest/ForestMap01_Tile Layer 1.csv");
                playerTileLayer = loadCSV("images/map/forest/ForestMap01_path.csv");
                secondaryTileLayer = loadCSV("images/map/forest/ForestMap01_structures.csv");
                chestTileLayer = loadCSV("images/map/forest/ForestMap01_chest.csv");

                // Load collision layer
                collisionLayer = loadCSV("path/to/forest/ForestMap01_Collision.csv");

            } else {
                // Load base tileset and tile layer
                baseTilesetImage = ImageIO.read(new File("images/map/atlas.png"));
                baseTileLayer = loadCSV("images/map/SampleMap_Tile Layer 1.csv");

                // Load additional tileset and tile layer
                playerTileLayer = loadCSV("images/map/SampleMap_Tile Layer 2.csv");
                secondaryTileLayer = loadCSV("images/map/SampleMap_Tile Layer 3.csv");

                // Load collision layer
                collisionLayer = loadCSV("path/to/sample/SampleMap_Collision.csv");
            }

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

            if (map.equals("dungeon") || map.equals("forest")) {
                layer = new int[60][66];
            } else {
                layer = new int[32][32]; // Adjust size based on your map dimensions
            }

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

    public void drawChestLayer(Graphics2D g2, int playerWorldX, int playerWorldY) {
        drawLayer(g2, additionalTilesetImage, chestTileLayer, playerWorldX, playerWorldY);
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
