package com.project.main;

import com.project.controller.GameplayController;
import com.project.model.gameplay.Gameplay;
import com.project.sound.SoundPlayer;
import com.project.view.GameplayView;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationMain extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Group group = new Group();
		SoundPlayer player = SoundPlayer.getInstance();
		player.init(group);
		Gameplay gameplay = new Gameplay(25, 25, 25.0);
		GameplayView gameplayView = new GameplayView();
		group.getChildren().add(gameplayView.getPane());
		Scene scene = new Scene(group);
		stage.setScene(scene);
		stage.setResizable(false);
		new GameplayController(gameplay, gameplayView);
		stage.show();
	}

}