package com.project.model.gameplay;

import com.project.model.bubble.Bubble;

public class Dropper {

    private final double startHeight;

    private final int initialDelay;

    private final long startTime;

    private double counter;

    public Dropper(double startHeight, int initialDelay) {
        this.startHeight = startHeight;
        this.initialDelay = initialDelay;
        startTime = System.currentTimeMillis();
    }

    public double getHeight() {
        if (System.currentTimeMillis() < startTime + initialDelay)
            return startHeight;
        double height = startHeight - 0.5 * (Math.pow(counter++ / 2, 2));
        return Math.max(height, Bubble.DIAMETER / 2);
    }

    public static double convertHeight(double height, double paneSize) {
        return paneSize - height;
    }

}