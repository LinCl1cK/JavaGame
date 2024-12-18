package main;

import java.awt.Rectangle;
import tile.TileManager;

public class CollisionManager {
    private final TileManager tileManager;

    public CollisionManager(TileManager tileManager) {
        this.tileManager = tileManager;
    }

    public boolean isCollision(Rectangle nextPosition) {
        int tileSize = tileManager.getTileSize(); // Fetch tileSize from TileManager
        for (int row = 0; row < tileManager.getCollisionLayer().length; row++) {
            for (int col = 0; col < tileManager.getCollisionLayer()[row].length; col++) {
                if (tileManager.getCollisionLayer()[row][col] == 1) { // Assuming 1 is a solid tile
                    int tileX = col * tileSize;
                    int tileY = row * tileSize;
                    Rectangle tileArea = new Rectangle(tileX, tileY, tileSize, tileSize);
                    if (nextPosition.intersects(tileArea)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
