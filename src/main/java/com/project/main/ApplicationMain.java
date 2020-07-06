package com.project.main;

import com.project.fxml.FxmlDocument;
import com.project.model.gameplay.BubblesTab;
import com.project.sound.SoundPlayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ApplicationMain extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		StackPane stackPane = new StackPane();
		Scene scene = new Scene(stackPane);
		stage.setScene(scene);
		SoundPlayer player = SoundPlayer.getInstance();
		player.init(stackPane);
		FXMLLoader loader = FxmlDocument.MainMenuView.getLoader();
		Pane pane = loader.load();
		pane.setPrefSize(BubblesTab.WIDTH / 2 * 3, BubblesTab.HEIGHT);
		stackPane.getChildren().add(pane);
		stage.setResizable(false);
		stage.show();
	}

}