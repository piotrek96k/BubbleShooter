package com.project.model.gameplay;

import com.project.model.bubble.Bubble;

public class Dropper {

	private long startTime;
	
	private double startHeight;
	
	public Dropper(double startHeight) {
		this.startHeight = startHeight;
		startTime = System.currentTimeMillis();
	}
	
	public double getHeight(long time) {
		double height = startHeight-0.5*(Math.pow(getVelocity(time), 2));
		if(height>Bubble.getDiameter()/2)
			return height;
		return Bubble.getDiameter()/2;
	}
	
	private double getVelocity(long time) {
		double velocity = (startTime-time)/20;
		return velocity;
	}
	
	public static double convertHeight(double height, double paneSize) {
		return paneSize-height;
	}
	
}