package com.lordstark.java2d.Game;

import com.lordstark.java2d.AppConfig;

public class Camera {
    private double offsetX, offsetY;

    public Camera(double offsetX, double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    // Updates camera position to center on the player
    public void update(Player player) {
        this.offsetX = player.getX() - (double) AppConfig.getWidth() / 2;
        this.offsetY = player.getY() - (double) AppConfig.getHeight() / 2;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }
}
