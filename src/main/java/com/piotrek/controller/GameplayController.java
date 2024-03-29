package com.piotrek.controller;

import java.util.List;

import com.piotrek.dialog.DialogOpener;
import com.piotrek.exception.ReadingFileException;
import com.piotrek.exception.WritingFileException;
import com.piotrek.fxml.FxmlDocument;
import com.piotrek.fxml.Loader;
import com.piotrek.main.ApplicationMain;
import com.piotrek.model.bubble.Bubble;
import com.piotrek.model.gameplay.Gameplay;
import com.piotrek.model.gameplay.PointsCounter;
import com.piotrek.model.gameplay.TimeCounter;
import com.piotrek.model.mode.GameMode;
import com.piotrek.model.player.Player;
import com.piotrek.view.GameplayView;
import com.piotrek.view.Painter;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameplayController {

    private Gameplay gameplay;

    private GameplayView view;

    private Point2D clickPoint;

    private Pane pauseMenuPane;

    private EventHandler<KeyEvent> keyEventHandler;

    private ChangeListener<? super Boolean> iconifingListener;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button pauseButton;

    @FXML
    private TextField timeField;

    @FXML
    private TextField pointsField;

    @FXML
    private TextField comboField;

    @FXML
    private TextField gameModeField;

    @FXML
    private TextField levelField;

    private boolean wasPaused;

    @FXML
    private void initialize() {
        gridPane.setBackground(new Background(new BackgroundFill(
                Painter.getLinearGradientPaint(Color.rgb(200, 178, 128)), CornerRadii.EMPTY, new Insets(0.0))));
        pauseButton.getStyleClass().add("button-gameplay");
    }

    public void setGameplay(Gameplay gameplay) {
        this.gameplay = gameplay;
        view = new GameplayView();
        view.setController(this);
        initGameplay();
        initViewElements();
        iconifingListener = this::handleStageIconizing;
        ((Stage) gridPane.getScene().getWindow()).iconifiedProperty().addListener(iconifingListener);
        EventHandler<WindowEvent> windowCloseHandler = event -> {
            if (!gameplay.getFinishedProperty().get() && !gameplay.isPaused())
                pauseOrResumeGame();
            ApplicationMain.DEFAULT_WINDOW_CLOSE_HANDLER.handle(event);
        };
        gridPane.getScene().getWindow().setOnCloseRequest(windowCloseHandler);
    }

    private void initViewElements() {
        gridPane.add(view.getPane(), 0, 0, 1, 1);
        timeField.setText(TimeCounter.getSimpleFormattedTime(gameplay.getTime()));
        pointsField.setText(PointsCounter.getFormattedPoints(gameplay.getPoints()));
        comboField.setText(PointsCounter.getFormattedCombo(gameplay.getCombo()));
        keyEventHandler = this::handleKeyEvents;
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);
        pauseButton.setOnAction(event -> pauseOrResumeGame());
        GameMode gameMode = gameplay.getGameMode();
        gameModeField.setText(gameMode.toString());
        if (gameMode.equals(GameMode.ARCADE_MODE))
            levelField.setText(gameplay.getGameMode().getDifficultyLevel().toString());
    }

    private void initGameplay() {
        gameplay.addBubbleAddedListener(bubble -> Platform.runLater(() -> addBubble(bubble)));
        gameplay.addBubbleChangedListener(bubble -> Platform.runLater(() -> view.updateBubble(bubble)));
        gameplay.addBubbleRemovedListener(bubble -> Platform.runLater(() -> removeBubble(bubble)));
        gameplay.addMoveListener(() -> Platform.runLater(() -> {
            view.updateBubblesTab();
            refreshLines();
        }));
        gameplay.addTimeListener(() -> Platform
                .runLater(() -> timeField.setText(TimeCounter.getSimpleFormattedTime(gameplay.getTime()))));
        gameplay.addPointsListener(() -> Platform
                .runLater(() -> pointsField.setText(PointsCounter.getFormattedPoints(gameplay.getPoints()))));
        gameplay.addComboListener(() -> Platform
                .runLater(() -> comboField.setText(PointsCounter.getFormattedCombo(gameplay.getCombo()))));
        gameplay.getFinishedProperty().addListener(this::handleFinishingGame);
        view.getPane().addEventHandler(MouseEvent.MOUSE_EXITED, event -> clickPoint = null);
    }

    private void handleStageIconizing(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                      Boolean newValue) {
        if (!gameplay.getFinishedProperty().get())
            if (newValue.booleanValue()) {
                wasPaused = gameplay.isPaused();
                if (!gameplay.isPaused())
                    gameplay.pauseOrResume();
            } else if (!wasPaused)
                gameplay.pauseOrResume();
    }

    private void handleFinishingGame(Observable observable) {
        if (gameplay.isPaused())
            return;
        gridPane.removeEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);
        Platform.runLater(() -> {
            try {
                int number = Player.willBeAddedToList(gameplay);
                if (number >= 0)
                    Player.addPlayer(gameplay,
                            DialogOpener.openTextInputDialog(number, gameplay.getPoints(), gameplay.getTime()));
            } catch (ReadingFileException | WritingFileException e) {
                DialogOpener.openErrorDialog(e.getMessage());
            }
            Loader<RestartMenuController, Pane> loader = new Loader<RestartMenuController, Pane>(
                    FxmlDocument.RESTART_MENU);
            Pane pane = loader.getView();
            pane.setPrefWidth(gridPane.getWidth());
            pane.setPrefHeight(gridPane.getHeight());
            gridPane.add(loader.getView(), 0, 0, GridPane.REMAINING, GridPane.REMAINING);
            loader.getController().setGameplayController(this);
        });
    }

    private void handleKeyEvents(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ESCAPE))
            pauseOrResumeGame();
        else if (keyEvent.getCode().equals(KeyCode.SPACE)) {
            keyEvent.consume();
            gameplay.replaceBubble();
        }
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

    private void addBubble(Bubble bubble) {
        view.addBubble(bubble);
        refreshLines();
    }

    private void removeBubble(Bubble bubble) {
        view.removeBubble(bubble);
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

    public void replaceBubble() {
        gameplay.replaceBubble();
    }

    public Gameplay getGameplay() {
        return gameplay;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void dispose() {
        view.cancelTimer();
        ((Stage) gridPane.getScene().getWindow()).iconifiedProperty().removeListener(iconifingListener);
        gridPane.getScene().getWindow().setOnCloseRequest(ApplicationMain.DEFAULT_WINDOW_CLOSE_HANDLER);
    }

}