package com.project.controller;

import java.util.List;

import com.project.model.bubble.Bubble;
import com.project.model.gameplay.Gameplay;
import com.project.view.GameplayView;
import com.project.view.Painter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class GameplayController {

	private Gameplay gameplay;

	private GameplayView view;

	private Point2D clickPoint;

	@FXML
	private GridPane gridPane;
	
	@FXML
	private void initialize() {
		gridPane.setBackground(new Background(new BackgroundFill(
				Painter.getLinearGradientPaint(Color.rgb(200, 178, 128)), CornerRadii.EMPTY, new Insets(0.0))));	}

	public void setGameplay(Gameplay gameplay) {
		this.gameplay = gameplay;
		view = new GameplayView();
		view.setController(this);
		initGameplay();
		gridPane.add(view.getPane(),0,0,1,1);
		view.getPane().getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyEvents);
	}

	private void initGameplay() {
		gameplay.addBubbleAddedListener(bubble -> Platform.runLater(() -> addBubble(bubble)));
		gameplay.addBubbleChangedListener(bubble -> Platform.runLater(() -> view.updateBubble(bubble)));
		gameplay.addBubbleRemovedListener(bubble -> Platform.runLater(() -> removeBubble(bubble)));
		gameplay.addMoveListener(() -> Platform.runLater(() -> refreshLines()));
		gameplay.init();
	}

	private void handleKeyEvents(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.SPACE))
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