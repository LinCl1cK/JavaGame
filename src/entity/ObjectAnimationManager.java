package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.GamePanel;
import tile.TileManager;

public class ObjectAnimationManager {
        private GamePanel gp;
        private TileManager tileManager;
        private Map<String, BufferedImage[][]> animations;
        private int[][] objectMap, doorMap, chestMap, keyMap;
        private List<Chest> chests;
    
        public ObjectAnimationManager(GamePanel gp) {
            this.gp = gp;
            this.animations = new HashMap<>();
        }
    
        public void setTileManager(TileManager tileManager) {
            this.tileManager = tileManager;
    }    

    public void addAnimation(String key, BufferedImage[][] animationFrames) {
        animations.put(key, animationFrames);
    }

    public void setMaps(int[][] objectMap, int[][] doorMap, int[][] chestMap, int[][] keyMap, List<Chest> chests) {
        this.objectMap = objectMap;
        this.doorMap = doorMap;
        this.chestMap = chestMap;
        this.keyMap = keyMap;
        this.chests = chests;
    }

    private void drawAnimatedLayer(Graphics2D g2, int[][] map, String animationKey, int playerWorldX, int playerWorldY) {
        long currentTime = System.currentTimeMillis();
        BufferedImage[][] frames = animations.get(animationKey);
        if (frames == null) return;

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                int tileIndex = map[row][col];
                if (tileIndex >= 0 && tileIndex < frames.length) {
                    int frameIndex = (int) ((currentTime / 500) % frames[tileIndex].length); 
                    BufferedImage sprite = frames[tileIndex][frameIndex];

                    int screenX = col * tileManager.getTileSize() - playerWorldX + gp.screenWidth / 2;
                    int screenY = row * tileManager.getTileSize() - playerWorldY + gp.screenHeight / 2;

                    if (screenX + tileManager.getTileSize() > 0 && screenX < gp.screenWidth &&
                        screenY + tileManager.getTileSize() > 0 && screenY < gp.screenHeight) {
                        g2.drawImage(sprite, screenX, screenY, tileManager.getTileSize(), tileManager.getTileSize(), null);
                    }
                }
            }
        }
    }

    public void drawObjectAnimations(Graphics2D g2, int playerWorldX, int playerWorldY) {
        drawAnimatedLayer(g2, objectMap, "object", playerWorldX, playerWorldY);
    }

    public void drawDoorAnimations(Graphics2D g2, int playerWorldX, int playerWorldY) {
        drawAnimatedLayer(g2, doorMap, "door", playerWorldX, playerWorldY);
    }

    public void drawChestAnimations(Graphics2D g2, int playerWorldX, int playerWorldY) {
        //if (chests == null) return; // ✅ Prevent crash if chests is null

        for (Chest chest : chests) {
            if (chest != null) {
                BufferedImage sprite = chest.getCurrentFrame();
                int screenX = chest.getX() - playerWorldX + gp.screenWidth / 2;
                int screenY = chest.getY() - playerWorldY + gp.screenHeight / 2;
                g2.drawImage(sprite, screenX, screenY, tileManager.getTileSize(), tileManager.getTileSize(), null);
            }
        }
    }
    
    public void drawKeyAnimations(Graphics2D g2, int playerWorldX, int playerWorldY) {
        drawAnimatedLayer(g2, keyMap, "object", playerWorldX, playerWorldY);
    }

    public void updateKeyCollection(Player player) {
        for (int row = 0; row < keyMap.length; row++) {
            for (int col = 0; col < keyMap[row].length; col++) {
                int keyIndex = keyMap[row][col];

                if (keyIndex == 7 || keyIndex == 8) {
                    int keyTileX = col * tileManager.getTileSize();
                    int keyTileY = row * tileManager.getTileSize();
                    Rectangle keyBounds = new Rectangle(keyTileX, keyTileY, tileManager.getTileSize(), tileManager.getTileSize());

                    if (player.collisionBounds.intersects(keyBounds)) {
                        player.collectKey(keyIndex);
                        keyMap[row][col] = 11;
                        System.out.println("Key collected! Gold: " + player.getGoldKeyCount() + " Silver: " + player.getSilverKeyCount());
                    }
                }
            }
        }
    }

    private boolean isPlayerNearChest(Player player, Chest chest) {
        Rectangle chestBounds = new Rectangle(chest.getX(), chest.getY(), tileManager.getTileSize(), tileManager.getTileSize());
        return player.getCollisionBounds().intersects(chestBounds);
    }
    

    public void interactWithChest(Player player) {
        for (Chest chest : chests) {
            if (chest != null && isPlayerNearChest(player, chest) && !chest.isOpened()) {
                if (player.getSilverKeyCount() > 0) {
                    chest.open();
                    player.consumeSilverKey();
                    System.out.println("Chest opened! A silver key was used.");
                } else {
                    System.out.println("Player needs a silver key to open this chest.");
                }
                break;
            }
        }
    }
    

    public void update(Player player) {
        for (Chest chest : chests) {
            chest.update();
        }
    }
}
