package com.project.fxml;

import javafx.fxml.FXMLLoader;

public enum FxmlDocument {

	GAMEPLAY_VIEW("/fxml/GameplayView.fxml"),

	MAIN_MENU("/fxml/MainMenu.fxml"),

	OPTIONS_MENU("/fxml/OptionsMenu.fxml"),

	PAUSE_MENU("/fxml/PauseMenu.fxml"),

	PLAY_MENU("/fxml/PlayMenu.fxml");

	String document;

	private FxmlDocument(String document) {
		this.document = document;
	}

	public FXMLLoader getLoader() {
		return new FXMLLoader(FxmlDocument.class.getResource(document));
	}

}
