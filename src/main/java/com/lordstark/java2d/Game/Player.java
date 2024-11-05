package com.lordstark.java2d.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.*;

public class Player {
    private double x, y;
    public static List<Bullet> bullets = new ArrayList<>();
    private static final double WIDTH = 70;
    private static final double HEIGHT = 70;
    private boolean shooting = false, damage = false, isDead = false;
    private int hp = 100;
    private SpriteAnimation spriteAnimation;
    private boolean facingLeft = false;

    private Image spriteSheet;
    private Image sideSpriteSheet;
    private Image frontSpriteSheet;
    private Image backSpriteSheet;

    private static final int IDLE_ROW = 0, IDLE_COLUMNS = 6;
    private static final int WALK_ROW = 1, WALK_COLUMNS = 8;
    private static final int SHOOT_ROW = 2, SHOOT_COLUMNS = 6;
    private static final int DAMAGE_ROW = 3, DAMAGE_COLUMNS = 1;
    private static final int DEATH_ROW = 4, DEATH_COLUMNS = 14;

    private static final long IDLE_DURATION = 167;
    private static final long WALK_DURATION = 125;
    private static final long SHOOT_DURATION = 56;
    private static final long DAMAGE_DURATION = 100;
    private static final long DEATH_DURATION = 125;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.sideSpriteSheet = new Image("file:src/main/resources/Player/Player_Side_Sheet.png");
        this.frontSpriteSheet = new Image("file:src/main/resources/Player/Player_Front_Sheet.png");
        this.backSpriteSheet = new Image("file:src/main/resources/Player/Player_Back_Sheet.png");

        this.spriteSheet = sideSpriteSheet;
        this.spriteAnimation = new SpriteAnimation(spriteSheet, 48, 44, 12);
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
        spriteAnimation.setAnimationRow(spriteSheet, IDLE_ROW, IDLE_COLUMNS, IDLE_DURATION);
    }

    private void setWalkAnimation() {
        spriteAnimation.setAnimationRow(spriteSheet, WALK_ROW, WALK_COLUMNS, WALK_DURATION);
    }

    private void setShootAnimation() {
        spriteAnimation.setAnimationRow(spriteSheet, SHOOT_ROW, SHOOT_COLUMNS, SHOOT_DURATION);
    }

    private void setDamageAnimation() {
        spriteAnimation.setAnimationRow(spriteSheet, DAMAGE_ROW, DAMAGE_COLUMNS, DAMAGE_DURATION);
    }

    private void setDeathAnimation() {
        spriteAnimation.setAnimationRow(spriteSheet, DEATH_ROW, DEATH_COLUMNS, DEATH_DURATION);
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
            graphicsContext.drawImage(currentFrame, -(this.x - camera.getOffsetX()) - WIDTH, this.y - camera.getOffsetY(), WIDTH, HEIGHT);
        } else {
            graphicsContext.drawImage(currentFrame, this.x - camera.getOffsetX(), this.y - camera.getOffsetY(), WIDTH, HEIGHT);
        }
        graphicsContext.restore();

        for (int i = 0; i < this.bullets.size(); i++) {
            this.bullets.get(i).render(graphicsContext, camera);
        }
    }
    public void move(double dx, double dy) {
        double newX = x + dx;
        double newY = y + dy;

        if (dx < 0) {
            facingLeft = true;
            this.spriteSheet = sideSpriteSheet;
        } else if (dx > 0) {
            facingLeft = false;
            this.spriteSheet = sideSpriteSheet;
        } else if (dy < 0) {
            this.spriteSheet = backSpriteSheet; // Moving north (back-facing)
        } else if (dy > 0) {
            this.spriteSheet = frontSpriteSheet; // Moving south (front-facing)
        }

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

        // Set appropriate sprite direction
        if (angle < -Math.PI / 4 && angle > -3 * Math.PI / 4) {
            this.spriteSheet = backSpriteSheet; // Shooting north
        } else if (angle > Math.PI / 4 && angle < 3 * Math.PI / 4) {
            this.spriteSheet = frontSpriteSheet; // Shooting south
        } else {
            this.spriteSheet = sideSpriteSheet; // Shooting left or right
            facingLeft = angle > Math.PI / 2 || angle < -Math.PI / 2;
        }

        setShootAnimation();

        Bullet bullet = new Bullet(angle, this.x + WIDTH / 2, this.y + WIDTH / 2);
        bullets.add(bullet);

        Game.timerBullet(150, () -> {
            shooting = false;
            setIdleAnimation();
        });
    }
}
