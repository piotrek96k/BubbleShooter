package com.project.model.bubble;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

public enum BubbleColor {

	YELLOW(Color.YELLOW), RED(Color.RED), PURPLE(Color.MEDIUMORCHID), GREEN(Color.LIME), BLUE(Color.ROYALBLUE),
	TURQUOISE(Color.TURQUOISE);

	private Color color;

	private BubbleColor(Color color) {
		this.color = color;
	}

	public Paint getPaint() {
		Color firstColor = color.brighter();
		Color secondColor = color.darker();
		Stop[] stops = { new Stop(0.35, firstColor), new Stop(0.8, secondColor) };
		return new LinearGradient(0.0, 0.0, 1.0, 1.0, true, CycleMethod.NO_CYCLE, stops);
	}

}