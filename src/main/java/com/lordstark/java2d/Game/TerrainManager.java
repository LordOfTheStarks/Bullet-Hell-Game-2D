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

    private final List<TreeObject> trees; // New list for trees
    private final Image treeImage;
    private final Image treeShadowImage;

    private static final int TILE_SIZE = 30;
    private static final int RADIUS = 10; // Radius around the player for tile generation
    private static final int ROCK_PROBABILITY = 10; // 10% chance of a rock
    private static final int GRASS_PROBABILITY = 30; // 30% chance of grass
    private static final int TREE_PROBABILITY = 15; // 15% chance of tree generation
    private static final int MIN_TREE_SPACING = 100; // Minimum distance between trees

    public TerrainManager() {
        this.terrainMap = new HashMap<>();
        this.grassImages = loadImages("Grass", 6);
        this.rockImages = loadImages("Rocks", 6);
        this.staticHouses = initializeStaticHouses(); // Initialize static houses

        this.trees = new ArrayList<>();
        this.treeImage = new Image("file:src/main/resources/Trees/Tree1.png");
        this.treeShadowImage = new Image("file:src/main/resources/Shadows/6.png");
        loadMainTile();
        initializeTrees();
    }

    private List<Image> loadImages(String type, int count) {
        List<Image> images = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            images.add(new Image("file:src/main/resources/" + type + "/" + i + ".png"));
        }
        return images;
    }
    // Inner class to handle tree and shadow pairing
    private class TreeObject {
        private final double x, y;
        private final Image treeImage;
        private final Image shadowImage;

        private final double treeWidth;
        private final double treeHeight;
        private final double shadowWidth;
        private final double shadowHeight;

        public TreeObject(double x, double y, Image treeImage, Image shadowImage) {
            this.x = x;
            this.y = y;
            this.treeImage = treeImage;
            this.shadowImage = shadowImage;
            this.treeWidth = treeImage.getWidth();
            this.treeHeight = treeImage.getHeight();
            this.shadowWidth = shadowImage.getWidth();
            this.shadowHeight = shadowImage.getHeight();
        }

        public double getX() { return x; }
        public double getY() { return y; }
        public Image getTreeImage() { return treeImage; }
        public Image getShadowImage() { return shadowImage; }

        // Method to get the base collision rectangle
        public double[] getCollisionBounds() {
            // Collision box at the base of the tree, about 1/3 of the tree's width
            double collisionWidth = treeWidth * 0.3;
            double collisionHeight = treeHeight * 1.2; // Small height for base collision
            double collisionX = x + (treeWidth - collisionWidth) / 2;
            double collisionY = y + treeHeight - collisionHeight;

            return new double[]{collisionX, collisionY, collisionWidth, collisionHeight};
        }
    }
    private void initializeTrees() {
        Random random = new Random();
        int numberOfTrees = 20; // Adjust this number based on your map size

        for (int i = 0; i < numberOfTrees; i++) {
            double x = random.nextDouble() * AppConfig.getWidth() * 2 - AppConfig.getWidth();
            double y = random.nextDouble() * AppConfig.getHeight() * 2 - AppConfig.getHeight();

            // Check if the position is valid (not too close to other objects)
            if (isValidTreePosition(x, y)) {
                trees.add(new TreeObject(x, y, treeImage, treeShadowImage));
            }
        }
    }

    private boolean isValidTreePosition(double x, double y) {
        // Check distance from houses
        for (TerrainObject house : staticHouses) {
            double distance = Math.sqrt(Math.pow(x - house.getX(), 2) + Math.pow(y - house.getY(), 2));
            if (distance < MIN_TREE_SPACING) return false;
        }

        // Check distance from other trees
        for (TreeObject tree : trees) {
            double distance = Math.sqrt(Math.pow(x - tree.getX(), 2) + Math.pow(y - tree.getY(), 2));
            if (distance < MIN_TREE_SPACING) return false;
        }

        return true;
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
        // First render all shadows
        for (TreeObject tree : trees) {
            // Calculate shadow position to be at the base of the tree
            double shadowX = tree.getX() + (tree.treeWidth - tree.shadowWidth) / 2;
            double shadowY = tree.getY() + tree.treeHeight - tree.shadowHeight / 2;

            graphicsContext.drawImage(
                    tree.getShadowImage(),
                    shadowX - camera.getOffsetX(),
                    shadowY - camera.getOffsetY()
            );
        }

        // Then render all trees
        for (TreeObject tree : trees) {
            graphicsContext.drawImage(
                    tree.getTreeImage(),
                    tree.getX() - camera.getOffsetX(),
                    tree.getY() - camera.getOffsetY()
            );

            // Uncomment for debugging collision boxes
            /*
            double[] bounds = tree.getCollisionBounds();
            graphicsContext.setStroke(javafx.scene.paint.Color.RED);
            graphicsContext.strokeRect(
                bounds[0] - camera.getOffsetX(),
                bounds[1] - camera.getOffsetY(),
                bounds[2],
                bounds[3]
            );
            */
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

            if (x + width > houseX &&
                    x < houseX + houseWidth &&
                    y + height > houseY &&
                    y < houseY + houseHeight) {
                return true;
            }
        }
        // Check tree collisions with improved collision boxes
        for (TreeObject tree : trees) {
            double[] bounds = tree.getCollisionBounds();
            double treeX = bounds[0];
            double treeY = bounds[1];
            double treeWidth = bounds[2];
            double treeHeight = bounds[3];

            if (x + width > treeX &&
                    x < treeX + treeWidth &&
                    y + height > treeY &&
                    y < treeY + treeHeight) {
                return true;
            }
        }
        return false;
    }

    // Add these helper methods to get actual sprite dimensions
    public static double getActualSpriteWidth(double width) {
        return width * 0.3; // Adjust this multiplier to fine-tune sprite collision width
    }

    public static double getActualSpriteHeight(double height) {
        return height * 0.3; // Adjust this multiplier to fine-tune sprite collision height
    }
}
