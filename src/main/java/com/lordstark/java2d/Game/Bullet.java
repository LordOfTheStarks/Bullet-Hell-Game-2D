package com.lordstark.java2d.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet {
    private final double angle;
    private double x;
    private double y;
    private static final double SPEED = 18; // Speed of the bullet, this is the optimal speed
    public static final double WIDTH = 5;

    // Bullet constructor
    public Bullet(double angle, double x, double y) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    // getX and getY
    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }

    // Render the bullets, it is a black and oval object
    public void render(GraphicsContext graphicsContext, Camera camera) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillOval(this.x - camera.getOffsetX(),
                                 this.y - camera.getOffsetY(), WIDTH, WIDTH);

        this.x += Math.cos(this.angle)*SPEED;
        this.y += Math.sin(this.angle)*SPEED;

    }
}
