package com.project.model.gameplay;

import com.project.model.bubble.Bubble;

public class Dropper {

	private double startHeight;
	
	private int initialDelay;
	
	private long startTime;

	private double counter;

	public Dropper(double startHeight) {
		this(startHeight, 0);
	}
	
	public Dropper(double startHeight, int initialDelay) {
		this.startHeight = startHeight;
		this.initialDelay = initialDelay;
		startTime= System.currentTimeMillis();
	}

	public double getHeight(long time) {
		if(System.currentTimeMillis()<startTime+initialDelay)
			return startHeight;
		double height = startHeight - 0.5 * (Math.pow(counter++ / 2, 2));
		if (height > Bubble.getDiameter() / 2)
			return height;
		return Bubble.getDiameter() / 2;
	}

	public static double convertHeight(double height, double paneSize) {
		return paneSize - height;
	}

}