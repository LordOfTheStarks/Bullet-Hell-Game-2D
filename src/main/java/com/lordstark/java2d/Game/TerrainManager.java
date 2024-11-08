package com.lordstark.java2d.Game;

import com.lordstark.java2d.AppConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.*;

public class TerrainManager {
    private final Map<String, TerrainObject> terrainMap; // Stores tiles with unique keys
    private final List<Image> grassImages;
    private final List<Image> rockImages;
    private Image mainTile; // The main tile image to use as the base layer
    private final List<TerrainObject> staticHouses; // Fixed list of houses

    private static final int TILE_SIZE = 30;
    private static final int RADIUS = 10; // Radius around the player for tile generation
    private static final int ROCK_PROBABILITY = 10; // 10% chance of a rock
    private static final int GRASS_PROBABILITY = 30; // 30% chance of grass

    public TerrainManager() {
        this.terrainMap = new HashMap<>();
        this.grassImages = loadImages("Grass", 6);
        this.rockImages = loadImages("Rocks", 6);
        this.staticHouses = initializeStaticHouses(); // Initialize static houses
        loadMainTile();
    }

    private List<Image> loadImages(String type, int count) {
        List<Image> images = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            images.add(new Image("file:src/main/resources/" + type + "/" + i + ".png"));
        }
        return images;
    }

    private void loadMainTile() {
        mainTile = new Image("file:src/main/resources/tiles/1.png");
    }

    // Create fixed house objects with unique positions and images
    private List<TerrainObject> initializeStaticHouses() {
        List<TerrainObject> houses = new ArrayList<>();

        // Define unique positions and images for each house
        houses.add(new TerrainObject(-180, -180, new Image("file:src/main/resources/Houses/1.png")));
        houses.add(new TerrainObject(180, -180, new Image("file:src/main/resources/Houses/2.png")));
        houses.add(new TerrainObject(-180, 120, new Image("file:src/main/resources/Houses/3.png")));
        houses.add(new TerrainObject(180, 120, new Image("file:src/main/resources/Houses/4.png")));

        // Add more houses here if needed, ensuring they don’t overlap in position
        return houses;
    }

    public void updateTerrain(double playerX, double playerY) {
        int playerTileX = (int) (playerX / TILE_SIZE);
        int playerTileY = (int) (playerY / TILE_SIZE);

        // Generate surrounding terrain tiles (excluding houses)
        for (int x = playerTileX - RADIUS; x <= playerTileX + RADIUS; x++) {
            for (int y = playerTileY - RADIUS; y <= playerTileY + RADIUS; y++) {
                String key = x + "," + y;
                if (!terrainMap.containsKey(key)) {
                    generateTile(x, y, key);
                } else {
                    TerrainObject tile = terrainMap.get(key);
                    if (tile.isEmpty() && Math.abs(playerTileX - x) <= 2 && Math.abs(playerTileY - y) <= 2) {
                        tile.setImage(randomImage(grassImages));
                    }
                }
            }
        }

        // Remove tiles outside the radius to free memory
        terrainMap.keySet().removeIf(key -> {
            String[] coords = key.split(",");
            int tileX = Integer.parseInt(coords[0]);
            int tileY = Integer.parseInt(coords[1]);
            return Math.abs(tileX - playerTileX) > RADIUS || Math.abs(tileY - playerTileY) > RADIUS;
        });
    }

    private void generateTile(int tileX, int tileY, String key) {
        Random random = new Random();
        int x = tileX * TILE_SIZE;
        int y = tileY * TILE_SIZE;

        Image image = mainTile; // Start with the main tile as the base image

        // Randomly decide if this tile should have grass or rock, excluding houses
        if (random.nextInt(100) < GRASS_PROBABILITY) {
            image = randomImage(grassImages);
        } else if (random.nextInt(100) < ROCK_PROBABILITY) {
            image = randomImage(rockImages);
        }

        TerrainObject tile = new TerrainObject(x, y, image);
        terrainMap.put(key, tile);
    }

    private Image randomImage(List<Image> images) {
        return images.get(new Random().nextInt(images.size()));
    }

    public void render(GraphicsContext graphicsContext, Camera camera) {
        double canvasWidth = AppConfig.getWidth();
        double canvasHeight = AppConfig.getHeight();
        double offsetX = camera.getOffsetX() % TILE_SIZE;
        double offsetY = camera.getOffsetY() % TILE_SIZE;

        for (int x = 0; x <= canvasWidth; x += TILE_SIZE) {
            for (int y = 0; y <= canvasHeight; y += TILE_SIZE) {
                graphicsContext.drawImage(mainTile, x - offsetX, y - offsetY);
            }
        }

        // Render dynamically generated terrain objects (grass, rocks, etc.)
        for (TerrainObject obj : terrainMap.values()) {
            if (obj.getImage() != null) {
                graphicsContext.drawImage(
                        obj.getImage(),
                        obj.getX() - camera.getOffsetX(),
                        obj.getY() - camera.getOffsetY()
                );
            }
        }

        // Render static houses with fixed positions
        for (TerrainObject house : staticHouses) {
            graphicsContext.drawImage(
                    house.getImage(),
                    house.getX() - camera.getOffsetX(),
                    house.getY() - camera.getOffsetY()
            );
        }
    }

    public Image getMainTile() {
        return mainTile;
    }
    public boolean collidesWithHouse(double x, double y, double width, double height) {
        // Collision offset to make the boundaries tighter
        double collisionOffset = 10; // Adjust this value to fine-tune the collision boundary

        for (TerrainObject house : staticHouses) {
            // Adjust collision boundaries to be slightly smaller than the actual house image
            double houseX = house.getX() + collisionOffset;
            double houseY = house.getY() + collisionOffset;
            double houseWidth = house.getWidth() - (collisionOffset * 2);
            double houseHeight = house.getHeight() - (collisionOffset * 2);

            // Draw collision boundaries for debugging (uncomment when needed)
        /*
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.strokeRect(
            houseX - camera.getOffsetX(),
            houseY - camera.getOffsetY(),
            houseWidth,
            houseHeight
        );
        */

            if (x + width > houseX &&
                    x < houseX + houseWidth &&
                    y + height > houseY &&
                    y < houseY + houseHeight) {
                return true;
            }
        }
        return false;
    }

    // Add these helper methods to get actual sprite dimensions
    public static double getActualSpriteWidth(double width) {
        return width * 0.8; // Adjust this multiplier to fine-tune sprite collision width
    }

    public static double getActualSpriteHeight(double height) {
        return height * 0.8; // Adjust this multiplier to fine-tune sprite collision height
    }
}
