package com.project.model.bubble;

import java.util.Arrays;
import java.util.List;

public class ColoredBubble extends Bubble {

    protected List<BubbleColor> colors;

    public ColoredBubble(double centerX, double centerY, BubbleColor[] colors) {
        super(centerX, centerY);
        this.colors = Arrays.asList(colors);
    }

    public List<BubbleColor> getColors() {
        return colors;
    }

    public int getColorsQuantity() {
        return colors.size();
    }


}
