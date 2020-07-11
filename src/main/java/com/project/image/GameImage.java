package com.project.image;

import java.net.URL;
import java.util.Random;

import javafx.scene.image.Image;

public enum GameImage {

	BOMB("/image/bomb.png"),

	GHOST("/image/ghost.png"),

	TWO_COLORED_CIRCLE("/image/twoColoredCircle.png"),

	THREE_COLORED_CIRCLE("/image/threeColoredCircle.png"),

	OPTIONS_MENU_BACKGROUND("/image/optionsMenuWallpaper.png");

	private static final int NUMBER_OF_WALLPAPERS;
	
	static {
		NUMBER_OF_WALLPAPERS = 3;
	}

	private String path;

	private Image image;

	private GameImage(String path) {
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
		StringBuilder builder = new StringBuilder();
		builder.append("-fx-background-image: url(");
		builder.append(url);
		builder.append(");");
		builder.append("-fx-background-repeat: no-repeat;");
		builder.append("-fx-background-size: 100% 100%;");
		return builder.toString();
	}

}
