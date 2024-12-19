package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    // Movement keys
    public boolean upPressed, downPressed, leftPressed, rightPressed, 
                enterPressed, spacePressed, shiftPressed, fKeyPressed; 

    // Pause and exit keys
    public boolean pausePressed, enterPressedForResume; 

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Movement keys
        if(code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if(code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if(code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if(code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if(code == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if(code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if(code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if(code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }

        // Pause and resume handling
        if (code == KeyEvent.VK_ESCAPE) {
            pausePressed = true; // Trigger pause or exit when Escape is pressed
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressedForResume = true; // Trigger resume when Enter is pressed
        }
        if (code == KeyEvent.VK_F) {
            fKeyPressed = true; // Set to true when F is pressed
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        // Movement keys
        if(code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if(code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if(code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if(code == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if(code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if(code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if(code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if(code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_F) {
            fKeyPressed = false; // Reset F key when released
        }
    }

    // Check if any key is pressed to skip intro
    public boolean anyKeyPressed() {
        return upPressed || downPressed || leftPressed || rightPressed 
           || enterPressedForResume || spacePressed || shiftPressed
           || pausePressed; 
    }

    public void resetAllKeys() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        //enterPressedForResume = false;
        spacePressed = false;
        shiftPressed = false;
        //pausePressed = false;
    }
    
}
