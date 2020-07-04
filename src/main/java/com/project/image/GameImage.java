package com.project.image;

import javafx.scene.image.Image;

public enum GameImage {

	BOMB("/image/bomb.png"),

	GHOST("/image/ghost.png"),

	TWO_COLORED_CIRCLE("/image/twoColoredCircle.png"),

	THREE_COLORED_CIRCLE("/image/threeColoredCircle.png");

	private Image image;

	private GameImage(String path) {
		image = new Image(path);
	}

	public Image getImage() {
		return image;
	}

}
