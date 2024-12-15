package tile;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

public class TileManager {

    GamePanel gp;
    private int[][] baseTileLayer;
    private int[][] playerTileLayer;
    private int[][] secondaryTileLayer;
    private BufferedImage baseTilesetImage;
    //private BufferedImage additionalTilesetImage;

    private String map = "dungeon";

    public TileManager(GamePanel gp) {
        this.gp = gp;
    }

    public void loadMap() {
        try {
            if (map == "dungeon") {
                baseTilesetImage = ImageIO.read(new File("images/map/dungeon/Dungeon_Tileset.png"));
                baseTileLayer = loadCSV("images/map/dungeon/DungeonMap01_Tile Layer 1.csv");
                playerTileLayer = loadCSV("images/map/dungeon/DungeonMap01_Tile Layer 2.csv");
                secondaryTileLayer = loadCSV("images/map/dungeon/DungeonMap01_Rooms.csv");
                
            } else if (map == "forest") {
                
            }else {
                // Load base tileset and tile layer
                baseTilesetImage = ImageIO.read(new File("images/map/atlas.png"));
                baseTileLayer = loadCSV("images/map/SampleMap_Tile Layer 1.csv");

                // Load additional tileset and tile layer
                //additionalTilesetImage = ImageIO.read(new File("images/map/SampleLayer.png"));
                playerTileLayer = loadCSV("images/map/SampleMap_Tile Layer 2.csv");
                secondaryTileLayer = loadCSV("images/map/SampleMap_Tile Layer 3.csv");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[][] loadCSV(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        int[][] layer;
        int row = 0;
        
        if (map == "dungeon" || map == "forest") {
            layer = new int[60][66];
        } else {
            layer = new int[32][32]; // Adjust size based on your map dimensions
        }

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

    public void drawBaseLayer(Graphics2D g2, int playerWorldX, int playerWorldY) {
        drawLayer(g2, baseTilesetImage, baseTileLayer, playerWorldX, playerWorldY);
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

    public void draw(Graphics2D g2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'draw'");
    }
}
