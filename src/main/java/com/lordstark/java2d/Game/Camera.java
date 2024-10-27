package com.lordstark.java2d.Game;

import com.lordstark.java2d.AppConfig;

public class Camera {
    private double x, y;

    public Camera(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void update(Player player) {
        // Center camera based on player's position
        this.x = player.getX() - AppConfig.getWidth() / 2;
        this.y = player.getY() - AppConfig.getHeight() / 2;
    }
}
