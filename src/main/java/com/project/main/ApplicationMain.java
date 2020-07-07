package com.project.main;

import com.project.dialog.DialogOpener;
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
		SoundPlayer.getInstance().init(stackPane);
		FXMLLoader loader = FxmlDocument.MAIN_MENU.getLoader();
		Pane pane = loader.load();
		pane.setPrefSize(BubblesTab.WIDTH / 2 * 3, BubblesTab.HEIGHT);
		stackPane.getChildren().add(pane);
		stage.setOnCloseRequest(event -> {
			event.consume();
			DialogOpener.openExitConfirmationAlert();
		});
		stage.setTitle("Bubble Shooter");
		stage.setResizable(false);
		stage.show();
	}

	@Override
	public void stop() {
		SoundPlayer.getInstance().saveUserPreferences();
	}

}