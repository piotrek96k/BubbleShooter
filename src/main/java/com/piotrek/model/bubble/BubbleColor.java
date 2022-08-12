package com.piotrek.model.bubble;

import javafx.scene.paint.Color;

public enum BubbleColor {

    YELLOW(Color.YELLOW),

    RED(Color.RED),

    PURPLE(Color.PURPLE.brighter()),

    GREEN(Color.LIME.darker()),

    BLUE(Color.ROYALBLUE),

    TURQUOISE(Color.TURQUOISE),

    ORANGE(Color.ORANGE),

    GRAY(Color.GRAY),

    DARK_GREEN(Color.GREEN.darker()),

    BLUE_VIOLET(Color.BLUEVIOLET),

    WHITE(Color.WHITE),

    PINK(Color.HOTPINK);

    private final Color color;

    BubbleColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

}