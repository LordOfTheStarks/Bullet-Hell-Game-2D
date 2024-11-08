package com.lordstark.java2d.Game;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class SpriteAnimation {
    private Image[] frames;
    private int currentFrameIndex = 0;
    private long lastFrameTime;
    private long frameDuration;

    private final int frameWidth;
    private final int frameHeight;

    public SpriteAnimation(int frameWidth, int frameHeight, int fps) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameDuration = 1000 / fps;
    }

    // Configure animation with variable columns for each row
    public void setAnimationRow(Image spriteSheet, int row, int columns, long newFrameDuration) {
        this.frameDuration = newFrameDuration;
        PixelReader reader = spriteSheet.getPixelReader();
        if (reader == null) {
            System.out.println("Error: Sprite sheet image could not be read.");
            return;
        }

        frames = new Image[columns];
        for (int i = 0; i < columns; i++) {
            int x = i * frameWidth;
            int y = row * frameHeight;
            frames[i] = new WritableImage(reader, x, y, frameWidth, frameHeight);
        }

        currentFrameIndex = 0; // Reset frame index when switching animation
    }

    public Image getFrame() {
        long now = System.currentTimeMillis();
        if (now - lastFrameTime > frameDuration) {
            lastFrameTime = now;
            currentFrameIndex = (currentFrameIndex + 1) % frames.length;
        }
        return frames[currentFrameIndex];
    }

    public boolean isLastFrame() {
        return currentFrameIndex == frames.length - 1;
    }
}
