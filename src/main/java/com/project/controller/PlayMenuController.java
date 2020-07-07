package com.project.controller;

import java.io.IOException;

import com.project.fxml.FxmlDocument;
import com.project.model.gameplay.Gameplay;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
		initPlayButton();
		initBackButton();
	}

	private void initPlayButton() {
		playButton.setOnAction(event -> {
			Pane pane = (Pane) (gridPane.getParent());
			pane.getChildren().remove(gridPane);
			FXMLLoader loader = FxmlDocument.GAMEPLAY_VIEW.getLoader();
			try {
				Pane loadedPane = loader.load();
				pane.getChildren().add(loadedPane);
				((GameplayController) loader.getController()).setGameplay(new Gameplay());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void initBackButton() {
		backButton.setOnAction(event -> {
			Pane pane = (Pane) (gridPane.getParent());
			pane.getChildren().remove(gridPane);
			try {
				pane.getChildren().add(FxmlDocument.MAIN_MENU.getLoader().load());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}
