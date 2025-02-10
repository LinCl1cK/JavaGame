package tile;

import entity.Chest;
import entity.ObjectAnimationManager;
import entity.Player;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import main.CollisionManager;
import main.GamePanel;

public class TileManager {

    
    GamePanel gp;
    Player player;
    ObjectAnimationManager objectAnimationManager;
    CollisionManager collisionManager;
    private int[][] baseTileLayer;
    private int[][] playerTileLayer;
    private int[][] secondaryTileLayer;
    private int[][] miscTileLayer;
    private BufferedImage baseTilesetImage;
    private BufferedImage additionalTilesetImage;

    private final int tileSize;

    private int[][] objectMap, doorMap, chestMap, keyMap;
    public List<Chest> chests;
    public BufferedImage[][] objectAnimations;
    public BufferedImage[][] doorAnimations;

    // Collision variables
    public Tile[] tiles; 
    public int[][] collisionLayer;

    public int[][] getObjectMap() {
        return objectMap;
    }

    public int[][] getDoorMap() {
        return doorMap;
    }

    public int[][] getChestMap() {
        return chestMap;
    }

    public int[][] getKeyMap() {
        return keyMap;
    }

    public List<Chest> getChests() {
        return chests;
    }

    public TileManager(GamePanel gp, ObjectAnimationManager objectAnimationManager) {  // Pass ObjectAnimationManager as a parameter
        this.gp = gp;
        this.objectAnimationManager = objectAnimationManager;  // ✅ Initialize here
        tiles = new Tile[50];
        this.tileSize = gp.originalTileSize * gp.scale;
    }
        
    public int getTileSize() {
        return tileSize;
    }    

    public int[][]  getCollisionLayer() {
        return collisionLayer;
    }

    public void loadMap() {
        try {
            baseTilesetImage = ImageIO.read(new File("src/assets/dungeon/map/Dungeon_Tileset.png"));
            baseTileLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_Tile Layer 1.csv");
            playerTileLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_Tile Layer 2.csv");
            secondaryTileLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_structures.csv");
    
            objectMap = loadCSV("src/assets/dungeon/object/DungeonMap01_Objects.csv");
            doorMap = loadCSV("src/assets/dungeon/object/DungeonMap01_Doors.csv");
            chestMap = loadCSV("src/assets/dungeon/object/DungeonMap01_Chests.csv");
            keyMap = loadCSV("src/assets/dungeon/object/DungeonMap01_Keys.csv");
    
            if (objectAnimationManager == null) {
                throw new IllegalStateException("Error: objectAnimationManager is null! Make sure it's initialized in GamePanel.");
            }
            BufferedImage[][] chestAnimations = loadAnimatedSpriteSheet("src/assets/dungeon/object/Chest.png", 2, 4);

            objectAnimationManager.addAnimation("object", loadAnimatedSpriteSheet("src/assets/dungeon/object/Assets.png", 12, 4));
            objectAnimationManager.addAnimation("door", loadAnimatedSpriteSheet("src/assets/dungeon/object/door.png", 4, 2));
            objectAnimationManager.addAnimation("chest", chestAnimations);

            chests = new ArrayList<>();
            chests.add(new Chest(24 * tileSize, 38 * tileSize, chestAnimations));
            chests.add(new Chest(11 * tileSize, 29 * tileSize, chestAnimations));
            chests.add(new Chest(53 * tileSize, 27 * tileSize, chestAnimations));
            chests.add(new Chest(13 * tileSize, 12 * tileSize, chestAnimations));
            chests.add(new Chest(54 * tileSize, 11 * tileSize, chestAnimations));
            chests.add(new Chest(53 * tileSize, 11 * tileSize, chestAnimations));
            chests.add(new Chest(54 * tileSize, 10 * tileSize, chestAnimations));
            chests.add(new Chest(53 * tileSize, 10 * tileSize, chestAnimations));
            
            collisionLayer = loadCSV("src/assets/dungeon/map/DungeonMap01_Collision.csv");
            setupTiles();
    
            objectAnimationManager.setMaps(objectMap, doorMap, chestMap, keyMap, this.getChests());  // ✅ FIXED

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
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
    
        BufferedImage sheet = ImageIO.read(file);
        BufferedImage[][] sprites = new BufferedImage[rows][frames];
    
        for (int row = 0; row < rows; row++) {
            for (int frame = 0; frame < frames; frame++) {
                sprites[row][frame] = sheet.getSubimage(frame * gp.originalTileSize, row * gp.originalTileSize, 
                                                        gp.originalTileSize, gp.originalTileSize);
            }
        }
        return sprites;
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

    public void update(Player player) {
        objectAnimationManager.update(player);
    }    

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