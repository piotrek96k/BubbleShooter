package com.project.view.circle;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ThreeColouredCircle extends TwoColouredCircle {

	protected Paint thirdPaint;

	protected Circle thirdCirclePart;

	public ThreeColouredCircle(double centerX, double centerY, double radius, Paint firstPaint, Paint secondPaint,
			Paint thirdPaint) {
		super(centerX, centerY, radius, firstPaint, secondPaint);
		this.thirdPaint = thirdPaint;
	}

	@Override
	public void init() {
		setFirstCirclePart();
		setSecondCirclePart();
		setThirdCirclePart();
	}

	private Shape getClipper() {
		Shape result;
		double coefficinet = Math.pow(2, 0.5) / 2;
		Circle circle = new Circle(CENTER_X, CENTER_Y, radius);
		double smallRadius = radius / (1.0 + 2.0 / 3.0 * Math.pow(3.0, 0.5));
		double height = Math.pow(3, 0.5) * smallRadius;
		Circle smallCircle = new Circle(CENTER_X, CENTER_Y - height * 2 / 3, smallRadius);
		result = Shape.subtract(circle, smallCircle);
		result.setRotate(-120.0);
		result = Shape.subtract(result, smallCircle);
		Rectangle rect = new Rectangle(CENTER_X - radius, CENTER_Y - radius, radius * 2, radius * 2);
		rect.setRotate(45.0);
		rect.setTranslateX(radius * coefficinet);
		rect.setTranslateY(radius * coefficinet);
		result = Shape.subtract(result, rect);
		rect = new Rectangle(CENTER_X, CENTER_Y - radius, radius, radius * 2);
		result = Shape.subtract(result, rect);
		result = Shape.union(result, smallCircle);
		return result;
	}

	private void setFirstCirclePart() {
		firstCirclePart = new Circle(CENTER_X, CENTER_Y, radius, firstPaint);
		firstCirclePart.setClip(getClipper());
	}

	private void setSecondCirclePart() {
		secondCirclePart = new Circle(CENTER_X, CENTER_Y, radius, secondPaint);
		secondCirclePart.setClip(getClipper());
		secondCirclePart.setRotate(-120.0);
	}

	private void setThirdCirclePart() {
		thirdCirclePart = new Circle(CENTER_X, CENTER_Y, radius, thirdPaint);
		thirdCirclePart.setClip(getClipper());
		thirdCirclePart.setRotate(-240.0);
	}

	@Override
	public void update(double centerX, double centerY, Paint[] paints) {
		setFirstPaint(paints[0]);
		setSecondPaint(paints[1]);
		setThirdPaint(paints[2]);
		firstCirclePart.setTranslateX(centerX - CENTER_X);
		firstCirclePart.setTranslateY(centerY - CENTER_Y);
		secondCirclePart.setTranslateX(centerX - CENTER_X);
		secondCirclePart.setTranslateY(centerY - CENTER_Y);
		thirdCirclePart.setTranslateX(centerX - CENTER_X);
		thirdCirclePart.setTranslateY(centerY - CENTER_Y);
	}

	protected void setThirdPaint(Paint thirPaint) {
		if (thirdPaint != null) {
			this.thirdPaint = thirPaint;
			thirdCirclePart.setFill(thirdPaint);
		}
	}

	public Circle getThirdCirclePart() {
		return thirdCirclePart;
	}

}