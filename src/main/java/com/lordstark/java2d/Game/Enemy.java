package com.lordstark.java2d.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy {
    private double x, y;
    private Player player;
    public static final double WIDTH = 40;
    private static final double SPEED = 2;

    public Enemy(Player p, double x, double y) {
        this.player = p;
        this.x = x;
        this.y = y;
    }
    private boolean checkCollision() {
        for(int i = 0; i < Game.enemies.size(); i++) {
            Enemy e = Game.enemies.get(i);
            if (e != this) {
                if (e.collides(this.x, this.y, WIDTH, WIDTH)) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean collides(double x, double y, double w1, double w2) {
        return Math.sqrt(Math.pow(this.x+w1/2-x-w2/2, 2)+Math.pow(this.y+w1/2-y-w2/2, 2)) <= w1/2+w2/2;
    }
    public void render(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.BLUE);
        graphicsContext.fillOval(this.x, this.y, WIDTH, WIDTH);
        double distance = Math.sqrt(Math.pow(this.x - this.player.getX(), 2)
                                            + Math.pow(this.y - this.player.getY(), 2));
        if (distance <= 40) {
            this.player.takeDamage(5);
        } else {
            double angle = Math.atan2(this.player.getY() - this.y, this.player.getX() - this.x);
            this.x += Math.cos(angle)*SPEED;
            if(checkCollision()) {
                this.x -= Math.cos(angle)*SPEED;
            }
            this.y += Math.sin(angle)*SPEED;
            if(checkCollision()) {
                this.y -= Math.sin(angle)*SPEED;
            }
        }
    }
}
