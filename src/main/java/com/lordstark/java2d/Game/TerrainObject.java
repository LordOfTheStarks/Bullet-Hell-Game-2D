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
}
