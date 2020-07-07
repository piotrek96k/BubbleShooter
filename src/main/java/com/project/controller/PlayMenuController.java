package com.project.controller;

import com.project.fxml.FxmlDocument;
import com.project.fxml.Loader;
import com.project.model.gameplay.Gameplay;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class PlayMenuController {

	@FXML
	private GridPane gridPane;

	@FXML
	private Button backButton;

	@FXML
	private Button playButton;

	@FXML
	private void initialize() {
		playButton.setOnAction(event -> {
			Loader<GameplayController, Pane> loader = MainMenuController.loadFxml(FxmlDocument.GAMEPLAY_VIEW, gridPane);
			loader.getController().setGameplay(new Gameplay());
		});
		backButton.setOnAction(event -> MainMenuController.loadFxml(FxmlDocument.MAIN_MENU, gridPane));
	}

}