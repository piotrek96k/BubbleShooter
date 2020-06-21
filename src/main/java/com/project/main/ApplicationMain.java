package com.project.main;

import com.project.controller.GameplayController;
import com.project.model.gameplay.Gameplay;
import com.project.view.GameplayView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationMain extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Gameplay gameplay = new Gameplay(25,25,25.0);
		GameplayView gameplayView = new GameplayView();
		stage.setScene(new Scene(gameplayView.getPane()));
		stage.setResizable(false);
		new GameplayController(gameplay,gameplayView);
		stage.show();
	}

}