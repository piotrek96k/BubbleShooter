package com.project.main;

import com.project.controller.MainMenuController;
import com.project.dialog.DialogOpener;
import com.project.fxml.FxmlDocument;
import com.project.fxml.Loader;
import com.project.image.GameImage;
import com.project.model.gameplay.BubblesTab;
import com.project.resources.Resources;
import com.project.sound.SoundPlayer;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ApplicationMain extends Application {

	private static final String APPLICATION_TITLE;

	public static final EventHandler<WindowEvent> DEFAULT_WINDOW_CLOSE_HANDLER;

	private static final String APPLICATION_STYLE_CSS;

	private boolean wereEffectsMuted;

	private boolean wasMusicMuted;

	static {
		DEFAULT_WINDOW_CLOSE_HANDLER = ApplicationMain::handleCloseRequest;
		APPLICATION_STYLE_CSS = "/css/ApplicationStyle.css";
		APPLICATION_TITLE = Resources.RESOURCE_BUNDLE.getString("ApplicationMain.APPLICATION_TITLE");
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		StackPane stackPane = new StackPane();
		Scene scene = new Scene(stackPane);
		scene.getStylesheets().add(APPLICATION_STYLE_CSS);
		stage.setScene(scene);
		SoundPlayer.getInstance().init(stackPane);
		Loader<MainMenuController, Pane> loader = new Loader<MainMenuController, Pane>(FxmlDocument.MAIN_MENU);
		Pane pane = loader.getView();
		pane.setPrefSize(BubblesTab.WIDTH / 2 * 3, BubblesTab.HEIGHT);
		stackPane.getChildren().add(pane);
		stage.setOnCloseRequest(DEFAULT_WINDOW_CLOSE_HANDLER);
		stage.iconifiedProperty().addListener(this::handleStageIconizing);
		stage.setTitle(APPLICATION_TITLE);
		stage.getIcons().add(GameImage.ICON.getImage());
		stage.setResizable(false);
		stage.show();
	}

	@Override
	public void stop() {
		SoundPlayer.getInstance().saveUserPreferences();
	}

	private void handleStageIconizing(ObservableValue<? extends Boolean> observable, Boolean oldValue,
			Boolean newValue) {
		if (newValue.booleanValue()) {
			if (SoundPlayer.getInstance().isEffectsMuted())
				wereEffectsMuted = true;
			else {
				wereEffectsMuted = false;
				SoundPlayer.getInstance().muteOrUnmuteEffects();
			}
			if (SoundPlayer.getInstance().isMusicMuted())
				wasMusicMuted = true;
			else {
				wasMusicMuted = false;
				SoundPlayer.getInstance().muteOrUnmuteMusic();
			}
		} else {
			if (!wereEffectsMuted)
				SoundPlayer.getInstance().muteOrUnmuteEffects();
			if (!wasMusicMuted)
				SoundPlayer.getInstance().muteOrUnmuteMusic();
		}
	}

	private static void handleCloseRequest(WindowEvent event) {
		event.consume();
		DialogOpener.openExitConfirmationAlert();
	}

}