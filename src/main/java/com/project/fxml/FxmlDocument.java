package com.project.fxml;

import javafx.fxml.FXMLLoader;

public enum FxmlDocument {

	MainMenuView("/fxml/MainMenuView.fxml"), GameplayView("/fxml/GameplayView.fxml");

	String document;

	private FxmlDocument(String document) {
		this.document = document;
	}

	public FXMLLoader getLoader() {
		return new FXMLLoader(FxmlDocument.class.getResource(document));
	}

}
