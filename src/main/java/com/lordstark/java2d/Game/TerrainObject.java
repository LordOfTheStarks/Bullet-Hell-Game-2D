package com.lordstark.java2d.Game;

import javafx.scene.image.Image;

public record TerrainObject(double x, double y, Image image) {

    // Add these methods to get the width and height of the image
    public double getWidth() {
        return image != null ? image.getWidth() : 0;
    }

    public double getHeight() {
        return image != null ? image.getHeight() : 0;
    }
}
