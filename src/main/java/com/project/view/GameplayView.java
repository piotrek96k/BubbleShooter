package com.project.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.controller.GameplayController;
import com.project.model.bubble.Bubble;
import com.project.model.bubble.OrdinaryBubble;
import com.project.util.ImageUtil;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class GameplayView {

	private GameplayController controller;

	private Pane pane;

	private List<Line> lines = new ArrayList<>();

	private Map<Integer, Circle> circlesMap = new HashMap<>();

	public GameplayView() {
		pane = new Pane();
	}

	public void setController(GameplayController gameplayController) {
		this.controller = gameplayController;
		setPaneSize();
		pane.setBackground(
				new Background(new BackgroundFill(Color.rgb(24, 24, 24), CornerRadii.EMPTY, new Insets(0.0))));
		addRectangle();
		pane.setOnMouseEntered(this::handleMouseEntering);
		pane.setOnMouseExited(this::handleMouseExiting);
		pane.setOnMouseMoved(this::handleMouseMoving);
		pane.setOnMouseDragged(this::handleMouseMoving);
		pane.setOnMouseClicked(this::handleMouseClicking);
	}

	private void addRectangle() {
		double height = controller.getModel().HEIGHT - controller.getModel().BUBBLES_HEIGHT;
		double startY = pane.getPrefHeight() - height;
		Rectangle rectangle = new Rectangle(0, startY, pane.getPrefWidth(), height);
		rectangle.setFill(Color.rgb(48, 48, 48));
		pane.getChildren().add(0, rectangle);
	}

	private void setPaneSize() {
		pane.setPrefHeight(controller.getModel().HEIGHT);
		pane.setPrefWidth(controller.getModel().WIDTH);
		pane.setMinHeight(Pane.USE_PREF_SIZE);
		pane.setMinWidth(Pane.USE_PREF_SIZE);
	}

	public void addBubble(Bubble bubble) {
		double centerX = bubble.getCenterX();
		double centerY = bubble.getCenterY();
		double radius = Bubble.getRadius();
		Circle circle;
		if (bubble instanceof OrdinaryBubble) {
			Paint paint = ((OrdinaryBubble) bubble).getColor().getPaint();
			circle = new Circle(centerX, centerY, radius, paint);
		} else {
			circle = new Circle(centerX, centerY, radius + 2.5);
			circle.setFill(new ImagePattern(ImageUtil.BOMB_IMAGE));
		}
		circlesMap.put(bubble.BUBBLE_NUMBER, circle);
		pane.getChildren().add(circle);
	}

	public void removeBubble(Bubble bubble) {
		Circle circle = circlesMap.get(bubble.BUBBLE_NUMBER);
		circlesMap.remove(bubble.BUBBLE_NUMBER);
		pane.getChildren().remove(circle);
	}

	public void updateBubble(Bubble bubble) {
		Circle circle = circlesMap.get(bubble.BUBBLE_NUMBER);
		circle.setCenterX(bubble.getCenterX());
		circle.setCenterY(bubble.getCenterY());
		if (bubble instanceof OrdinaryBubble)
			circle.setFill(((OrdinaryBubble) bubble).getColor().getPaint());
	}

	private void setLineStyle(Line line) {
		line.setStroke(Color.WHITE);
		line.setStrokeWidth(3);
		line.setOpacity(0.5);
		line.getStrokeDashArray().addAll(10.0, 15.0);
	}

	public Pane getPane() {
		return pane;
	}

	private void handleMouseEntering(MouseEvent event) {
		pane.getScene().setCursor(Cursor.NONE);
		List<Point2D> points = controller.getLinePoints(event.getX(), event.getY());
		for (int i = 0; i < points.size() - 1; i++)
			addLine(points, i);
	}

	private void handleMouseExiting(MouseEvent event) {
		pane.getScene().setCursor(Cursor.DEFAULT);
		for (Line line : lines)
			pane.getChildren().remove(line);
		lines.clear();
	}

	private void handleMouseMoving(MouseEvent event) {
		List<Point2D> points = controller.getLinePoints(event.getX(), event.getY());
		if (points.isEmpty())
			handleMouseExiting(event);
		else {
			if (!pane.getScene().getCursor().equals(Cursor.NONE))
				pane.getScene().setCursor(Cursor.NONE);
			manageLines(points);
		}
	}

	public void manageLines(List<Point2D> points) {
		int difference = points.size() - 1 - lines.size();
		if (difference >= 0) {
			for (int i = 0; i < lines.size(); i++)
				setline(points, i);
			for (int i = lines.size(); i < points.size() - 1; i++)
				addLine(points, i);
		} else {
			for (int i = 0; i < points.size() - 1; i++)
				setline(points, i);
			if (points.size() > 0)
				for (int i = points.size() - 1; i < lines.size(); i++)
					removeLine(i);
		}
	}

	private void handleMouseClicking(MouseEvent event) {
		controller.throwBubble(event.getX(), event.getY());
	}

	private void removeLine(int i) {
		Line line = lines.get(i);
		pane.getChildren().remove(line);
		lines.remove(i);
	}

	private void setline(List<Point2D> points, int i) {
		lines.get(i).setStartX(points.get(i).getX());
		lines.get(i).setStartY(points.get(i).getY());
		lines.get(i).setEndX(points.get(i + 1).getX());
		lines.get(i).setEndY(points.get(i + 1).getY());
	}

	private void addLine(List<Point2D> points, int i) {
		Point2D previous = points.get(i);
		Point2D next = points.get(i + 1);
		Line line = new Line(previous.getX(), previous.getY(), next.getX(), next.getY());
		setLineStyle(line);
		lines.add(line);
		pane.getChildren().add(1, line);
	}

}