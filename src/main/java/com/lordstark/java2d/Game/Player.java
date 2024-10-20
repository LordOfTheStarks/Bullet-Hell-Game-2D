package com.lordstark.java2d.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

public class Player {
    private double x, y;
    private List<Bullet> bullets = new ArrayList<>();
    private static final double WIDTH = 50;
    private boolean shooting = false;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void render(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(this.x, this.y, WIDTH, WIDTH);

        for (int i = 0; i < this.bullets.size(); i++) {
            this.bullets.get(i).render(graphicsContext);
        }
    }
    public void move(double x, double y) {
        this.x += x;
        this.y += y;
    }
    public void shoot(double x, double y) {
        if (shooting) return;
        shooting = true;
        Game.timerBullet(150, () -> this.shooting = false);
        double angle = Math.atan2(y-this.y, x-this.x);
        Bullet b = new Bullet(angle, this.x+WIDTH/2, this.y+WIDTH/2);
        this.bullets.add(b);
    }
}
