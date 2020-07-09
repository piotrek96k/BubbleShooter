package com.project.model.mode;

public enum GameMode {

	TIME_MODE(DifficultyLevel.EASY, "Czasowy") {

		@Override
		public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
			this.difficultyLevel = difficultyLevel;
		}

	},

	SURVIVAL_MODE(DifficultyLevel.EASY, "Przetrwanie") {
		@Override
		public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
		}
	};

	protected DifficultyLevel difficultyLevel;

	private String name;

	private GameMode(DifficultyLevel difficultyLevel, String name) {
		this.name = name;
		this.difficultyLevel = difficultyLevel;
	}

	public abstract void setDifficultyLevel(DifficultyLevel difficultyLevel);

	public DifficultyLevel getDifficultyLevel() {
		return difficultyLevel;
	}

	@Override
	public String toString() {
		return name;
	}

}
