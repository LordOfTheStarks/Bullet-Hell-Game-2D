package com.lordstark.java2d.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet {
    private double angle, x, y;
    private static final double SPEED = 18;
    public static final double WIDTH = 5;

    public Bullet(double angle, double x, double y) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }
    public void render(GraphicsContext graphicsContext, Camera camera) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillOval(this.x - camera.getOffsetX(),
                                 this.y - camera.getOffsetY(), WIDTH, WIDTH);

        this.x += Math.cos(this.angle)*SPEED;
        this.y += Math.sin(this.angle)*SPEED;

    }
}
