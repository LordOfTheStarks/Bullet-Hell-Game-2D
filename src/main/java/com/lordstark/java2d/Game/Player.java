package com.lordstark.java2d.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

public class Player {
    private double x, y;
    public static List<Bullet> bullets = new ArrayList<>();
    private static final double WIDTH = 50;
    private boolean shooting = false, damage = false;
    private int hp = 100;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }
    public int getHp() {
        return this.hp;
    }
    public void takeDamage(int dmg) {
        if(damage) return;
        this.hp -= dmg;
        damage = true;
        Game.timerBullet(150, () -> damage = false);
    }
    public void render(GraphicsContext graphicsContext, Camera camera) {
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(this.x - camera.getOffsetX(),
                                 this.y - camera.getOffsetY(), WIDTH, WIDTH);

        for (int i = 0; i < this.bullets.size(); i++) {
            this.bullets.get(i).render(graphicsContext, camera);
        }
    }
    public void move(double dx, double dy) {
        double newX = x + dx;
        double newY = y + dy;

        for (Wall wall : Game.getWalls()) {
            if (wall.collides(newX, y, WIDTH, WIDTH)) {
                dx = 0;
            }
            if (wall.collides(x, newY, WIDTH, WIDTH)) {
                dy = 0;
            }
        }
        this.x += dx;
        this.y += dy;
    }
    public void shoot(double mouseX, double mouseY) {
        if (shooting) return;
        shooting = true;
        Game.timerBullet(150, () -> this.shooting = false);

        double worldMouseX = mouseX + Game.camera.getOffsetX();
        double worldMouseY = mouseY + Game.camera.getOffsetY();

        double angle = Math.atan2(worldMouseY-this.y, worldMouseX-this.x);
        Bullet bullet = new Bullet(angle, this.x+WIDTH/2, this.y+WIDTH/2);
        this.bullets.add(bullet);
    }
}
