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
    private boolean checkCollisionWithEnemies(double nextX, double nextY) {
        for (Enemy e : Game.enemies) {
            if (e != this && e.collides(nextX, nextY, WIDTH, WIDTH)) {
                return true;
            }
        }
        return false;
    }
    public boolean collides(double x, double y, double w1, double w2) {
        return Math.sqrt(Math.pow(this.x+w1/2-x-w2/2, 2)+Math.pow(this.y+w1/2-y-w2/2, 2)) <= w1/2+w2/2;
    }
    public void render(GraphicsContext graphicsContext, Camera camera) {
        graphicsContext.setFill(Color.BLUE);
        graphicsContext.fillOval(x - camera.getOffsetX(),
                                 y - camera.getOffsetY(), WIDTH, WIDTH);

        // Calculate angle to move toward the player
        double angle = Math.atan2(player.getY() - y, player.getX() - x);
        double nextX = x + Math.cos(angle) * SPEED;
        double nextY = y + Math.sin(angle) * SPEED;

        // Check collisions with walls and other enemies on X and Y axes separately
        boolean canMoveX = true;
        boolean canMoveY = true;

        for (Wall wall : Game.getWalls()) {
            if (wall.collides(nextX, y, WIDTH, WIDTH)) {
                canMoveX = false;
            }
            if (wall.collides(x, nextY, WIDTH, WIDTH)) {
                canMoveY = false;
            }
        }

        // Check collisions with other enemies
        if (checkCollisionWithEnemies(nextX, y)) {
            canMoveX = false;
        }
        if (checkCollisionWithEnemies(x, nextY)) {
            canMoveY = false;
        }

        // Apply movement only if no collision was detected
        if (canMoveX) {
            x = nextX;
        }
        if (canMoveY) {
            y = nextY;
        }

        // Check distance to the player to deal damage if close enough
        double distanceToPlayer = Math.sqrt(Math.pow(this.x - this.player.getX(), 2) + Math.pow(this.y - this.player.getY(), 2));
        if (distanceToPlayer <= 40) {
            this.player.takeDamage(5);
        }
    }
}
