package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import main.entity.Player;

//import main.tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    //Screen Settings
    final int originalTileSize = 16; // 16 x 16 Tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48 x 48 Tile
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; // 768 px
    final int screenHeight = tileSize * maxScreenRow; // 576 px

    //FPS
    int FPS = 60;

   //TileManager tileM = new TileManager(this);

    //Instances
    KeyHandler keyH = new KeyHandler();
    Thread gameThread; // when game thread is called, it automatically runs the 'run' method
    Player player  = new Player(this, keyH);

    //Default player location
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;


    public GamePanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

    }

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();

    }


    @Override
    /*public void run() {


        double drawInterval = 1000000000/FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        //game loop
        while( gameThread != null) {

            

            update();

            repaint();


            try {
            double remainingTime = nextDrawTime - System.nanoTime();
            remainingTime = remainingTime/1000000;

            if(remainingTime < 0) {
                remainingTime = 0;
            }

            
            Thread.sleep((long)remainingTime);

            nextDrawTime += drawInterval;

            }catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }    */
        
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer +=(currentTime - lastTime);

            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;

            }
            if(timer >= 1000000000);

        }

    }

    public void update() {

        player.update();

    

        

    }

    //for drawing the UI
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g; 

        player.draw(g2);

       

        //tileM.draw(g2);

        
        g2.dispose();

    }

}