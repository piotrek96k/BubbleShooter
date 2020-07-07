package com.project.controller;

import com.project.dialog.DialogOpener;
import com.project.fxml.FxmlDocument;
import com.project.fxml.Loader;
import com.project.model.gameplay.Gameplay;

import javafx.fxml.FXML;
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
		Loader<GameplayController, Pane> loader = MainMenuController.loadFxml(FxmlDocument.GAMEPLAY_VIEW, gridPane);
		loader.getController().setGameplay(new Gameplay());
	}

	private void loadOptionsMenu() {
		GridPane gridPane = gameplayController.getGridPane();
		Pane pane = (Pane) vBox.getParent().getParent();
		pane.getChildren().remove(gridPane);
		Loader<OptionsMenuController, Pane> loader = new Loader<OptionsMenuController, Pane>(FxmlDocument.OPTIONS_MENU);
		Pane optionsPane = loader.getView();
		pane.getChildren().add(optionsPane);
		loader.getController().setBackButtonAction(event -> {
			pane.getChildren().remove(optionsPane);
			pane.getChildren().add(gridPane);
		});
	}

	private void loadPlayMenu() {
		gameplayController.getGameplay().finishGame();
		Pane gridPane = gameplayController.getGridPane();
		MainMenuController.loadFxml(FxmlDocument.PLAY_MENU, gridPane);
	}

}