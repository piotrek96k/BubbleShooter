package com.piotrek.image;

import java.net.URL;
import java.util.Random;

import javafx.scene.image.Image;

public enum GameImage {

    BOMB("/image/bomb.png"),

    GHOST("/image/ghost.png"),

    ICON("/image/icon.png"),

    OPTIONS_MENU_BACKGROUND("/image/optionsMenuWallpaper.png"),

    REPLACE("/image/replace.png"),

    TWO_COLORED_CIRCLE("/image/twoColoredCircle.png"),

    THREE_COLORED_CIRCLE("/image/threeColoredCircle.png");

    private static final int NUMBER_OF_WALLPAPERS;

    static {
        NUMBER_OF_WALLPAPERS = 3;
    }

    private final String path;

    private final Image image;

    GameImage(String path) {
        this.path = path;
        image = new Image(path);
    }

    public Image getImage() {
        return image;
    }

    public String getPath() {
        return path;
    }

    public static String getRandomWallpaperStyle() {
        Random random = new Random();
        URL url = GameImage.class.getResource("/image/wallpaper" + random.nextInt(NUMBER_OF_WALLPAPERS) + ".png");
        return getStyle(url);
    }

    public static String getWallpaperStyle(GameImage image) {
        URL url = GameImage.class.getResource(image.getPath());
        return getStyle(url);
    }

    private static String getStyle(URL url) {
        return "-fx-background-image: url(" +
                url +
                ");" +
                "-fx-background-repeat: no-repeat;" +
                "-fx-background-size: 100% 100%;";
    }

}
