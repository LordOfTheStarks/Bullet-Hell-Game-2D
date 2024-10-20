package com.lordstark.java2d.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet {
    private double angle, x, y;
    private static final double SPEED = 18;

    public Bullet(double angle, double x, double y) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }
    public void render(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.GRAY);
        graphicsContext.fillOval(this.x, this.y, 20, 20);

        this.x += Math.cos(this.angle)*SPEED;
        this.y += Math.sin(this.angle)*SPEED;
    }
}
