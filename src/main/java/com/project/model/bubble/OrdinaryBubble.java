package com.project.model.bubble;

public class OrdinaryBubble extends Bubble{
	
	protected BubbleColor color;

	public OrdinaryBubble(double centerX, double centerY, BubbleColor color) {
		super(centerX, centerY);
		this.color = color;
	}	
	
	public BubbleColor getColor() {
		return color;
	}
	
	public void setColor(BubbleColor color) {
		this.color = color;
	}
	
}
