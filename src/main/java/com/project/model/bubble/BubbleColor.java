package com.project.model.bubble;

import javafx.scene.paint.Color;

public enum BubbleColor {

	YELLOW(Color.YELLOW), RED(Color.RED), PURPLE(Color.MEDIUMORCHID), GREEN(Color.LIME), BLUE(Color.ROYALBLUE),
	TURQUOISE(Color.TURQUOISE);

	private Color color;

	private BubbleColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}

}