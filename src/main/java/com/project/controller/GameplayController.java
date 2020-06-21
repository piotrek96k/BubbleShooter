package com.project.controller;

import java.util.List;

import com.project.model.bubble.Bubble;
import com.project.model.gameplay.Gameplay;
import com.project.view.GameplayView;

import javafx.application.Platform;
import javafx.geometry.Point2D;

public class GameplayController {

	private Gameplay gameplay;

	private GameplayView view;

	private Point2D clickPoint;

	public GameplayController(Gameplay gameplay, GameplayView view) {
		this.gameplay = gameplay;
		this.view = view;
		view.setController(this);
		initGameplay();
		setEndGameListener();
	}

	private void initGameplay() {
		gameplay.addBubbleAddedListener(bubble -> Platform.runLater(() -> addBubble(bubble)));
		gameplay.addBubbleChangedListener(bubble -> Platform.runLater(() -> view.updateBubble(bubble)));
		gameplay.addBubbleRemovedListener(bubble -> Platform.runLater(() -> removeBubble(bubble)));
		gameplay.init();
	}

	private void setEndGameListener() {
		view.getPane().getScene().getWindow().setOnCloseRequest(observable -> gameplay.finishGame());
	}

	private void addBubble(Bubble ball) {
		view.addBubble(ball);
		if (clickPoint != null)
			view.manageLines(getLinePoints(clickPoint.getX(), clickPoint.getY()));
	}

	private void removeBubble(Bubble ball) {
		view.removeBubble(ball);
		if (clickPoint != null)
			view.manageLines(getLinePoints(clickPoint.getX(), clickPoint.getY()));
	}

	public List<Point2D> getLinePoints(double x, double y) {
		clickPoint = new Point2D(x, y);
		return gameplay.getLinePoints(x, y);
	}

	public void throwBubble(double x, double y) {
		gameplay.throwBubble(x, y);
	}

	public Gameplay getModel() {
		return gameplay;
	}

}