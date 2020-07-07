package com.project.controller;

import java.io.IOException;

import com.project.dialog.DialogOpener;
import com.project.fxml.FxmlDocument;
import com.project.model.gameplay.Gameplay;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class PauseMenuController {

	private GameplayController gameplayController;

	@FXML
	private VBox vBox;

	@FXML
	private Button resumeButton;

	@FXML
	private Button restartButton;

	@FXML
	private Button optionsButton;

	@FXML
	private Button bestPlayersButton;

	@FXML
	private Button menuButton;

	public void setGameplayController(GameplayController gameplayController) {
		this.gameplayController = gameplayController;
		resumeButton.setOnAction(event -> gameplayController.pauseOrResumeGame());
		restartButton.setOnAction(event -> DialogOpener.openStartNewGameConfirmationAlert(this::startNewGame));
		optionsButton.setOnAction(event -> loadOptionsMenu());
		menuButton.setOnAction(event -> DialogOpener.openBackToMenuConfirmationAlert(this::loadPlayMenu));
	}

	private void startNewGame() {
		gameplayController.getGameplay().finishGame();
		Pane gridPane = gameplayController.getGridPane();
		Pane pane = (Pane) gridPane.getParent();
		pane.getChildren().remove(gridPane);
		FXMLLoader loader = FxmlDocument.GAMEPLAY_VIEW.getLoader();
		try {
			Pane loadedPane = loader.load();
			pane.getChildren().add(loadedPane);
			((GameplayController) loader.getController()).setGameplay(new Gameplay());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadOptionsMenu() {
		GridPane gridPane = gameplayController.getGridPane();
		Pane pane = (Pane) vBox.getParent().getParent();
		pane.getChildren().remove(gridPane);
		FXMLLoader loader = FxmlDocument.OPTIONS_MENU.getLoader();
		try {
			Pane optionsPane = loader.load();
			pane.getChildren().add(optionsPane);
			((OptionsMenuController) loader.getController()).setBackButtonAction(event -> {
				pane.getChildren().remove(optionsPane);
				pane.getChildren().add(gridPane);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadPlayMenu() {
		gameplayController.getGameplay().finishGame();
		Pane gridPane = gameplayController.getGridPane();
		Pane pane = (Pane) gridPane.getParent();
		pane.getChildren().remove(gridPane);
		try {
			pane.getChildren().add(FxmlDocument.PLAY_MENU.getLoader().load());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
