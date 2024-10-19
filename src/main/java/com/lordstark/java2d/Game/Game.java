package com.lordstark.java2d.Game;

import java.util.HashMap;
import java.util.Map;

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

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;
    private static final double SPEED = 4;
    private Player player;
    private Map<KeyCode, Boolean> keys = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) {
        stage.setTitle("Simple shooter game");

        StackPane pane = new StackPane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        canvas.setFocusTraversable(true);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        pane.getChildren().add(canvas);

        this.player = new Player(50,50);

        Timeline loop = new Timeline(new KeyFrame(Duration.millis(1000.0/60),
                                                  e -> update(graphicsContext)));
        loop.setCycleCount(Animation.INDEFINITE);
        loop.play();

        canvas.setOnKeyPressed(e -> this.keys.put(e.getCode(), true));
        canvas.setOnKeyReleased(e -> this.keys.put(e.getCode(), false));

        Scene scene = new Scene(pane, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
    private void update(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(0, 0, WIDTH, HEIGHT);
        graphicsContext.setFill(Color.LIME);
        graphicsContext.fillRect(0, 0, WIDTH, HEIGHT);
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
    }
}
