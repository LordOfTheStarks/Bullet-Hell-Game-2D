package com.lordstark.java2d.Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Enemy {
    private double x, y; // Position of the enemy
    private final Player player;
    public static final double WIDTH = 80; // Enemy Width, this can be used to arrange size of enemies
    private static final double SPEED = 2; // Speed of the enemies, this is optimal

    private TerrainManager terrainManager; // Used for collision detection, trees etc.

    private boolean isAttacking = false; // If enemy attacking
    private boolean isDead = false; // If enemy is dead

    // These are for sprite animations
    private final SpriteAnimation spriteAnimation;
    private final Image walkingSpriteSheet;
    private final Image attackingSpriteSheet;
    private final Image deathSpriteSheet;

    // These are for sprite sheet, since there are 6 columns, it's been set like this
    private static final int WALK_COLUMNS = 6;
    private static final int ATTACK_COLUMNS = 6;
    private static final int DEATH_COLUMNS = 6;

    // Constructor to initialize the enemy with player, position and terrain management
    public Enemy(Player p, double x, double y, TerrainManager terrainManager) {
        this.player = p;
        this.x = x;
        this.y = y;
        this.terrainManager = terrainManager;

        // Load the sprite sheets for each different animation
        walkingSpriteSheet = new Image(getClass().getResourceAsStream("/Enemy/D_Walk.png"));
        attackingSpriteSheet = new Image(getClass().getResourceAsStream("/Enemy/D_Attack.png"));
        deathSpriteSheet = new Image(getClass().getResourceAsStream("/Enemy/D_Death.png"));

        // Initialize sprite animation with default walking animation settings
        spriteAnimation = new SpriteAnimation(48, 48, 10); // This is precise and calculated according to the sprite sheet, DON'T TOUCH!
        setWalkingAnimation(); // Sets the default animation as walking animation
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

    // Check if the enemy collides with any other enemies during its movement
    private boolean checkCollisionWithEnemies(double nextX, double nextY) {
        // Calculate the collision bounds for the enemy
        double collisionWidth = TerrainManager.getActualSpriteWidth(WIDTH);
        double collisionHeight = TerrainManager.getActualSpriteHeight(WIDTH);
        double offsetX = (WIDTH - collisionWidth) / 2;
        double offsetY = (WIDTH - collisionHeight) / 2;

        // Check collision with other enemies in the game
        for (Enemy e : Game.enemies) {
            if (e != this) {
                double otherOffsetX = (WIDTH - collisionWidth) / 2;
                double otherOffsetY = (WIDTH - collisionHeight) / 2;

                // Checks if the enemy's position overlaps with another enemy's position
                if (nextX + offsetX + collisionWidth > e.x + otherOffsetX &&
                        nextX + offsetX < e.x + otherOffsetX + collisionWidth &&
                        nextY + offsetY + collisionHeight > e.y + otherOffsetY &&
                        nextY + offsetY < e.y + otherOffsetY + collisionHeight) {
                    return true; // Collision detected
                }
            }
        }
        return false; // No collision detected
    }
    // Check if this enemy collides with the given position (for general collision check)
    public boolean collides(double x, double y, double w1, double w2) {
        // Distance formula
        return Math.sqrt(Math.pow(this.x+w1/2-x-w2/2, 2)+Math.pow(this.y+w1/2-y-w2/2, 2)) <= w1/2+w2/2;
    }

    // Render the enemy on the screen using the given graphics context and camera
    public void render(GraphicsContext graphicsContext, Camera camera) {
        if (isDead && spriteAnimation.isLastFrame()) {
            // Stop rendering if the death animation is complete
            return;
        }

        // Get the current frame from the animation
        Image currentFrame = spriteAnimation.getFrame();
        // Draw the current frame of the enemy, adjusted for camera offset
        graphicsContext.drawImage(currentFrame, x - camera.getOffsetX(), y - camera.getOffsetY(), WIDTH, WIDTH);

        if (!isDead) {
            moveTowardsPlayer(); // Move towards the player if the enemy is not dead
        }
    }
    // Move the enemy towards the player
    private void moveTowardsPlayer() {
        if (isDead) return;

        // Calculate the direction towards the player using trigonometry
        double angle = Math.atan2(player.getY() - y, player.getX() - x);
        double dx = Math.cos(angle) * SPEED; // Change in x (horizontal movement)
        double dy = Math.sin(angle) * SPEED; // Change in y (vertial movement)

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
    // Start the attacking animation and damage the player after a short delay
    private void startAttacking() {
        isAttacking = true;
        setAttackingAnimation();
        Game.timerBullet(500, () -> {
            player.takeDamage(10);
            stopAttacking();
        });
    }

    // Stop the attacking animation and return to walking animation
    private void stopAttacking() {
        isAttacking = false;
        setWalkingAnimation();
    }

    // Triggering death animation if health reaches zero, which means player shoots at enemy
    public void takeDamage() {
        if (isDead) return;

        // If health reaches 0, set to dead and trigger death animation
        isDead = true;
        setDeathAnimation();
    }
    public boolean isDead() {
        return isDead;
    }
    // Return if the death animation is complete
    public boolean isDeathAnimationComplete() {
        return isDead && spriteAnimation.isLastFrame();
    }
    // Set the terrain manager, allowing dynamic updates to the terrain
    public void setTerrainManager(TerrainManager terrainManager) {
        this.terrainManager = terrainManager;
    }
}
