package com.lordstark.java2d.Menu;

import com.lordstark.java2d.AppConfig;
import com.lordstark.java2d.Game.Game;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
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

        double linex = AppConfig.getWidth() / 2 - 100;
        double liney = AppConfig.getWidth() / 3 + 50;

        addMenu(linex + 5, liney + 5);

        startAnimation();

        return root;
    }

    private void addBackground() {
        ImageView imageView = new ImageView(
                new Image(getClass().getResource("/5d9ce6c826eac1ebec3d8b65536d3ae1.jpg").toExternalForm()));
        imageView.setFitWidth(AppConfig.getWidth());
        imageView.setFitHeight(AppConfig.getHeight());

        root.getChildren().add(imageView);
    }
    private void addTitle() {
        MenuTitle title = new MenuTitle("Shooter Game");
        title.setTranslateX(AppConfig.getWidth() / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(AppConfig.getHeight() / 3);

        root.getChildren().add(title);
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
        menuBox.setTranslateX(AppConfig.getWidth() / 2 - 100);
        menuBox.setTranslateY(AppConfig.getHeight() / 2);
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
        primaryStage.setTitle("Shooter Game Menu");
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

        settingsRoot.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        ComboBox<String> resolutionDropdown = new ComboBox<>();
        resolutionDropdown.getItems().addAll(
                                "800x600",
                                    "1024x768",
                                    "1280x720",
                                    "1366x768",
                                    "1600x900",
                                    "1920x1080"
        );
        resolutionDropdown.setStyle("-fx-background-color: #000; -fx-text-fill: #fff; -fx-font-size: 14px;");

        String currentResolution = AppConfig.getWidth() + "x" + AppConfig.getHeight();
        resolutionDropdown.setValue(currentResolution);

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #000; -fx-text-fill: #fff; -fx-font-size: 14px;");

        saveButton.setOnAction(e -> {
            String selectedResolution = resolutionDropdown.getValue();
            String[] dimensions = selectedResolution.split("x");
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);

            AppConfig.setWidth(width);
            AppConfig.setHeight(height);

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setWidth(width);
            stage.setHeight(height);

            root.getChildren().clear();
            menuBox.getChildren().clear();

            root.getChildren().add(createContent());

            settingsStage.close();
        });

        saveButton.setEffect(new DropShadow(10, Color.BLACK));
        resolutionDropdown.setEffect(new DropShadow(10, Color.BLACK));

        settingsRoot.getChildren().addAll(resolutionDropdown, saveButton);

        Scene settingsScene = new Scene(settingsRoot, 400, 250);
        settingsStage.setTitle("Settings");
        settingsStage.setScene(settingsScene);
        settingsStage.show();
    }
    private void quitGame() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

}
