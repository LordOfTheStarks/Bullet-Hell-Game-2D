package com.lordstark.java2d.Menu;

import com.lordstark.java2d.AppConfig;
import com.lordstark.java2d.Game.Game;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class MainMenu extends Application {

    private final List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("Play", this::startGame),
            new Pair<String, Runnable>("Settings", this::openSettings),
            new Pair<String, Runnable>("Quit", this::quitGame)
    );

    private final Pane root = new Pane();
    private final VBox menuBox = new VBox(-5);
    private MenuTitle title;
    private boolean animationStarted = false;

    private Parent createContent() {
        addBackground();
        addTitle();
        addMenu();
        if (!animationStarted) {
            startAnimation();
            animationStarted = true;
        }
        return root;
    }

    private void addBackground() {
        ImageView imageView = new ImageView(
                new Image(getClass().getResource("/MainMenu.jpeg").toExternalForm()));
        imageView.setFitWidth(AppConfig.getWidth());
        imageView.setFitHeight(AppConfig.getHeight());
        root.getChildren().add(imageView);
    }

    private void addTitle() {
        title = new MenuTitle("Shooter Game");
        updateTitlePosition();
        root.getChildren().add(title);
    }

    private void updateTitlePosition() {
        title.setTranslateX(AppConfig.getWidth() / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(AppConfig.getHeight() / 3);
    }

    private void addMenu() {
        menuBox.getChildren().clear();
        updateMenuPosition();

        menuData.forEach(data -> {
            MenuItem item = new MenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(0);
            menuBox.getChildren().addAll(item);
        });

        if (!root.getChildren().contains(menuBox)) {
            root.getChildren().add(menuBox);
        }
    }

    private void updateMenuPosition() {
        menuBox.setTranslateX(AppConfig.getWidth() / 2 - 100);
        menuBox.setTranslateY(AppConfig.getHeight() / 2);
    }

    private void startAnimation() {
        for (int i = 0; i < menuBox.getChildren().size(); i++) {
            Node n = menuBox.getChildren().get(i);

            // Start from left side of screen
            n.setTranslateX(-300);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
            tt.setToX(0);
            tt.play();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Shooter Game Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void recreateContent() {
        root.getChildren().clear();
        addBackground();
        addTitle();
        addMenu();
    }

    private void startGame() {
        Stage stage = (Stage) root.getScene().getWindow();
        Game game = new Game();
        try {
            game.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openSettings() {
        Settings settings = new Settings((Stage) root.getScene().getWindow(), this);
        settings.show();
    }

    private void quitGame() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}