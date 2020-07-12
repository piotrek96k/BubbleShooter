package com.project.dialog;

import java.util.Optional;

import com.project.function.Action;
import com.project.image.GameImage;
import com.project.model.gameplay.PointsCounter;
import com.project.model.gameplay.TimeCounter;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class DialogOpener {

	private static final String DIALOG_STYLE_CSS;

	static {
		DIALOG_STYLE_CSS = "/css/DialogStyle.css";
	}

	private DialogOpener() {
	}

	public static Optional<String> openTextInputDialog(int number, long points, long time) {
		TextInputDialog dialog = new TextInputDialog("Imi� Gracza");
		dialog.getDialogPane().getStylesheets().add(DIALOG_STYLE_CSS);
		dialog.getDialogPane().getStyleClass().add("custom-dialog");
		((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(GameImage.ICON.getImage());
		dialog.setTitle("Wprowad� imi�");
		dialog.setHeaderText("Podaj swoje imi�");
		StringBuilder builder = new StringBuilder();
		builder.append("Gratulacje, podaj swoje imi�, aby zapisa� si� jako nr ");
		builder.append(number + 1);
		builder.append(" w�r�d 10 najlepszych\n");
		builder.append("Tw�j wynik wynosi ");
		builder.append(PointsCounter.getFormattedPoints(points));
		builder.append(" punkt�w\nCzas twojej gry wynosi ");
		builder.append(TimeCounter.getFullFormattedTime(time));
		dialog.setContentText(builder.toString());
		return dialog.showAndWait();
	}

	public static void openExitConfirmationAlert() {
		String title = "Potwierd� wyj�cie";
		String header = "Czy na pewno chcesz opu�ci� aplikacj�";
		String content = "";
		Action action = () -> Platform.exit();
		openConfirmationAlert(title, header, content, action);
	}

	public static void openErrorDialog(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().getStylesheets().add(DIALOG_STYLE_CSS);
		((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(GameImage.ICON.getImage());
		alert.setTitle("B��d");
		alert.setHeaderText("Co� posz�o nie tak :(");
		alert.setContentText(message);
		alert.showAndWait();
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
		alert.getDialogPane().getStylesheets().add(DIALOG_STYLE_CSS);
		((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(GameImage.ICON.getImage());
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait().ifPresent(response -> {
			if (response.equals(ButtonType.OK))
				action.execute();
		});
	}

}
