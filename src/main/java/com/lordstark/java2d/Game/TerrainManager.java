package com.lordstark.java2d.Game;

import com.lordstark.java2d.AppConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TerrainManager {
    private List<TerrainObject> terrainObjects;
    private List<Image> grassImages;
    private List<Image> rockImages;

    private static final int TILE_SIZE = 50;
    private static final int ROCK_PROBABILITY = 10; // 10% chance of a rock
    private static final int GRASS_PROBABILITY = 80; // 50% chance of grass

    public TerrainManager() {
        this.terrainObjects = new ArrayList<>();
        this.grassImages = loadImages("Grass", 6); // Adjust count to available images
        this.rockImages = loadImages("Rocks", 6);
        generateTerrain();
    }

    private List<Image> loadImages(String type, int count) {
        List<Image> images = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            images.add(new Image("file:src/main/resources/" + type + "/" + i + ".png"));
        }
        return images;
    }

    private void generateTerrain() {
        Random rand = new Random();
        for (int x = 0; x < AppConfig.getWidth(); x += TILE_SIZE) {
            for (int y = 0; y < AppConfig.getHeight(); y += TILE_SIZE) {
                if (rand.nextInt(100) < GRASS_PROBABILITY) {
                    terrainObjects.add(new TerrainObject(x, y, randomImage(grassImages)));
                }
                if (rand.nextInt(100) < ROCK_PROBABILITY && !isOverlapping(x, y)) {
                    terrainObjects.add(new TerrainObject(x, y, randomImage(rockImages)));
                }
            }
        }
    }

    private Image randomImage(List<Image> images) {
        return images.get(new Random().nextInt(images.size()));
    }

    private boolean isOverlapping(double x, double y) {
        for (TerrainObject obj : terrainObjects) {
            if (Math.abs(obj.getX() - x) < TILE_SIZE && Math.abs(obj.getY() - y) < TILE_SIZE) {
                return true;
            }
        }
        return false;
    }

    public void render(GraphicsContext graphicsContext, Camera camera) {
        for (TerrainObject obj : terrainObjects) {
            graphicsContext.drawImage(obj.getImage(),
                                      obj.getX() - camera.getOffsetX(), obj.getY() - camera.getOffsetY());
        }
    }
}
