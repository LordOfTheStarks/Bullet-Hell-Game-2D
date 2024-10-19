package com.lordstark.java2d.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {
    private double x, y;
    private static final double WIDTH = 50;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void render(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(this.x, this.y, WIDTH, WIDTH);
    }
    public void move(double x, double y) {
        this.x += x;
        this.y += y;
    }
}
