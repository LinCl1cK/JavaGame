package main;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundHandler {
    private static Clip clip;  // Static variable to hold the clip instance

    // Method to play music
    public static void playMusic(String path) {
        try {
            // Check if clip is already playing, then don't load again
            if (clip != null && clip.isRunning()) {
                return; // Music is already playing, no need to restart it
            }

            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(path));
            clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // Loop music indefinitely
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to stop music
    public static void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();  // Stop the music
        }
    }

    // Method to pause the music
    public static void pauseMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();  // Stop the music without resetting the position
        }
    }

    // Method to resume music
    public static void resumeMusic() {
        if (clip != null && !clip.isRunning()) {
            clip.start();  // Resume the music from where it was paused
        }
    }
}
