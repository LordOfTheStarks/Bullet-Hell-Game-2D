package com.lordstark.java2d.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Enemy {
    private double x, y;
    private final Player player;
    public static final double WIDTH = 80;
    private static final double SPEED = 2;

    private TerrainManager terrainManager;

    private boolean isAttacking = false;
    private boolean isDead = false;

    private final SpriteAnimation spriteAnimation;
    private final Image walkingSpriteSheet;
    private final Image attackingSpriteSheet;
    private final Image deathSpriteSheet;
    private static final int WALK_COLUMNS = 6;
    private static final int ATTACK_COLUMNS = 6;
    private static final int DEATH_COLUMNS = 6;

    public Enemy(Player p, double x, double y, TerrainManager terrainManager) {
        this.player = p;
        this.x = x;
        this.y = y;
        this.terrainManager = terrainManager;

        // Load the sprite sheets for each animation
        walkingSpriteSheet = new Image(getClass().getResourceAsStream("/Enemy/D_Walk.png"));
        attackingSpriteSheet = new Image(getClass().getResourceAsStream("/Enemy/D_Attack.png"));
        deathSpriteSheet = new Image(getClass().getResourceAsStream("/Enemy/D_Death.png"));

        // Initialize SpriteAnimation with the walking animation by default
        spriteAnimation = new SpriteAnimation(48, 48, 10); // Adjust frame width, height, and fps as needed
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
        double collisionWidth = TerrainManager.getActualSpriteWidth(WIDTH);
        double collisionHeight = TerrainManager.getActualSpriteHeight(WIDTH);
        double offsetX = (WIDTH - collisionWidth) / 2;
        double offsetY = (WIDTH - collisionHeight) / 2;

        for (Enemy e : Game.enemies) {
            if (e != this) {
                double otherOffsetX = (WIDTH - collisionWidth) / 2;
                double otherOffsetY = (WIDTH - collisionHeight) / 2;

                if (nextX + offsetX + collisionWidth > e.x + otherOffsetX &&
                        nextX + offsetX < e.x + otherOffsetX + collisionWidth &&
                        nextY + offsetY + collisionHeight > e.y + otherOffsetY &&
                        nextY + offsetY < e.y + otherOffsetY + collisionHeight) {
                    return true;
                }
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
        double dx = Math.cos(angle) * SPEED;
        double dy = Math.sin(angle) * SPEED;

        double nextX = x + dx;
        double nextY = y + dy;

        // Calculate actual collision bounds
        double collisionWidth = TerrainManager.getActualSpriteWidth(WIDTH);
        double collisionHeight = TerrainManager.getActualSpriteHeight(WIDTH);
        // Offset to center the collision box
        double offsetX = (WIDTH - collisionWidth) / 2;
        double offsetY = (WIDTH - collisionHeight) / 2;

        // Try moving on each axis separately
        if (!terrainManager.collidesWithHouse(nextX + offsetX, y + offsetY,
                                              collisionWidth, collisionHeight) &&
                !checkCollisionWithEnemies(nextX, y)) {
            x = nextX;
        }

        if (!terrainManager.collidesWithHouse(x + offsetX, nextY + offsetY,
                                              collisionWidth, collisionHeight) &&
                !checkCollisionWithEnemies(x, nextY)) {
            y = nextY;
        }

        // Check for attack range
        double distanceToPlayer = Math.sqrt(Math.pow(x - player.getX(), 2) +
                                                    Math.pow(y - player.getY(), 2));

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

    public void takeDamage() {
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
    public void setTerrainManager(TerrainManager terrainManager) {
        this.terrainManager = terrainManager;
    }
}
