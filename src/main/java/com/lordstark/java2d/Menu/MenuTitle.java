package com.lordstark.java2d.Menu;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuTitle extends Pane {
    private Text text;

    public MenuTitle(String name) {
        String spread = "";
        for(char c : name.toCharArray()) {
            spread += c;
        }

        text = new Text(spread);
        text.setFont(Font.loadFont(MainMenu.class
                                           .getResource("/joystix-monospace.otf")
                                           .toExternalForm(), 48));
        text.setFill(Color.WHITE);
        text.setEffect(new DropShadow(50, Color.BLACK));

        getChildren().addAll(text);
    }
    public double getTitleWidth() {
        return text.getLayoutBounds().getWidth();
    }
    public double getTitleHeight() {
        return text.getLayoutBounds().getHeight();
    }
}
