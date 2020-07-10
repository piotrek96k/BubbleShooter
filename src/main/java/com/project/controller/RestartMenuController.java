package com.project.controller;

import com.project.dialog.DialogOpener;
import com.project.fxml.FxmlDocument;
import com.project.model.gameplay.Gameplay;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RestartMenuController {

	@FXML
	private Button restartButton;

	@FXML
	private Button menuButton;

	@FXML
	private Button exitButton;

	public void setGameplayController(GameplayController gameplayController) {
		restartButton.setOnAction(event -> ((GameplayController) (MainMenuController
				.loadFxml(FxmlDocument.GAMEPLAY_VIEW, gameplayController.getGridPane()).getController()))
						.setGameplay(new Gameplay(gameplayController.getGameplay().getGameMode())));
		menuButton.setOnAction(
				event -> MainMenuController.loadFxml(FxmlDocument.PLAY_MENU, gameplayController.getGridPane()));
		exitButton.setOnAction(event -> DialogOpener.openExitConfirmationAlert());
	}

}