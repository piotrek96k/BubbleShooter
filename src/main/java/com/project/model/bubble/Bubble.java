package com.project.model.bubble;

public class Bubble {

	protected static double diameter;
	
	protected static double offset;
	
	protected static int bubblesNumber = 0;

	protected double centerX;
	
	protected double centerY;
	
	protected BubbleColor color;
	
	public final int BUBBLE_NUMBER;

	public Bubble(double centerX, double centerY, BubbleColor color) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.color = color;
		BUBBLE_NUMBER = ++bubblesNumber;
	}
	
	@Override
	public int hashCode() {
		return BUBBLE_NUMBER;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bubble other = (Bubble) obj;
		if (BUBBLE_NUMBER != other.BUBBLE_NUMBER)
			return false;
		return true;
	}
	
	public double getCenterX() {
		return centerX;
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	public BubbleColor getColor() {
		return color;
	}
	
	public void setColor(BubbleColor color) {
		this.color = color;
	}

	public static double getDiameter() {
		return diameter;
	}

	public static void setDiameter(double diameter) {
		Bubble.diameter = diameter;
	}
	
	public static double getOffset() {
		return offset;
	}

	public static void setOffset(double offset) {
		Bubble.offset = offset;
	}
	
	public static double getRadius() {
		return (diameter-offset)/2;
	}

}