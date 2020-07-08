package com.project.controller;

import java.util.List;

import com.project.fxml.FxmlDocument;
import com.project.fxml.Loader;
import com.project.model.bubble.Bubble;
import com.project.model.gameplay.Gameplay;
import com.project.view.GameplayView;
import com.project.view.Painter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GameplayController {

	private Gameplay gameplay;

	private GameplayView view;

	private Point2D clickPoint;

	private Pane pauseMenuPane;

	@FXML
	private GridPane gridPane;

	@FXML
	private Button pauseButton;

	@FXML
	private Label timeLabel;

	@FXML
	private Label pointsLabel;

	@FXML
	private Label comboLabel;

	@FXML
	private void initialize() {
		gridPane.setBackground(new Background(new BackgroundFill(
				Painter.getLinearGradientPaint(Color.rgb(200, 178, 128)), CornerRadii.EMPTY, new Insets(0.0))));
	}

	public void setGameplay(Gameplay gameplay) {
		this.gameplay = gameplay;
		view = new GameplayView();
		view.setController(this);
		initGameplay();
		gridPane.add(view.getPane(), 0, 0, 1, 1);
		timeLabel.setText(gameplay.getTime());
		pointsLabel.setText(gameplay.getPoints());
		comboLabel.setText(gameplay.getCombo());
		gridPane.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyEvents);
		pauseButton.setOnAction(event -> pauseOrResumeGame());
	}

	private void initGameplay() {
		gameplay.addBubbleAddedListener(bubble -> Platform.runLater(() -> addBubble(bubble)));
		gameplay.addBubbleChangedListener(bubble -> Platform.runLater(() -> view.updateBubble(bubble)));
		gameplay.addBubbleRemovedListener(bubble -> Platform.runLater(() -> removeBubble(bubble)));
		gameplay.addMoveListener(() -> Platform.runLater(() -> view.updateBubblesTab()));
		gameplay.addTimeListener(() -> Platform.runLater(() -> timeLabel.setText(gameplay.getTime())));
		gameplay.addPointsListener(
				() -> Platform.runLater(() -> pointsLabel.setText(gameplay.getPoints())));
		gameplay.addComboListener(() -> Platform.runLater(() -> comboLabel.setText(gameplay.getCombo())));
	}

	private void handleKeyEvents(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.ESCAPE))
			pauseOrResumeGame();
	}

	public void pauseOrResumeGame() {
		gameplay.pauseOrResume();
		if (gameplay.isPaused()) {
			Loader<PauseMenuController, Pane> loader = new Loader<PauseMenuController, Pane>(FxmlDocument.PAUSE_MENU);
			pauseMenuPane = loader.getView();
			pauseMenuPane.setPrefWidth(gridPane.getWidth());
			pauseMenuPane.setPrefHeight(gridPane.getHeight());
			gridPane.add(pauseMenuPane, 0, 0, GridPane.REMAINING, GridPane.REMAINING);
			loader.getController().setGameplayController(this);
		} else
			gridPane.getChildren().remove(pauseMenuPane);
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

	public Gameplay getGameplay() {
		return gameplay;
	}

	public GridPane getGridPane() {
		return gridPane;
	}

}