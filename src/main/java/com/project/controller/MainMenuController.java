package com.project.controller;

import com.project.dialog.DialogOpener;
import com.project.fxml.FxmlDocument;
import com.project.fxml.Loader;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
		optionsButton.setOnAction(event -> loadOptions());
		exitButton.setOnAction(event -> DialogOpener.openExitConfirmationAlert());
	}

	private void loadOptions() {
		Loader<OptionsMenuController, Pane> loader = loadFxml(FxmlDocument.OPTIONS_MENU, gridPane);
		loader.getController().setBackButtonAction(event -> {
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

}