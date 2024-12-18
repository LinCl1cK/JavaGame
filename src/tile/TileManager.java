package tile;

import entity.Player;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.CollisionManager;
import main.GamePanel;

public class TileManager {

    
    GamePanel gp;
    Player player;
    CollisionManager collisionManager;
    private int[][] baseTileLayer;
    private int[][] playerTileLayer;
    private int[][] secondaryTileLayer;
    private int[][] miscTileLayer;
    private BufferedImage baseTilesetImage;
    private BufferedImage additionalTilesetImage;

    public final int tileSize;

    public int[][] objectMap;
    public int[][] doorMap;
    private int[][] chestMap;
    private int[][] keyMap;
    public BufferedImage[][] objectAnimations;
    public BufferedImage[][] doorAnimations;

    // Collision variables
    public Tile[] tiles; 
    public int[][] collisionLayer;

    public int getTileSize() {
        return tileSize;
    }    

    public int[][]  getCollisionLayer() {
        return collisionLayer;
    }

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tiles = new Tile[50]; // Adjust size based on the number of unique tiles
        this.tileSize = gp.originalTileSize * gp.scale;
    }

    //private List<Chest> chests = new ArrayList<>();

    public void loadMap() {
        try {
            // Loading map
            baseTilesetImage = ImageIO.read(new File("src/assets/dungeon/map/Dungeon_Tileset.png"));
            baseTileLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_Tile Layer 1.csv");
            playerTileLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_Tile Layer 2.csv");
            secondaryTileLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_structures.csv");

            // Load maps and spritesheets
            objectMap = loadCSV("src/assets/dungeon/object/DungeonMap01_Objects.csv");
            doorMap = loadCSV("src/assets/dungeon/object/DungeonMap01_Doors.csv");
            chestMap = loadCSV("src/assets/dungeon/object/DungeonMap01_Chests.csv");
            keyMap = loadCSV("src/assets/dungeon/object/DungeonMap01_Keys.csv");
            objectAnimations = loadAnimatedSpriteSheet("src/assets/dungeon/object/Assets.png", 11, 4);
            doorAnimations = loadAnimatedSpriteSheet("src/assets/dungeon/object/door.png", 4, 2);

            // Load collision layer
            collisionLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_Collision.csv");

            // Setup tiles with collision info
            setupTiles();

            //printCollisionData();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[][] loadCSV(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int[][] layer;

            layer = new int[60][66]; // Adjust size based on your map dimensions

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

    public BufferedImage getSprite(BufferedImage sheet, int col, int row) {
        return sheet.getSubimage((col - 1) * gp.originalTileSize, (row - 1) * gp.originalTileSize, gp.originalTileSize, gp.originalTileSize);
    }


    public BufferedImage[][] loadAnimatedSpriteSheet(String filePath, int rows, int frames) throws IOException {
        BufferedImage sheet = ImageIO.read(new File(filePath));
        BufferedImage[][] sprites = new BufferedImage[rows][frames];
        for (int row = 0; row < rows; row++) {
            for (int frame = 0; frame < frames; frame++) {
                sprites[row][frame] = sheet.getSubimage(frame * gp.originalTileSize, row * gp.originalTileSize, gp.originalTileSize, gp.originalTileSize);
            }
        }
        return sprites;
    }

    private void drawAnimatedLayer(Graphics2D g2, int[][] map, BufferedImage[][] animations, int playerWorldX, int playerWorldY, long currentTime) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                int tileIndex = map[row][col];
                if (tileIndex >= 0 && tileIndex < animations.length) {
                    int frameIndex = (int) ((currentTime / 500) % animations[tileIndex].length);  // Change '100' to control speed
                    BufferedImage sprite = animations[tileIndex][frameIndex];
    
                    int screenX = col * tileSize - playerWorldX + gp.screenWidth / 2;
                    int screenY = row * tileSize - playerWorldY + gp.screenHeight / 2;
    
                    // Only draw tiles within the visible screen area
                    if (screenX + tileSize > 0 && screenX < gp.screenWidth &&
                        screenY + tileSize > 0 && screenY < gp.screenHeight) {
                        g2.drawImage(sprite, screenX, screenY, tileSize, tileSize, null);
                    }
                }
            }
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
                    int tileX = col * tileSize;
                    int tileY = row * tileSize;
                    Rectangle tileArea = new Rectangle(tileX, tileY, tileSize, tileSize);

                    if (playerArea.intersects(tileArea)) {
                        System.out.println("Collision detected at: (" + tileX + ", " + tileY + ")");
                        return true;
                    }
                }
            }
        }
        return false;
    }

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

                    int screenX = col * tileSize - playerWorldX + gp.screenWidth / 2;
                    int screenY = row * tileSize - playerWorldY + gp.screenHeight / 2;

                    // Only draw tiles within the visible screen area
                    if (screenX + tileSize > 0 && screenX < gp.screenWidth &&
                        screenY + tileSize > 0 && screenY < gp.screenHeight) {
                        g.drawImage(tilesetImage, screenX, screenY, screenX + tileSize, screenY + tileSize,
                                    tilesetX, tilesetY, tilesetX + gp.originalTileSize, tilesetY + gp.originalTileSize, gp);
                    }
                }
            }
        }
    }

    // public void drawObjectsAndDoors(Graphics2D g2, int playerWorldX, int playerWorldY) {
    //     drawObjectAnimations(g2, playerWorldX, playerWorldY);
    //     drawDoorAnimations(g2, playerWorldX, playerWorldY);
    // }

    public void drawObjectAnimations(Graphics2D g2, int playerWorldX, int playerWorldY) {
        long currentTime = System.currentTimeMillis();
        drawAnimatedLayer(g2, objectMap, objectAnimations, playerWorldX, playerWorldY, currentTime);
    }

    public void drawDoorAnimations(Graphics2D g2, int playerWorldX, int playerWorldY) {
        long currentTime = System.currentTimeMillis();
        drawAnimatedLayer(g2, doorMap, doorAnimations, playerWorldX, playerWorldY, currentTime);
    }

    public void drawChestAnimations(Graphics2D g2, int playerWorldX, int playerWorldY) {
        long currentTime = System.currentTimeMillis();
        drawAnimatedLayer(g2, chestMap, objectAnimations, playerWorldX, playerWorldY, currentTime);
    }

    public void drawKeyAnimations(Graphics2D g2, int playerWorldX, int playerWorldY) {
        long currentTime = System.currentTimeMillis();
        drawAnimatedLayer(g2, keyMap, objectAnimations, playerWorldX, playerWorldY, currentTime);
    }
    public void updateKeyCollection(Player player) {
        // Iterate through the keyMap to find key tiles
        for (int row = 0; row < keyMap.length; row++) {
            for (int col = 0; col < keyMap[row].length; col++) {
                int keyIndex = keyMap[row][col];
    
                // Check if the player is near a key and is not already collected (key value should not be zero)
                if (keyIndex == 7 || keyIndex == 8) {
                    // Calculate the tile position
                    int keyTileX = col * tileSize;
                    int keyTileY = row * tileSize;
    
                    Rectangle keyBounds = new Rectangle(keyTileX, keyTileY, tileSize, tileSize);
    
                    // Check if the player intersects with the key tile
                    if (player.collisionBounds.intersects(keyBounds)) {
                        // Collect the key based on its index (7 for Gold, 8 for Silver)
                        player.collectKey(keyIndex);
    
                        // Remove the key from the keyMap (set it to 0, indicating the key is collected)
                        keyMap[row][col] = 11;
                        System.out.println("Key collected! Gold: " + player.getGoldKeyCount() + " Silver: " + player.getSilverKeyCount());
                    }
                }
            }
        }
    }
    
        
    // Testing method to print collision data
    // public void printCollisionData() {
    //     System.out.println("Collision Layer Data:");
    //     for (int row = 0; row < collisionLayer.length; row++) {
    //         for (int col = 0; col < collisionLayer[row].length; col++) {
    //             System.out.print(collisionLayer[row][col] + " ");
    //         }
    //         System.out.println();
    //     }

    //     System.out.println("\nTile Collision Properties:");
    //     for (int i = 0; i < tiles.length; i++) {
    //         System.out.println("Tile " + i + ": " + (tiles[i].collision ? "solid" : "walkable"));
    //     }
    // }

    // Testing method to check collision at specific coordinates
    public boolean checkCollisionAt(int x, int y) {
        int playerWidth = player.collisionBounds.width; // Use the player's collision bounds width
        int playerHeight = player.collisionBounds.height; // Use the player's collision bounds height
        Rectangle testArea = new Rectangle(x, y, playerWidth, playerHeight);
        boolean collision = isCollision(testArea);
        System.out.println("Collision at (" + x + ", " + y + "): " + collision);
        return collision;
    }


    
}
