package com.lordstark.java2d.Game;

import java.util.*;

import com.lordstark.java2d.AppConfig;
import com.lordstark.java2d.Menu.MainMenu;
import javafx.animation.KeyFrame;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Game extends Application {

    public static Camera camera;
    private static final double SPEED = 8;
    private Player player;
    private final Map<KeyCode, Boolean> keys = new HashMap<>();
    public static List<Enemy> enemies = new ArrayList<>();
    private int score = 0;
    private TerrainManager terrainManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        terrainManager = new TerrainManager();

        stage.setTitle("Shooter game");

        StackPane pane = new StackPane();

        camera = new Camera(0, 0);

        // Get width and height from AppConfig
        Canvas canvas = new Canvas(AppConfig.getWidth(), AppConfig.getHeight());
        canvas.setFocusTraversable(true);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        pane.getChildren().add(canvas);

        this.player = new Player(50, 50);
        this.player.setTerrainManager(terrainManager); // Link player to terrainManager

        Timeline loop = new Timeline(new KeyFrame(Duration.millis(1000.0/60),
                                                  e -> update(graphicsContext)));
        loop.setCycleCount(Animation.INDEFINITE);
        loop.play();

        spawnEnemies();

        for (Enemy e : enemies) {
            e.setTerrainManager(terrainManager);
        }

        canvas.setOnKeyPressed(e -> {
            this.keys.put(e.getCode(), true);
            if (e.getCode() == KeyCode.ESCAPE) {
                System.exit(0);
            }
        });
        canvas.setOnKeyReleased(e -> this.keys.put(e.getCode(), false));
        canvas.setOnMousePressed(e -> this.player.shoot(e.getX(), e.getY()));
        canvas.setOnMouseDragged(e -> this.player.shoot(e.getX(), e.getY()));

        Scene scene = new Scene(pane, AppConfig.getWidth(), AppConfig.getHeight());
        stage.setScene(scene);
        stage.show();
    }
    public static void timerBullet(long time, Runnable r) {
        new Thread(() -> {
            try {
                Thread.sleep(time);
                r.run();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public void spawnEnemies() {
        Thread spawner = new Thread(() -> {
            try {
                Random random = new Random();
                while (true) {
                    double x = random.nextDouble()*AppConfig.getWidth();
                    double y = random.nextDouble()*AppConfig.getHeight();
                    enemies.add(new Enemy(this.player, x, y, terrainManager));
                    Thread.sleep(2000);
                }
            }catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        spawner.start();
    }
    private void update(GraphicsContext graphicsContext) {
        // Clamp player position to world bounds
        double clampedX = Math.max((double) -TerrainManager.WORLD_WIDTH /2,
                                   Math.min((double) TerrainManager.WORLD_WIDTH /2, player.getX()));
        double clampedY = Math.max((double) -TerrainManager.WORLD_HEIGHT /2,
                                   Math.min((double) TerrainManager.WORLD_HEIGHT /2, player.getY()));
        player.setPosition(clampedX, clampedY);

        // Update and clean up bullets that are out of bounds
        for (int i = Player.bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = Player.bullets.get(i);
            if (bullet.getX() < (double) -TerrainManager.WORLD_WIDTH /2 ||
                    bullet.getX() > (double) TerrainManager.WORLD_WIDTH /2 ||
                    bullet.getY() < (double) -TerrainManager.WORLD_HEIGHT /2 ||
                    bullet.getY() > (double) TerrainManager.WORLD_HEIGHT /2) {
                Player.bullets.remove(i);
            }
        }

        camera.update(player);

        // Update terrain around the player's position
        terrainManager.updateTerrain(player.getX(), player.getY());

        // Render the base tile layer dynamically
        double tileWidth = terrainManager.getMainTile().getWidth();
        double tileHeight = terrainManager.getMainTile().getHeight();
        for (int x = 0; x < AppConfig.getWidth(); x += (int) tileWidth) {
            for (int y = 0; y < AppConfig.getHeight(); y += (int) tileHeight) {
                graphicsContext.drawImage(
                        terrainManager.getMainTile(),
                        x - camera.getOffsetX() % tileWidth,
                        y - camera.getOffsetY() % tileHeight
                );
            }
        }

        // Render dynamically generated terrain (grass, rocks, etc.)
        terrainManager.render(graphicsContext, camera);

        // Check and update bullets, enemies, player, etc.
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            e.render(graphicsContext, camera);

            // Check for bullet collisions with enemies and houses
            for (int j = 0; j < Player.bullets.size(); j++) {
                Bullet bullet = Player.bullets.get(j);
                if (terrainManager.collidesWithHouse(bullet.getX(), bullet.getY(), Bullet.WIDTH, Bullet.WIDTH)) {
                    Player.bullets.remove(j);
                    j--;  // Adjust bullet index after removal
                    continue;
                }
                if (e.collides(bullet.getX(), bullet.getY(), Enemy.WIDTH, Bullet.WIDTH)) {
                    Player.bullets.remove(j);
                    e.takeDamage();
                    score += 10;
                    break;
                }
            }

            if (e.isDead() && e.isDeathAnimationComplete()) {
                enemies.remove(i);
                i--; // Adjust index for removed enemy
            }
        }

        this.player.render(graphicsContext, camera);

        if (this.keys.getOrDefault(KeyCode.W, false)){
            this.player.move(0, -SPEED);
        }
        if (this.keys.getOrDefault(KeyCode.A, false)){
            this.player.move(-SPEED, 0);
        }
        if (this.keys.getOrDefault(KeyCode.S, false)){
            this.player.move(0, SPEED);
        }
        if (this.keys.getOrDefault(KeyCode.D, false)){
            this.player.move(SPEED, 0);
        }
        //DRAWS HP BAR
        graphicsContext.setFill(Color.GREEN);
        graphicsContext.fillRect(50, AppConfig.getHeight()-80, 100*(this.player.getHp()/100.0), 30);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeRect(50, AppConfig.getHeight()-80, 100, 30);

        //Display SCORE
        double fontSize = AppConfig.getWidth() * 0.025;
        graphicsContext.setFont(Font.loadFont(Objects.requireNonNull(MainMenu.class.getResource("/joystix-monospace.otf")).toExternalForm(), fontSize));
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText("Score: " + score, 10, 20);
    }
}
