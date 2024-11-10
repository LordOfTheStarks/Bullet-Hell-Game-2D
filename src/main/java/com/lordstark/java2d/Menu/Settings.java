package com.lordstark.java2d.Menu;

import com.lordstark.java2d.AppConfig;
import com.lordstark.java2d.Game.Game;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Settings {
    private final Stage primaryStage;
    private Stage settingsStage;
    private final MainMenu mainMenu;
    private final Game game;
    private final boolean isFromGame;

    // Constructor for MainMenu
    public Settings(Stage primaryStage, MainMenu mainMenu) {
        this.primaryStage = primaryStage;
        this.mainMenu = mainMenu;
        this.game = null;
        this.isFromGame = false;
    }

    // Constructor for Game
    public Settings(Stage primaryStage, Game game) {
        this.primaryStage = primaryStage;
        this.game = game;
        this.mainMenu = null;
        this.isFromGame = true;
    }

    public void show() {
        settingsStage = new Stage(StageStyle.UNDECORATED);
        VBox settingsRoot = new VBox(20);
        settingsRoot.setAlignment(Pos.CENTER);
        settingsRoot.setPadding(new javafx.geometry.Insets(30));

        settingsRoot.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.9);" +
                        "-fx-border-color: #4CAF50;" +
                        "-fx-border-width: 2px;"
        );

        Label titleLabel = new Label("Settings");
        titleLabel.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-text-fill: white;"
        );
        titleLabel.setEffect(new DropShadow(10, Color.GREEN));

        ComboBox<String> resolutionDropdown = new ComboBox<>();
        resolutionDropdown.getItems().addAll(
                "800x600",
                "1024x768",
                "1280x720",
                "1366x768",
                "1600x900",
                "1920x1080"
        );

        String currentResolution = AppConfig.getWidth() + "x" + AppConfig.getHeight();
        resolutionDropdown.setValue(currentResolution);

        resolutionDropdown.setStyle(
                "-fx-background-color: #4a4a4a;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-mark-color: #4CAF50;"
        );

        Button saveButton = createStyledButton("Save");
        Button cancelButton = createStyledButton("Cancel");

        saveButton.setOnAction(e -> {
            try {
                String selectedResolution = resolutionDropdown.getValue();
                String[] dimensions = selectedResolution.split("x");
                int width = Integer.parseInt(dimensions[0]);
                int height = Integer.parseInt(dimensions[1]);

                AppConfig.setWidth(width);
                AppConfig.setHeight(height);

                Platform.runLater(() -> {
                    // Update stage size and position
                    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                    primaryStage.setWidth(width);
                    primaryStage.setHeight(height);
                    primaryStage.setX((screenBounds.getWidth() - width) / 2);
                    primaryStage.setY((screenBounds.getHeight() - height) / 2);

                    if (isFromGame) {
                        game.resizeGame(width, height);
                    } else {
                        mainMenu.recreateContent();
                    }
                });

                settingsStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> settingsStage.close());

        settingsRoot.getChildren().addAll(
                titleLabel,
                new Label("Resolution:") {{
                    setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                }},
                resolutionDropdown,
                saveButton,
                cancelButton
        );

        Scene settingsScene = new Scene(settingsRoot);
        settingsStage.setScene(settingsScene);

        settingsStage.setX(primaryStage.getX() + (primaryStage.getWidth() - 400) / 2);
        settingsStage.setY(primaryStage.getY() + (primaryStage.getHeight() - 300) / 2);

        settingsStage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10 20;" +
                        "-fx-cursor: hand"
        );

        button.setOnMouseEntered(e ->
                                         button.setStyle(button.getStyle() + ";-fx-background-color: #45a049;")
        );

        button.setOnMouseExited(e ->
                                        button.setStyle(button.getStyle().replace(";-fx-background-color: #45a049", ""))
        );

        return button;
    }
}