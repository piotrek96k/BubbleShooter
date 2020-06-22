package com.project.controller;

import java.util.List;

import com.project.model.bubble.Bubble;
import com.project.model.gameplay.Gameplay;
import com.project.view.GameplayView;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GameplayController {

	private Gameplay gameplay;

	private GameplayView view;

	private Point2D clickPoint;

	public GameplayController(Gameplay gameplay, GameplayView view) {
		this.gameplay = gameplay;
		this.view = view;
		view.setController(this);
		view.getPane().getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyEvents);
		initGameplay();
		setEndGameListener();
	}

	private void initGameplay() {
		gameplay.addBubbleAddedListener(bubble -> Platform.runLater(() -> addBubble(bubble)));
		gameplay.addBubbleChangedListener(bubble -> Platform.runLater(() -> view.updateBubble(bubble)));
		gameplay.addBubbleRemovedListener(bubble -> Platform.runLater(() -> removeBubble(bubble)));
		gameplay.addMoveListener(()->Platform.runLater(()->refreshLines()));
		gameplay.init();
	}

	private void setEndGameListener() {
		view.getPane().getScene().getWindow().setOnCloseRequest(observable -> gameplay.finishGame());
	}
	
	private void handleKeyEvents(KeyEvent keyEvent) {
		if(keyEvent.getCode().equals(KeyCode.SPACE))
			gameplay.pauseOrResume();
	}

	private void addBubble(Bubble ball) {
		view.addBubble(ball);
		refreshLines();
	}

	private void removeBubble(Bubble ball) {
		view.removeBubble(ball);
		refreshLines();
	}
	
	private void refreshLines() {
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