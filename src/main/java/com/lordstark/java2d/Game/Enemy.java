package com.lordstark.java2d.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy {
    private double x, y;
    private Player player;
    private static final double WIDTH = 40;
    private static final double SPEED = 2;

    public Enemy(Player p, double x, double y) {
        this.player = p;
        this.x = x;
        this.y = y;
    }
    public void render(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.BLUE);
        graphicsContext.fillOval(this.x, this.y, WIDTH, WIDTH);
        double distance = Math.sqrt(Math.pow(this.x - this.player.getX(), 2)
                                            + Math.pow(this.y - this.player.getY(), 2));
        if (distance <= 120) {

        } else {
            double angle = Math.atan2(this.player.getY() - this.y, this.player.getX() - this.x);
            this.x += Math.cos(angle)*SPEED;
            this.y += Math.sin(angle)*SPEED;
        }
    }
}
