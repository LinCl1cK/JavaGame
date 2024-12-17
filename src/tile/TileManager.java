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
    private BufferedImage openDoorSprite;
    private BufferedImage closedDoorSprite;
    public static boolean isDoorOpen = false;

    // Collision variables
    public Tile[] tiles;
    public int[][] collisionLayer;
    public int[][] collisionLayerKey; 
    public int[][] collisionLayerDoor; // Declare collisionLayerDoor

    //Key and door position from csv
    private int  keyCol, keyRow;
    private int doorCol, doorRow;
    private boolean keyPickedUp = false;


    public TileManager(GamePanel gp) {
        this.gp = gp;
        tiles = new Tile[100]; // Adjust size based on the number of unique tiles

        // Initialize each tile in the array
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile(); 
        }

        try {
            openDoorSprite = ImageIO.read(new File("images/open_door.png")); 
            closedDoorSprite = ImageIO.read(new File("images/closed_door.png")); 
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }

    public void loadMap() {
        try {
            // Loading map
            baseTilesetImage = ImageIO.read(new File("images/dungeon/map/Dungeon_Tileset.png"));
            baseTileLayer = loadCSV("images/dungeon/map/DungeonMap01_Tile Layer 1.csv");
            playerTileLayer = loadCSV("images/dungeon/map/DungeonMap01_Tile Layer 2.csv");
            secondaryTileLayer = loadCSV("images/dungeon/map/DungeonMap01_structures.csv");

            // Load collision layers
            collisionLayer = loadCSV("images/dungeon/map/DungeonMap01_Collision.csv");
            collisionLayerKey = loadCSV("images/dungeon/map/DungeonMap01_chesKeyt.csv"); 
            collisionLayerDoor = loadCSV("images/dungeon/map/DungeonMap01_dors.csv"); 

            //Find key and door positions
            findKeyPosition();
            findDoorPosition();

            // Setup tiles with collision info
            setupTiles();

            printCollisionData();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findKeyPosition() {
        for (int row = 0; row < collisionLayerKey.length; row++) {
            for (int col = 0; col < collisionLayerKey[row].length; col++) {
                if (collisionLayerKey[row][col] == 99) { // Assuming key tile ID is 99
                    keyCol = col;
                    keyRow = row;
                    break;
                }
            }
            if (keyCol != -1 && keyRow != -1) {
                break;
            }
        }
    }

    private void findDoorPosition() {
        // Assuming only one door for simplicity
        for (int row = 0; row < collisionLayerDoor.length; row++) {
            for (int col = 0; col < collisionLayerDoor[row].length; col++) {
                if (collisionLayerDoor[row][col] == 36) { // Assuming door tile ID is 36
                    doorCol = col;
                    doorRow = row;
                    break;
                }
            }
            if (doorCol != -1 && doorRow != -1) {
                break;
            }
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
        int playerTileX = playerArea.x / gp.tileSize;
        int playerTileY = playerArea.y / gp.tileSize;

        // Check if player is within map boundaries
        if (playerTileX < 0 || playerTileX >= collisionLayer.length || 
            playerTileY < 0 || playerTileY >= collisionLayer[0].length) {
            return false; // Player is outside the map, no collision
        }

        int tileIndex = collisionLayer[playerTileY][playerTileX];
        if (tiles[tileIndex].collision) {
            return true;
        }

        return false;
    }

    public boolean checkCollisionWithKey(Rectangle playerArea) {
        if (collisionLayerKey == null){
            return false;
        }
        for (int row = 0; row < collisionLayerKey.length; row++) {
            for (int col = 0; col < collisionLayerKey[row].length; col++) {
                if (collisionLayerKey[row][col] == 99) { // Check for key tile ID (99)
                    int tileX = col * gp.tileSize;
                    int tileY = row * gp.tileSize;
                    Rectangle keyArea = new Rectangle(tileX, tileY, gp.tileSize, gp.tileSize);

                    if (playerArea.intersects(keyArea)) {
                        keyPickedUp = true; 
                        System.out.println("Key Picked Up!"); // Print to console
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkCollisionWithDoor(Rectangle playerArea) {
        if (collisionLayerDoor == null) {
            return false;
        }
        for (int row = 0; row < collisionLayerDoor.length; row++) {
            for (int col = 0; col < collisionLayerDoor[row].length; col++) {
                if (collisionLayerDoor[row][col] == 36) { // Check for door tile ID (100)
                    int tileX = col * gp.tileSize;
                    int tileY = row * gp.tileSize;
                    Rectangle doorArea = new Rectangle(tileX, tileY, gp.tileSize, gp.tileSize);

                    if (playerArea.intersects(doorArea)) {
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
                        if (tileIndex == 99) { // Check for door tile
                            if (isDoorOpen) {
                                // Draw open door sprite here
                                g.drawImage(openDoorSprite, screenX, screenY, gp.tileSize, gp.tileSize, null); 
                            } else {
                                // Draw closed door sprite here
                                g.drawImage(closedDoorSprite, screenX, screenY, gp.tileSize, gp.tileSize, null);
                            }
                        } else {
                            g.drawImage(tilesetImage, screenX, screenY, screenX + gp.tileSize, screenY + gp.tileSize,
                                tilesetX, tilesetY, tilesetX + gp.originalTileSize, tilesetY + gp.originalTileSize, gp);
                        }
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