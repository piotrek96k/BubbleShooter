package com.piotrek.model.mode;

import com.piotrek.resources.Resources;

public enum GameMode {

    ARCADE_MODE(DifficultyLevel.EASY, Resources.RESOURCE_BUNDLE.getString("GameMode.ARCADE_MODE")) {
        @Override
        public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
            this.difficultyLevel = difficultyLevel;
        }

    },

    SURVIVAL_MODE(DifficultyLevel.EASY, Resources.RESOURCE_BUNDLE.getString("GameMode.SURVIVAL_MODE")) {
        @Override
        public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        }
    };

    protected DifficultyLevel difficultyLevel;

    private final String name;

    GameMode(DifficultyLevel difficultyLevel, String name) {
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
