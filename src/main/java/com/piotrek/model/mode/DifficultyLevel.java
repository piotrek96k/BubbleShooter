package com.piotrek.model.mode;

import com.piotrek.resources.Resources;

public enum DifficultyLevel {

    EASY(6, Resources.RESOURCE_BUNDLE.getString("DifficultyLevel.EASY")),

    MEDIUM(7, Resources.RESOURCE_BUNDLE.getString("DifficultyLevel.MEDIUM")),

    HARD(8, Resources.RESOURCE_BUNDLE.getString("DifficultyLevel.HARD")),

    EXTREME(9, Resources.RESOURCE_BUNDLE.getString("DifficultyLevel.EXTREME")),

    HARDCORE(10, Resources.RESOURCE_BUNDLE.getString("DifficultyLevel.HARDCORE"));

    private final int numberOfColors;

    private final String name;

    DifficultyLevel(int numberOfColors, String name) {
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
