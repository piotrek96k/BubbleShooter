package com.project.model.gameplay;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import com.project.model.bubble.Bubble;
import com.project.model.bubble.ColoredBubble;
import com.project.model.mode.DifficultyLevel;

public class PointsCounter {

    private final Gameplay gameplay;

    private long points;

    private int combo;

    public PointsCounter(Gameplay gameplay) {
        this.gameplay = gameplay;
        resetCombo();
    }

    public void addPointsForBubble(Bubble bubble) {
        points += (long) combo * getPointsForBubble(bubble);
        gameplay.sendPointsNotifications();
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
        combo = getMinimumCombo();
        gameplay.sendComboNotifications();
    }

    public static String getFormattedCombo(int combo) {
        return "x " + combo;
    }

    public static String getFormattedPoints(long points) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(points);
    }

    public int getMinimumCombo() {
        return 1 + gameplay.getBubblesTab().getNumberOfColors() - DifficultyLevel.EASY.getNumberOfColors();
    }

    public int getCombo() {
        return combo;
    }

    public long getPoints() {
        return points;
    }

}
