package com.piotrek.model.gameplay;

import java.util.HashMap;
import java.util.Map;

import com.piotrek.model.bubble.BubbleColor;

public class ColorsCounter {

    private final Map<BubbleColor, Integer> quantities;

    private int activeBubblesNumber;

    private int colorsSum;

    public ColorsCounter() {
        quantities = new HashMap<>();
        for (BubbleColor value : BubbleColor.values())
            quantities.put(value, 0);
    }

    public void increment(BubbleColor color) {
        int value = quantities.get(color);
        if (value == 0)
            activeBubblesNumber++;
        quantities.put(color, ++value);
        colorsSum++;
    }

    public void decrement(BubbleColor color) {
        int value = quantities.get(color);
        if (value == 1)
            activeBubblesNumber--;
        quantities.put(color, --value);
        colorsSum--;
    }

    public int getQuantity(BubbleColor color) {
        return quantities.get(color);
    }

    public int getActiveBubblesNumber() {
        return activeBubblesNumber;
    }

    public int getColorsSum() {
        return colorsSum;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        quantities.forEach((k, v) -> stringBuilder.append(k).append(" ").append(v).append('\n'));
        stringBuilder.append('\n');
        return stringBuilder.toString();
    }

}