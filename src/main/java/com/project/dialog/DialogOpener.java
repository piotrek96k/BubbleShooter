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
		TextInputDialog dialog = new TextInputDialog("Imiê Gracza");
		dialog.setTitle("WprowadŸ imiê");
		dialog.setHeaderText("Podaj swoje imiê");
		dialog.setContentText("Gratulacje, twój wynik zakwalifikowa³ ciê do tabeli najlepszych graczy");
		return dialog.showAndWait();
	}

	public static void openExitConfirmationAlert() {
		String title = "PotwierdŸ wyjœcie";
		String header = "Czy na pewno chcesz opuœciæ aplikacjê";
		String content = "";
		Action action = () -> Platform.exit();
		openConfirmationAlert(title, header, content, action);
	}

	public static void openStartNewGameConfirmationAlert(Action action) {
		String title = "PotwierdŸ rozpoczêcie nowej gry";
		String header = "Czy na pewno chcesz zkoñczyæ bie¿¹c¹ grê";
		String content = "Rozpoczêcie nowej gry spowoduje wymazanie bie¿¹cego wyniku";
		openConfirmationAlert(title, header, content, action);
	}

	public static void openBackToMenuConfirmationAlert(Action action) {
		String title = "PotwierdŸ opuszczenie bie¿¹cej gry";
		String header = "Czy na pewno chcesz zkoñczyæ bie¿¹c¹ grê";
		String content = "Powrót do menu spowoduje wymazanie bie¿¹cego wyniku";
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
