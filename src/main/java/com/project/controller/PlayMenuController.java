package com.project.controller;

import com.project.fxml.FxmlDocument;
import com.project.fxml.Loader;
import com.project.model.gameplay.Gameplay;
import com.project.model.mode.DifficultyLevel;
import com.project.model.mode.GameMode;

import javafx.beans.binding.When;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
	private ChoiceBox<GameMode> modeChoiceBox;

	@FXML
	private ChoiceBox<DifficultyLevel> levelChoiceBox;

	@FXML
	private void initialize() {
		MainMenuController.initChoiceBoxes(levelChoiceBox, modeChoiceBox);
		initPlayButton();
		backButton.setOnAction(event -> MainMenuController.loadFxml(FxmlDocument.MAIN_MENU, gridPane));
	}

	private void initPlayButton() {
		playButton.setOnAction(event -> {
			Loader<GameplayController, Pane> loader = MainMenuController.loadFxml(FxmlDocument.GAMEPLAY_VIEW, gridPane);
			GameMode mode = modeChoiceBox.getSelectionModel().getSelectedItem();
			if (mode.equals(GameMode.ARCADE_MODE))
				mode.setDifficultyLevel(levelChoiceBox.getSelectionModel().getSelectedItem());
			loader.getController().setGameplay(new Gameplay(mode));
		});
		playButton.disableProperty().bind(modeChoiceBox.getSelectionModel().selectedItemProperty().isNull()
				.or(new When(modeChoiceBox.getSelectionModel().selectedItemProperty().isEqualTo(GameMode.ARCADE_MODE))
						.then(levelChoiceBox.getSelectionModel().selectedItemProperty().isNull()).otherwise(false)));
	}

}