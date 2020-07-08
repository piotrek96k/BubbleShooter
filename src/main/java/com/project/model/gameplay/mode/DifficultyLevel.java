package com.project.model.gameplay.mode;

public enum DifficultyLevel {

	EASY(6, "£atwy"),

	MEDIUM(7, "Œredni"),

	HARD(8, "Trudny"),

	EXTREME(9, "Ekstremalny"),

	HARDCORE(10, "Hardkorowy");

	private int numberOfColors;

	private String name;

	private DifficultyLevel(int numberOfColors, String name) {
		this.numberOfColors = numberOfColors;
		this.name = name;
	}

	public int getNumberOfColors() {
		return numberOfColors;
	}

	@Override
	public String toString() {
		return name;
	}

}
