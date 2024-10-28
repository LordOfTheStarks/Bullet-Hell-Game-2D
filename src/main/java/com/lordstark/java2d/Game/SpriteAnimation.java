// SpriteAnimation.java
package com.lordstark.java2d.Game;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;

public class SpriteAnimation {
    private Image[] frames;
    private int currentFrameIndex = 0;
    private int frameCount;
    private long lastFrameTime;
    private long frameDuration;

    public SpriteAnimation(Image spriteSheet, int columns, int rows, int fps) {
        this.frameCount = columns * rows;
        this.frames = new Image[frameCount];
        this.frameDuration = 1000 / fps;

        int frameWidth = (int) spriteSheet.getWidth() / columns;
        int frameHeight = (int) spriteSheet.getHeight() / rows;

        PixelReader reader = spriteSheet.getPixelReader();
        if (reader == null) {
            System.out.println("Error: Sprite sheet image could not be read.");
            return;
        }

        // Extract frames dynamically
        for (int i = 0; i < frameCount; i++) {
            int x = (i % columns) * frameWidth;
            int y = (i / columns) * frameHeight;

            // Debugging output to verify dimensions
            System.out.println("Extracting frame at position: " + x + "," + y);
            System.out.println("Calculated frame size: " + frameWidth + "x" + frameHeight);

            frames[i] = new WritableImage(reader, x, y, frameWidth, frameHeight);
        }
    }

    public Image getFrame() {
        long now = System.currentTimeMillis();
        if (now - lastFrameTime > frameDuration) {
            lastFrameTime = now;
            currentFrameIndex = (currentFrameIndex + 1) % frameCount;
        }
        return frames[currentFrameIndex];
    }
}
