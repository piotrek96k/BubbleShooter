package com.project.dialog;

import java.util.Optional;

import com.project.function.Action;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

public class DialogOpener {

	private DialogOpener() {
	}

	public static Optional<String> openTextInputDialog() {
		TextInputDialog dialog = new TextInputDialog("Imi� Gracza");
		dialog.setTitle("Wprowad� imi�");
		dialog.setHeaderText("Podaj swoje imi�");
		dialog.setContentText("Gratulacje, tw�j wynik zakwalifikowa� ci� do tabeli najlepszych graczy");
		return dialog.showAndWait();
	}

	public static void openExitConfirmationAlert() {
		String title = "Potwierd� wyj�cie";
		String header = "Czy na pewno chcesz opu�ci� aplikacj�";
		String content = "";
		Action action = () -> Platform.exit();
		openConfirmationAlert(title, header, content, action);
	}

	public static void openStartNewGameConfirmationAlert(Action action) {
		String title = "Potwierd� rozpocz�cie nowej gry";
		String header = "Czy na pewno chcesz zko�czy� bie��c� gr�";
		String content = "Rozpocz�cie nowej gry spowoduje wymazanie bie��cego wyniku";
		openConfirmationAlert(title, header, content, action);
	}

	public static void openBackToMenuConfirmationAlert(Action action) {
		String title = "Potwierd� opuszczenie bie��cej gry";
		String header = "Czy na pewno chcesz zko�czy� bie��c� gr�";
		String content = "Powr�t do menu spowoduje wymazanie bie��cego wyniku";
		openConfirmationAlert(title, header, content, action);
	}

	private static void openConfirmationAlert(String title, String header, String content, Action action) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait().ifPresent(response -> {
			if (response.equals(ButtonType.OK))
				action.execute();
		});
	}

}
