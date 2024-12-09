package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    //Screen Settings
    final int originalTileSize = 16; // 16 x 16 Tile
    final int scale = 3;

    final int tileSize = originalTileSize * scale; // 48 x 48 Tile
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; // 768 px
    final int screenHeight = tileSize * maxScreenRow; // 576 px

    Thread gameThread; // when game thread is called, it automatically runs the 'run' method


    public GamePanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

    }

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();

    }


    @Override
    public void run() {

        //game loop
        while( gameThread != null) {

            update();

            repaint();
        }

    }    

    public void update() {

    }

    //for drawing the UI
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g; // Upgrade version of drawing

    }

}