package com.lordstark.java2d.Game;

import java.util.*;

import com.lordstark.java2d.AppConfig;
import javafx.animation.KeyFrame;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Game extends Application {

    private static final double SPEED = 10;
    private Player player;
    private Map<KeyCode, Boolean> keys = new HashMap<>();
    public static List<Enemy> enemies = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) {
        stage.setTitle("Simple shooter game");

        StackPane pane = new StackPane();

        // Get width and height from AppConfig
        Canvas canvas = new Canvas(AppConfig.getWidth(), AppConfig.getHeight());
        canvas.setFocusTraversable(true);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        pane.getChildren().add(canvas);

        this.player = new Player(50, 50);

        Timeline loop = new Timeline(new KeyFrame(Duration.millis(1000.0/60),
                                                  e -> update(graphicsContext)));
        loop.setCycleCount(Animation.INDEFINITE);
        loop.play();

        spawnEnemies();

        canvas.setOnKeyPressed(e -> this.keys.put(e.getCode(), true));
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
                    this.enemies.add(new Enemy(this.player, x, y));
                    Thread.sleep(2000);
                }
            }catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        spawner.start();
    }
    private void update(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(0, 0, AppConfig.getWidth(), AppConfig.getHeight());
        graphicsContext.setFill(Color.LIME);
        graphicsContext.fillRect(0, 0, AppConfig.getWidth(), AppConfig.getHeight());

        for(int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            e.render(graphicsContext);
            for(int j = 0; j < Player.bullets.size(); j++) {
                if(e.collides(Player.bullets.get(j).getX(), Player.bullets.get(j).getY(),
                              Enemy.WIDTH, Bullet.WIDTH)) {
                    Player.bullets.remove(j);
                    enemies.remove(i);
                    i++;
                    break;
                }
            }
        }
        this.player.render(graphicsContext);

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
    }
}
