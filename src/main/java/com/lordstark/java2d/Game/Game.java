package com.lordstark.java2d.Game;

import java.util.*;

import com.lordstark.java2d.AppConfig;
import com.lordstark.java2d.Menu.MainMenu;
import com.lordstark.java2d.Menu.Settings;
import javafx.animation.KeyFrame;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

    private Stage primaryStage;
    private Timeline gameLoop;
    private StackPane root;
    private Canvas canvas;
    private VBox gameOverMenu;
    private VBox pauseMenu;
    private boolean gameWon = false;
    private boolean isPaused = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Shooter game");

        initializeGame();
    }

    private void initializeGame() {
        // Reset game state
        score = 0;
        gameWon = false;
        isPaused = false;
        enemies.clear();
        Player.bullets.clear();

        root = new StackPane();
        terrainManager = new TerrainManager();
        camera = new Camera(0, 0);

        // Get width and height from AppConfig
        canvas = new Canvas(AppConfig.getWidth(), AppConfig.getHeight());
        canvas.setFocusTraversable(true);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        this.player = new Player(50, 50);
        this.player.setTerrainManager(terrainManager);

        gameLoop = new Timeline(new KeyFrame(Duration.millis(1000.0/60),
                                             e -> update(graphicsContext)));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();

        spawnEnemies();

        for (Enemy e : enemies) {
            e.setTerrainManager(terrainManager);
        }

        setupInputHandlers();

        Scene scene = new Scene(root, AppConfig.getWidth(), AppConfig.getHeight());
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize menus but don't add them yet
        createGameOverMenu();
        createPauseMenu();
    }
    private void createPauseMenu() {
        pauseMenu = new VBox(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        pauseMenu.setPrefWidth(AppConfig.getWidth());
        pauseMenu.setPrefHeight(AppConfig.getHeight());

        Text pauseText = new Text("Game Paused");
        pauseText.setFont(Font.loadFont(Objects.requireNonNull(
                MainMenu.class.getResource("/joystix-monospace.otf")).toExternalForm(), 40));
        pauseText.setFill(Color.WHITE);

        Button resumeButton = new Button("Resume");
        resumeButton.setOnAction(e -> resumeGame());
        styleButton(resumeButton);

        Button replayButton = new Button("Replay");
        replayButton.setOnAction(e -> restartGame());
        styleButton(replayButton);

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> openSettings());
        styleButton(settingsButton);

        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setOnAction(e -> returnToMainMenu());
        styleButton(mainMenuButton);

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> System.exit(0));
        styleButton(quitButton);

        pauseMenu.getChildren().addAll(
                pauseText,
                resumeButton,
                replayButton,
                settingsButton,
                mainMenuButton,
                quitButton
        );
    }
    private void createGameOverMenu() {
        gameOverMenu = new VBox(20);
        gameOverMenu.setAlignment(Pos.CENTER);

        Button replayButton = new Button("Replay");
        replayButton.setOnAction(e -> restartGame());
        styleButton(replayButton);

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> System.exit(0));
        styleButton(quitButton);

        gameOverMenu.getChildren().addAll(replayButton, quitButton);
    }
    private void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-min-width: 200px;" +
                        "-fx-cursor: hand"
        );

        button.setOnMouseEntered(e ->
                                         button.setStyle(button.getStyle() + ";-fx-background-color: #45a049;"));
        button.setOnMouseExited(e ->
                                        button.setStyle(button.getStyle().replace(";-fx-background-color: #45a049", "")));
    }
    private void handleGameOver() {
        gameLoop.stop();

        // Create the text based on current game state
        Text gameOverText = new Text(gameWon ? "You Won" : "You Lost");
        gameOverText.setFont(Font.loadFont(Objects.requireNonNull(
                MainMenu.class.getResource("/joystix-monospace.otf")).toExternalForm(), 40));
        gameOverText.setFill(Color.WHITE);

        // Clear previous text if it exists and add new text at the beginning
        gameOverMenu.getChildren().removeIf(node -> node instanceof Text);
        gameOverMenu.getChildren().add(0, gameOverText);

        root.getChildren().add(gameOverMenu);
    }

    private void setupInputHandlers() {
        canvas.setOnKeyPressed(e -> {
            keys.put(e.getCode(), true);
            if (e.getCode() == KeyCode.ESCAPE) {
                togglePause();
            }
        });
        canvas.setOnKeyReleased(e -> keys.put(e.getCode(), false));
        canvas.setOnMousePressed(e -> {
            if (!isPaused) {
                player.shoot(e.getX(), e.getY());
            }
        });
        canvas.setOnMouseDragged(e -> {
            if (!isPaused) {
                player.shoot(e.getX(), e.getY());
            }
        });
    }
    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseGame();
        } else {
            resumeGame();
        }
    }
    private void pauseGame() {
        gameLoop.stop();
        root.getChildren().add(pauseMenu);
    }
    private void resumeGame() {
        root.getChildren().remove(pauseMenu);
        gameLoop.play();
        isPaused = false;
        canvas.requestFocus();
    }
    private void openSettings() {
        gameLoop.stop(); // Pause the game while in settings
        Settings settings = new Settings(primaryStage, this);
        settings.show();
    }

    public void resizeGame(int width, int height) {
        // Resize the canvas
        canvas.setWidth(width);
        canvas.setHeight(height);

        // Update pause menu size if it exists
        if (pauseMenu != null) {
            pauseMenu.setPrefWidth(width);
            pauseMenu.setPrefHeight(height);
        }

        // Update game over menu size if it exists
        if (gameOverMenu != null) {
            gameOverMenu.setPrefWidth(width);
            gameOverMenu.setPrefHeight(height);
        }

        // Resume the game
        if (!isPaused) {
            gameLoop.play();
        }

        // Request focus back to the canvas
        canvas.requestFocus();
    }
    private void returnToMainMenu() {
        gameLoop.stop();
        try {
            MainMenu mainMenu = new MainMenu();
            mainMenu.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void restartGame() {
        gameLoop.stop();
        root.getChildren().remove(gameOverMenu);
        initializeGame();
    }
    private void showGameWonScreen() {
        gameLoop.stop();
        root.getChildren().add(gameOverMenu);
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
                List<TerrainObject> tents = terrainManager.getTents();  // Get the list of tents
                while (true) {
                    if (tents.isEmpty()) break;  // Exit if there are no tents

                    // Select a random tent as the spawn point
                    TerrainObject tent = tents.get(random.nextInt(tents.size()));
                    double x = tent.x();  // Tent x-coordinate
                    double y = tent.y();  // Tent y-coordinate

                    // Spawn an enemy at the tent location
                    enemies.add(new Enemy(this.player, x, y, terrainManager));

                    // Wait before spawning the next enemy
                    Thread.sleep(2000);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        spawner.start();
    }
    private void update(GraphicsContext graphicsContext) {
        if (isPaused) {
            return;
        }

        // Check for win condition
        if (score >= 200 && !gameWon) {
            gameWon = true;
            handleGameOver();
            return;
        }
        // Check for death condition
        if (player.isDead() && player.isDeathAnimationComplete()) {
            gameWon = false;
            handleGameOver();
            return;
        }

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

        // Display SCORE - Now using relative positioning
        double scoreX = AppConfig.getWidth() * 0.02; // 2% from left edge
        double scoreY = AppConfig.getHeight() * 0.08; // 5% from top
        double fontSize = AppConfig.getWidth() * 0.025; // Dynamic font size

        graphicsContext.setFont(Font.loadFont(Objects.requireNonNull(
                MainMenu.class.getResource("/joystix-monospace.otf")).toExternalForm(), fontSize));
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText("Score: " + score, scoreX, scoreY);
    }
}
