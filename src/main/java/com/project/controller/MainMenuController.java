package com.project.controller;

import java.io.IOException;

import com.project.dialog.DialogOpener;
import com.project.fxml.FxmlDocument;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
		playButton.setOnAction(event -> loadFxml(FxmlDocument.PLAY_MENU));
		optionsButton.setOnAction(event -> loadOptions());
		exitButton.setOnAction(event -> DialogOpener.openExitConfirmationAlert());
	}

	private void loadOptions() {
		Pane pane = (Pane) gridPane.getParent();
		pane.getChildren().remove(gridPane);
		FXMLLoader loader = FxmlDocument.OPTIONS_MENU.getLoader();
		try {
			Pane optionsPane = loader.load();
			pane.getChildren().add(optionsPane);
			((OptionsMenuController) loader.getController()).setBackButtonAction(event -> {
				pane.getChildren().remove(optionsPane);
				try {
					pane.getChildren().add(FxmlDocument.MAIN_MENU.getLoader().load());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadFxml(FxmlDocument fxml) {
		Pane pane = (Pane) gridPane.getParent();
		pane.getChildren().remove(gridPane);
		try {
			pane.getChildren().add(fxml.getLoader().load());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
