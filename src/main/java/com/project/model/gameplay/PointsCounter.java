package com.project.model.gameplay;

import com.project.model.bubble.Bubble;
import com.project.model.bubble.ColoredBubble;

public class PointsCounter {

	private Gameplay gameplay;

	private long points;

	private int combo;

	public PointsCounter(Gameplay gameplay) {
		combo = 1;
		this.gameplay = gameplay;
	}

	public void addPointsForBubble(Bubble bubble) {
		points += combo * getPointsForBubble(bubble);
		gameplay.sendPointsNotofications();
	}

	private int getPointsForBubble(Bubble bubble) {
		if (bubble instanceof ColoredBubble)
			return 10 * ((ColoredBubble) bubble).getColorsQuantity();
		else
			return 25;
	}

	public void increaseCombo() {
		combo++;
		gameplay.sendComboNotifications();
	}

	public void resetCombo() {
		combo = 1;
		gameplay.sendComboNotifications();
	}

	public String getCombo() {
		return "x" + combo;
	}

	public long getPoints() {
		return points;
	}

}
