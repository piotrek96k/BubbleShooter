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
		TextInputDialog dialog = new TextInputDialog("Imiê Gracza");
		dialog.getDialogPane().getStylesheets().add(DIALOG_STYLE_CSS);
		dialog.getDialogPane().getStyleClass().add("custom-dialog");
		((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(GameImage.ICON.getImage());
		dialog.setTitle("WprowadŸ imiê");
		dialog.setHeaderText("Podaj swoje imiê");
		StringBuilder builder = new StringBuilder();
		builder.append("Gratulacje, podaj swoje imiê, aby zapisaæ siê jako nr ");
		builder.append(number + 1);
		builder.append(" wœród 10 najlepszych\n");
		builder.append("Twój wynik wynosi ");
		builder.append(PointsCounter.getFormattedPoints(points));
		builder.append(" punktów\nCzas twojej gry wynosi ");
		builder.append(TimeCounter.getFullFormattedTime(time));
		dialog.setContentText(builder.toString());
		return dialog.showAndWait();
	}

	public static void openExitConfirmationAlert() {
		String title = "PotwierdŸ wyjœcie";
		String header = "Czy na pewno chcesz opuœciæ aplikacjê";
		String content = "";
		Action action = () -> Platform.exit();
		openConfirmationAlert(title, header, content, action);
	}

	public static void openErrorDialog(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().getStylesheets().add(DIALOG_STYLE_CSS);
		((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(GameImage.ICON.getImage());
		alert.setTitle("B³¹d");
		alert.setHeaderText("Coœ posz³o nie tak :(");
		alert.setContentText(message);
		alert.showAndWait();
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
