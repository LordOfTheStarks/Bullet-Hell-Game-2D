package com.lordstark.java2d.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.*;

public class Player {
    private double x, y;
    public static List<Bullet> bullets = new ArrayList<>();
    private static final double WIDTH = 50;
    private boolean shooting = false, damage = false, isDead = false;
    private int hp = 100;
    private SpriteAnimation spriteAnimation;
    private boolean facingLeft = false;
    private Image spriteSheet;

    private static final int IDLE_ROW = 0, IDLE_COLUMNS = 6;
    private static final int WALK_ROW = 1, WALK_COLUMNS = 8;
    private static final int SHOOT_ROW = 2, SHOOT_COLUMNS = 6;
    private static final int DAMAGE_ROW = 3, DAMAGE_COLUMNS = 1;
    private static final int DEATH_ROW = 4, DEATH_COLUMNS = 14;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.spriteSheet = new Image("file:src/main/resources/Player_Side_Sheet.png");
        this.spriteAnimation = new SpriteAnimation(spriteSheet, 48, 44, 30); // Set frame width/height based on sprite dimensions
        setIdleAnimation(); // Start with idle animation
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
    private void setIdleAnimation() {
        spriteAnimation.setAnimationRow(spriteSheet, IDLE_ROW, IDLE_COLUMNS);
    }

    private void setWalkAnimation() {
        spriteAnimation.setAnimationRow(spriteSheet, WALK_ROW, WALK_COLUMNS);
    }

    private void setShootAnimation() {
        spriteAnimation.setAnimationRow(spriteSheet, SHOOT_ROW, SHOOT_COLUMNS);
    }

    private void setDamageAnimation() {
        spriteAnimation.setAnimationRow(spriteSheet, DAMAGE_ROW, DAMAGE_COLUMNS);
    }

    private void setDeathAnimation() {
        spriteAnimation.setAnimationRow(spriteSheet, DEATH_ROW, DEATH_COLUMNS);
    }

    public void takeDamage(int dmg) {
        if (damage || isDead) return;
        this.hp -= dmg;
        damage = true;

        if (this.hp <= 0) {
            death(); // Trigger death animation if health is depleted
        } else {
            setDamageAnimation();
            Game.timerBullet(150, () -> {
                damage = false;
                setIdleAnimation();
            });
        }
    }
    public void death() {
        isDead = true;
        setDeathAnimation();
    }
    public void render(GraphicsContext graphicsContext, Camera camera) {
        if (isDead && spriteAnimation.isLastFrame()) {
            // Stop rendering if death animation is complete
            return;
        }

        Image currentFrame = spriteAnimation.getFrame();
        graphicsContext.save();
        if (facingLeft) {
            graphicsContext.scale(-1, 1);
            graphicsContext.drawImage(currentFrame, -(this.x - camera.getOffsetX()) - WIDTH, this.y - camera.getOffsetY());
        } else {
            graphicsContext.drawImage(currentFrame, this.x - camera.getOffsetX(), this.y - camera.getOffsetY());
        }
        graphicsContext.restore();

        for (int i = 0; i < this.bullets.size(); i++) {
            this.bullets.get(i).render(graphicsContext, camera);
        }
    }
    public void move(double dx, double dy) {
        double newX = x + dx;
        double newY = y + dy;

        if (dx < 0) facingLeft = true;
        if (dx > 0) facingLeft = false;

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
        if (dx != 0 || dy != 0) {
            setWalkAnimation();
        } else {
            setIdleAnimation();
        }
    }
    public void shoot(double mouseX, double mouseY) {
        if (shooting || isDead) return;
        shooting = true;
        setShootAnimation();

        double worldMouseX = mouseX + Game.camera.getOffsetX();
        double worldMouseY = mouseY + Game.camera.getOffsetY();
        double angle = Math.atan2(worldMouseY - this.y, worldMouseX - this.x);

        Bullet bullet = new Bullet(angle, this.x + WIDTH / 2, this.y + WIDTH / 2);
        bullets.add(bullet);

        Game.timerBullet(150, () -> {
            shooting = false;
            setIdleAnimation();
        });
    }
}
