package com.lordstark.java2d.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Wall {
    public double x, y, width, height;

    public Wall(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
    public void render(GraphicsContext gc, Camera camera) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x - camera.getOffsetX(), y - camera.getOffsetY(), width, height);
    }
    public boolean collides(double px, double py, double pWidth, double pHeight) {
        return px < x + width && px + pWidth > x && py < y + height && py + pHeight > y;
    }
}
