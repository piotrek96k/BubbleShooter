package com.project.dialog;

import java.util.Optional;

import com.project.function.Action;
import com.project.image.GameImage;
import com.project.model.gameplay.PointsCounter;
import com.project.model.gameplay.TimeCounter;
import com.project.resources.Resources;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class DialogOpener {

    private static final String DIALOG_STYLE_CSS;

    static {
        DIALOG_STYLE_CSS = "/css/dialog-style.css";
    }

    private DialogOpener() {
    }

    public static Optional<String> openTextInputDialog(int number, long points, long time) {
        TextInputDialog dialog = new TextInputDialog(
                Resources.RESOURCE_BUNDLE.getString("DialogOpener.openTextInputDialog.dialog"));
        dialog.getDialogPane().getStylesheets().add(DIALOG_STYLE_CSS);
        dialog.getDialogPane().getStyleClass().add("custom-dialog");
        ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(GameImage.ICON.getImage());
        dialog.setTitle(Resources.RESOURCE_BUNDLE.getString("DialogOpener.openTextInputDialog.title"));
        dialog.setHeaderText(Resources.RESOURCE_BUNDLE.getString("DialogOpener.openTextInputDialog.header"));
        String builder = Resources.RESOURCE_BUNDLE.getString("DialogOpener.openTextInputDialog.builder.1") +
                (number + 1) +
                Resources.RESOURCE_BUNDLE.getString("DialogOpener.openTextInputDialog.builder.2") +
                '\n' +
                Resources.RESOURCE_BUNDLE.getString("DialogOpener.openTextInputDialog.builder.3") +
                PointsCounter.getFormattedPoints(points) +
                Resources.RESOURCE_BUNDLE.getString("DialogOpener.openTextInputDialog.builder.4") +
                '\n' +
                Resources.RESOURCE_BUNDLE.getString("DialogOpener.openTextInputDialog.builder.5") +
                TimeCounter.getFullFormattedTime(time);
        dialog.setContentText(builder);
        return dialog.showAndWait();
    }

    public static void openExitConfirmationAlert() {
        String title = Resources.RESOURCE_BUNDLE.getString("DialogOpener.openExitConfirmationAlert.title");
        String header = Resources.RESOURCE_BUNDLE.getString("DialogOpener.openExitConfirmationAlert.header");
        String content = "";
        Action action = Platform::exit;
        openConfirmationAlert(title, header, content, action);
    }

    public static void openErrorDialog(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(DIALOG_STYLE_CSS);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(GameImage.ICON.getImage());
        alert.setTitle(Resources.RESOURCE_BUNDLE.getString("DialogOpener.openErrorDialog.title"));
        alert.setHeaderText(Resources.RESOURCE_BUNDLE.getString("DialogOpener.openErrorDialog.header"));
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void openStartNewGameConfirmationAlert(Action action) {
        String title = Resources.RESOURCE_BUNDLE.getString("DialogOpener.openStartNewGameConfirmationAlert.title");
        String header = Resources.RESOURCE_BUNDLE.getString("DialogOpener.openStartNewGameConfirmationAlert.header");
        String content = Resources.RESOURCE_BUNDLE.getString("DialogOpener.openStartNewGameConfirmationAlert.content");
        openConfirmationAlert(title, header, content, action);
    }

    public static void openBackToMenuConfirmationAlert(Action action) {
        String title = Resources.RESOURCE_BUNDLE.getString("DialogOpener.openBackToMenuConfirmationAlert.title");
        String header = Resources.RESOURCE_BUNDLE.getString("DialogOpener.openBackToMenuConfirmationAlert.header");
        String content = Resources.RESOURCE_BUNDLE.getString("DialogOpener.openBackToMenuConfirmationAlert.content");
        openConfirmationAlert(title, header, content, action);
    }

    private static void openConfirmationAlert(String title, String header, String content, Action action) {
        ButtonType okButton = new ButtonType(
                Resources.RESOURCE_BUNDLE.getString("DialogOpener.openConfirmationAlert.ok"), ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(
                Resources.RESOURCE_BUNDLE.getString("DialogOpener.openConfirmationAlert.cancel"),
                ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(AlertType.CONFIRMATION, content, okButton, cancelButton);
        alert.getDialogPane().getStylesheets().add(DIALOG_STYLE_CSS);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(GameImage.ICON.getImage());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait().ifPresent(response -> {
            if (response.equals(okButton))
                action.execute();
        });
    }

}
