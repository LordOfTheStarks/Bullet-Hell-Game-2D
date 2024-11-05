package com.lordstark.java2d.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Enemy {
    private double x, y;
    private Player player;
    public static final double WIDTH = 80;
    private static final double SPEED = 2;

    private boolean isAttacking = false;
    private boolean isDead = false;

    private SpriteAnimation spriteAnimation;
    private Image walkingSpriteSheet;
    private Image attackingSpriteSheet;
    private Image deathSpriteSheet;
    private static final int WALK_COLUMNS = 6;
    private static final int ATTACK_COLUMNS = 6;
    private static final int DEATH_COLUMNS = 6;

    public Enemy(Player p, double x, double y) {
        this.player = p;
        this.x = x;
        this.y = y;

        // Load the sprite sheets for each animation
        walkingSpriteSheet = new Image("file:src/main/resources/Enemy/D_Walk.png");
        attackingSpriteSheet = new Image("file:src/main/resources/Enemy/D_Attack.png");
        deathSpriteSheet = new Image("file:src/main/resources/Enemy/D_Death.png");

        // Initialize SpriteAnimation with the walking animation by default
        spriteAnimation = new SpriteAnimation(walkingSpriteSheet, 48, 48, 10); // Adjust frame width, height, and fps as needed
        setWalkingAnimation();
    }

    private void setWalkingAnimation() {
        spriteAnimation.setAnimationRow(walkingSpriteSheet, 0, WALK_COLUMNS, 83);
    }
    private void setAttackingAnimation() {
        spriteAnimation.setAnimationRow(attackingSpriteSheet, 0, ATTACK_COLUMNS, 83);
    }
    private void setDeathAnimation() {
        spriteAnimation.setAnimationRow(deathSpriteSheet, 0, DEATH_COLUMNS, 56);
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
        if (isDead && spriteAnimation.isLastFrame()) {
            // Stop rendering if the death animation is complete
            return;
        }

        Image currentFrame = spriteAnimation.getFrame();
        graphicsContext.drawImage(currentFrame, x - camera.getOffsetX(), y - camera.getOffsetY(), WIDTH, WIDTH);

        if (!isDead) {
            moveTowardsPlayer();
        }
    }
        private void moveTowardsPlayer() {
            if (isDead) return;

            double angle = Math.atan2(player.getY() - y, player.getX() - x);
            double nextX = x + Math.cos(angle) * SPEED;
            double nextY = y + Math.sin(angle) * SPEED;

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

            if (checkCollisionWithEnemies(nextX, y)) {
                canMoveX = false;
            }
            if (checkCollisionWithEnemies(x, nextY)) {
                canMoveY = false;
            }

            if (canMoveX) x = nextX;
            if (canMoveY) y = nextY;

            double distanceToPlayer = Math.sqrt(Math.pow(x - player.getX(), 2) + Math.pow(y - player.getY(), 2));

            if (distanceToPlayer <= 40 && !isAttacking) {
                startAttacking();
            } else if (distanceToPlayer > 40 && isAttacking) {
                stopAttacking();
            }
        }
    private void startAttacking() {
        isAttacking = true;
        setAttackingAnimation();
        Game.timerBullet(500, () -> {
            player.takeDamage(10);
            stopAttacking();
        });
    }

    private void stopAttacking() {
        isAttacking = false;
        setWalkingAnimation();
    }

    public void takeDamage(int damage) {
        if (isDead) return;

        // If health reaches 0, set to dead and trigger death animation
        isDead = true;
        setDeathAnimation();
    }
    public boolean isDead() {
        return isDead;
    }
    public boolean isDeathAnimationComplete() {
        return isDead && spriteAnimation.isLastFrame();
    }
}
