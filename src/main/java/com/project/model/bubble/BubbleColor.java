package com.project.model.bubble;

import javafx.scene.paint.Color;

public enum BubbleColor {

	YELLOW(Color.YELLOW), RED(Color.RED), PURPLE(Color.PURPLE), GREEN(Color.LIME.darker()), BLUE(Color.ROYALBLUE),
	TURQUOISE(Color.TURQUOISE), ORANGE(Color.ORANGE), GRAY(Color.GRAY), DARKGREEN(Color.GREEN.darker());

	private Color color;

	private BubbleColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}

}