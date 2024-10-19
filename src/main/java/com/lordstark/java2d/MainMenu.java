package com.lordstark.java2d;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class MainMenu extends Application {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("Play", this::startGame),
            new Pair<String, Runnable>("Settings", this::openSettings),
            new Pair<String, Runnable>("Quit", this::quitGame)
    );

    private Pane root = new Pane();
    private VBox menuBox = new VBox(-5);
    private Line line;

    private Parent createContent() {
        addBackground();
        addTitle();

        double linex = WIDTH / 2 - 100;
        double liney = HEIGHT / 3 + 50;

        addLine(linex, liney);
        addMenu(linex + 5, liney + 5);

        startAnimation();
        return root;
    }

    private void addBackground() {
        ImageView imageView = new ImageView(new Image(getClass().getResource("/prengu.png").toExternalForm()));
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);

        root.getChildren().add(imageView);
    }
    private void addTitle() {
        MenuTitle title = new MenuTitle("Bullet Hell");
        title.setTranslateX(WIDTH / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(HEIGHT / 3);

        root.getChildren().add(title);
    }
    private void addLine(double x, double y) {
        line = new Line(x, y, x, y + 120);
        line.setStrokeWidth(3);
        line.setStroke(Color.color(1, 1, 1, 0.75));
        line.setEffect(new DropShadow(5, Color.BLACK));
        line.setScaleY(0);

        root.getChildren().add(line);
    }
    private void startAnimation() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), line);
        st.setToY(1);
        st.setOnFinished(e -> {

            for (int i = 0; i < menuBox.getChildren().size(); i++) {
                Node n = menuBox.getChildren().get(i);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
                tt.setToX(0);
                tt.setOnFinished(e2 -> n.setClip(null));
                tt.play();
            }
        });
        st.play();
    }
    private void addMenu(double x, double y) {
        menuBox.setTranslateX(x);
        menuBox.setTranslateY(y);
        menuData.forEach(data -> {
            MenuItem item = new MenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);
        });

        root.getChildren().add(menuBox);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Bullet Hell Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    private void startGame() {
        // This method will switch to the game scene
        Stage stage = (Stage) root.getScene().getWindow();
        Game game = new Game();
        try {
            game.start(stage); // Launch the game
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void openSettings() {
        Stage settingsStage = new Stage();
        VBox settingsRoot = new VBox(10);
        settingsRoot.setAlignment(Pos.CENTER);

        ComboBox<String> resolutionDropdown = new ComboBox<>();
        resolutionDropdown.getItems().addAll("1280x720", "1920x1080");
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> settingsStage.close());

        settingsRoot.getChildren().addAll(resolutionDropdown, saveButton);

        Scene settingsScene = new Scene(settingsRoot, 300, 200);
        settingsStage.setTitle("Settings");
        settingsStage.setScene(settingsScene);
        settingsStage.show();
    }
    private void quitGame() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

}
