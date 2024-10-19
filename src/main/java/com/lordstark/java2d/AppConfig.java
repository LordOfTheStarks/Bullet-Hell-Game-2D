package com.lordstark.java2d;

public class AppConfig {
    private static int width = 1280;
    private static int height = 720;

    public static int getWidth() {
        return width;
    }
    public static void setWidth(int width) {
        AppConfig.width = width;
    }
    public static int getHeight() {
        return height;
    }
    public static void setHeight(int height) {
        AppConfig.height = height;
    }
}
