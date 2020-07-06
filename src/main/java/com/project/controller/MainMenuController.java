package com.project.controller;

import java.io.IOException;

import com.project.fxml.FxmlDocument;
import com.project.model.gameplay.Gameplay;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainMenuController {

	@FXML
	private VBox vBox;

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
		initPlayButton();
	}

	private void initPlayButton() {
		playButton.setOnAction(event -> {
			StackPane stackPane = (StackPane) (vBox.getParent());
			stackPane.getChildren().remove(vBox);
			FXMLLoader loader = FxmlDocument.GameplayView.getLoader();
			try {
				Pane pane = loader.load();
				stackPane.getChildren().add(pane);
				GameplayController controller = loader.getController();
				Gameplay gameplay = new Gameplay();
				controller.setGameplay(gameplay);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}
