package com.lordstark.java2d.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.*;

public class Player {
    private double x, y; // Position for player
    public static List<Bullet> bullets = new ArrayList<>(); // List of bullets fired
    private static final double WIDTH = 70; // WIDTH of player
    private static final double HEIGHT = 70; // HEIGHT of player and these are optimal
    private boolean shooting = false, damage = false, isDead = false, isMoving = false;
    private long lastMoveTime; // Time of the last movement
    private static final long MOVE_THRESHOLD = 50;
    private boolean deathAnimationComplete = false; // Flag to check if the death animation is complete
    private int hp = 100; // Player's health
    private final SpriteAnimation spriteAnimation; // Manages the sprite animation
    private boolean facingLeft = false; // Tracks if the player is facing left
    private TerrainManager terrainManager; // Used to handle collisions with the terrain



    private Image spriteSheet; // Current sprite sheet
    private final Image sideSpriteSheet;
    private final Image frontSpriteSheet;
    private final Image backSpriteSheet;

    // Animation settings for different actions (idle, walking, shooting, etc.), these are precise
    private static final int IDLE_ROW = 0, IDLE_COLUMNS = 6;
    private static final int WALK_ROW = 1, WALK_COLUMNS = 8;
    private static final int SHOOT_ROW = 2, SHOOT_COLUMNS = 6;
    private static final int DAMAGE_ROW = 3, DAMAGE_COLUMNS = 1;
    private static final int DEATH_ROW = 4, DEATH_COLUMNS = 14;

    private static final long IDLE_DURATION = 125;
    private static final long WALK_DURATION = 167;
    private static final long SHOOT_DURATION = 125;
    private static final long DAMAGE_DURATION = 100;
    private static final long DEATH_DURATION = 125;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.sideSpriteSheet = new Image(getClass().getResourceAsStream("/Player/Player_Side_Sheet.png"));
        this.frontSpriteSheet = new Image(getClass().getResourceAsStream("/Player/Player_Front_Sheet.png"));
        this.backSpriteSheet = new Image(getClass().getResourceAsStream("/Player/Player_Back_Sheet.png"));

        this.spriteSheet = sideSpriteSheet; // Default sprite is side view
        this.spriteAnimation = new SpriteAnimation(48, 44, 12); //This is precise Initialize the animation with frame size and rate
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

    // Method to handle the player taking damage
    public void takeDamage(int dmg) {
        if (damage || isDead) return;
        this.hp -= dmg;
        damage = true;

        if (this.hp <= 0) {
            death(); // Trigger death animation if health is finished
        } else {
            setDamageAnimation();
            Game.timerBullet(150, () -> {
                damage = false;
                setIdleAnimation();
            });
        }
    }
    // Check if the player is dead
    public boolean isDead() {
        return isDead;
    }

    public boolean isDeathAnimationComplete() {
        return deathAnimationComplete;
    }

    // Handle the player's death (trigger death animation)
    public void death() {
        isDead = true;
        setDeathAnimation();
        // Set deathAnimationComplete when animation finishes
        Game.timerBullet(DEATH_DURATION * DEATH_COLUMNS, () -> {
            deathAnimationComplete = true;
        });
    }
    public void render(GraphicsContext graphicsContext, Camera camera) {
        if (isDead && spriteAnimation.isLastFrame()) {
            // Stop rendering if death animation is complete
            return;
        }
        // Update animation state if player has stopped moving
        if (isMoving && System.currentTimeMillis() - lastMoveTime > MOVE_THRESHOLD) {
            isMoving = false;
            if (!shooting && !damage) {
                setIdleAnimation();
            }
        }

        // Flip the sprite horizontally if the player is facing left
        Image currentFrame = spriteAnimation.getFrame();
        graphicsContext.save();
        if (facingLeft) {
            graphicsContext.scale(-1, 1);
            graphicsContext.drawImage(currentFrame, -(this.x - camera.getOffsetX()) - WIDTH, this.y - camera.getOffsetY(), WIDTH, HEIGHT);
        } else {
            graphicsContext.drawImage(currentFrame, this.x - camera.getOffsetX(), this.y - camera.getOffsetY(), WIDTH, HEIGHT);
        }
        graphicsContext.restore();

        for (Bullet bullet : bullets) {
            bullet.render(graphicsContext, camera);
        }
    }
    // Method to move the player
    public void move(double dx, double dy) {
        if (isDead) return;

        double newX = x + dx;
        double newY = y + dy;
        boolean moved = false;

        // Calculate actual collision bounds
        double collisionWidth = TerrainManager.getActualSpriteWidth(WIDTH);
        double collisionHeight = TerrainManager.getActualSpriteHeight(HEIGHT);
        // Offset to center the collision box
        double offsetX = (WIDTH - collisionWidth) / 2;
        double offsetY = (HEIGHT - collisionHeight) / 2;

        // Check for collisions and move along each axis if no collision
        if (dx != 0) {
            if (!terrainManager.collidesWithHouse(newX + offsetX, y + offsetY,
                                                  collisionWidth, collisionHeight)) {
                x = newX;
                moved = true;
            }
        }

        if (dy != 0) {
            if (!terrainManager.collidesWithHouse(x + (moved ? dx : 0) + offsetX,
                                                  newY + offsetY,
                                                  collisionWidth, collisionHeight)) {
                y = newY;
                moved = true;
            }
        }

        // Set the appropriate sprite based on movement direction
        if (dx < 0) {
            facingLeft = true;
            this.spriteSheet = sideSpriteSheet;
        } else if (dx > 0) {
            facingLeft = false;
            this.spriteSheet = sideSpriteSheet;
        } else if (dy < 0) {
            this.spriteSheet = backSpriteSheet;
        } else if (dy > 0) {
            this.spriteSheet = frontSpriteSheet;
        }

        // If the player moved, update animation and state
        if (moved) {
            isMoving = true;
            lastMoveTime = System.currentTimeMillis();
            if (!shooting && !damage) {
                setWalkAnimation();
            }
        } else if (isMoving && System.currentTimeMillis() - lastMoveTime > MOVE_THRESHOLD) {
            // Player has stopped moving
            isMoving = false;
            if (!shooting && !damage) {
                setIdleAnimation();
            }
        }
    }
    // Method for the player to shoot a bullet
    public void shoot(double mouseX, double mouseY) {
        if (shooting || isDead) return;
        shooting = true;
        setShootAnimation();

        double worldMouseX = mouseX + Game.camera.getOffsetX();
        double worldMouseY = mouseY + Game.camera.getOffsetY();
        double angle = Math.atan2(worldMouseY - this.y, worldMouseX - this.x);

        // Sets appropriate sprite direction
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
    // Set the terrain manager for collision detection
    public void setTerrainManager(TerrainManager terrainManager) {
        this.terrainManager = terrainManager;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
