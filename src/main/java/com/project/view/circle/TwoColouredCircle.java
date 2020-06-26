package com.project.view.circle;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class TwoColouredCircle {

	protected final double CENTER_X;

	protected final  double CENTER_Y;

	protected double radius;

	protected Paint firstPaint;

	protected Paint secondPaint;

	protected Circle firstCirclePart;

	protected Circle secondCirclePart;

	protected Shape shape;

	public TwoColouredCircle(double centerX, double centerY, double radius, Paint firstPaint, Paint secondPaint) {
		CENTER_X = centerX;
		CENTER_Y = centerY;
		this.radius = radius;
		this.firstPaint = firstPaint;
		this.secondPaint = secondPaint;
	}

	public void init() {
		setFirstCirclePart();
		setSecondCirclePart();
	}

	private Shape getClipper() {
		Shape result;
		double coefficinet = Math.pow(2, 0.5) / 2;
		Rectangle rect = new Rectangle(CENTER_X - radius, CENTER_Y - radius, radius * 2, radius * 2);
		rect.setRotate(45.0);
		rect.setTranslateX(radius * coefficinet);
		rect.setTranslateY(radius * coefficinet);
		Circle circle = new Circle(CENTER_X, CENTER_Y, radius);
		result = Shape.subtract(circle, rect);
		Circle smallCircle = new Circle(CENTER_X, CENTER_Y, radius / 2);
		smallCircle.setRotate(45.0);
		smallCircle.setTranslateX(radius / 2 * coefficinet);
		smallCircle.setTranslateY(-radius / 2 * coefficinet);
		result = Shape.subtract(result, smallCircle);
		smallCircle.setTranslateX(-radius / 2 * coefficinet);
		smallCircle.setTranslateY(radius / 2 * coefficinet);
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
		secondCirclePart.setRotate(180.0);
	}

	public void update(double centerX, double centerY, Paint[] paints) {
		setFirstPaint(paints[0]);
		setSecondPaint(paints[1]);
		firstCirclePart.setTranslateX(centerX - CENTER_X);
		firstCirclePart.setTranslateY(centerY - CENTER_Y);
		secondCirclePart.setTranslateX(centerX - CENTER_X);
		secondCirclePart.setTranslateY(centerY - CENTER_Y);
	}

	protected void setFirstPaint(Paint firstPaint) {
		if (firstPaint != null) {
			this.firstPaint = firstPaint;
			firstCirclePart.setFill(firstPaint);
		}
	}

	protected void setSecondPaint(Paint secondPaint) {
		if (secondPaint != null) {
			this.secondPaint = secondPaint;
			secondCirclePart.setFill(secondPaint);
		}
	}

	public Circle getFirstCirclePart() {
		return firstCirclePart;
	}

	public Circle getSecondCirclePart() {
		return secondCirclePart;
	}

}
