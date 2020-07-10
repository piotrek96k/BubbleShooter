package com.project.controller;

import com.project.dialog.DialogOpener;
import com.project.fxml.FxmlDocument;
import com.project.fxml.Loader;
import com.project.model.mode.DifficultyLevel;
import com.project.model.mode.GameMode;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class MainMenuController {

	@FXML
	private GridPane gridPane;

	@FXML
	private Button playButton;

	@FXML
	private Button optionsButton;

	@FXML
	private Button bestPlayersButton;

	@FXML
	private Button exitButton;

	@FXML
	private void initialize() {
		playButton.setOnAction(event -> loadFxml(FxmlDocument.PLAY_MENU, gridPane));
		optionsButton.setOnAction(event -> loadAndInitReturnButton(FxmlDocument.OPTIONS_MENU));
		bestPlayersButton.setOnAction(event -> loadAndInitReturnButton(FxmlDocument.STATISTICS_MENU));
		exitButton.setOnAction(event -> DialogOpener.openExitConfirmationAlert());
	}

	private  void loadAndInitReturnButton(FxmlDocument document) {
		Loader<Returnable, Pane> loader = loadFxml(document, gridPane);
		loader.getController().setReturnButtonAction(event -> {
			Pane pane = (Pane) loader.getView().getParent();
			pane.getChildren().remove(loader.getView());
			pane.getChildren().add(new Loader<MainMenuController, Pane>(FxmlDocument.MAIN_MENU).getView());
		});
	}

	public static <T> Loader<T, Pane> loadFxml(FxmlDocument document, Pane child) {
		Pane pane = (Pane) child.getParent();
		pane.getChildren().remove(child);
		Loader<T, Pane> loader = new Loader<T, Pane>(document);
		pane.getChildren().add(loader.getView());
		return loader;
	}

	private static void initModeChoiceBox(ChoiceBox<GameMode> choiceBox) {
		choiceBox.setStyle("-fx-font-size:24;");
		for (GameMode mode : GameMode.values())
			choiceBox.getItems().add(mode);
		choiceBox.getSelectionModel().select(GameMode.ARCADE_MODE);
	}

	public static void initChoiceBoxes(ChoiceBox<DifficultyLevel> levelChoiceBox, ChoiceBox<GameMode> modeChoiceBox) {
		levelChoiceBox.setStyle("-fx-font-size:24;");
		for (DifficultyLevel level : DifficultyLevel.values())
			levelChoiceBox.getItems().add(level);
		levelChoiceBox.getSelectionModel().select(DifficultyLevel.EASY);
		levelChoiceBox.disableProperty()
				.bind(modeChoiceBox.getSelectionModel().selectedItemProperty().isNotEqualTo(GameMode.ARCADE_MODE));
		initModeChoiceBox(modeChoiceBox);
	}

}