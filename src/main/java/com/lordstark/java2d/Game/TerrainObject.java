package com.lordstark.java2d.Game;

import javafx.scene.image.Image;

public class TerrainObject {
    private double x, y;
    private Image image;

    public TerrainObject(double x, double y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public Image getImage() { return image; }

    // Check if tile is empty (no image initially)
    public boolean isEmpty() {
        return image == null;
    }

    // Dynamically change image to another tile
    public void setImage(Image newImage) {
        this.image = newImage;
    }
}
