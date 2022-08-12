package com.piotrek.controller;

import com.piotrek.dialog.DialogOpener;
import com.piotrek.fxml.FxmlDocument;
import com.piotrek.model.gameplay.Gameplay;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RestartMenuController {

    @FXML
    private Button restartButton;

    @FXML
    private Button menuButton;

    @FXML
    private Button exitButton;

    public void setGameplayController(GameplayController gameplayController) {
        restartButton.setOnAction(event -> {
            gameplayController.dispose();
            (MainMenuController
                    .<GameplayController>loadFxml(FxmlDocument.GAMEPLAY_MENU, gameplayController.getGridPane())
                    .getController()).setGameplay(new Gameplay(gameplayController.getGameplay().getGameMode()));
        });
        menuButton.setOnAction(event -> {
            gameplayController.dispose();
            MainMenuController.loadFxml(FxmlDocument.PLAY_MENU, gameplayController.getGridPane());
        });
        exitButton.setOnAction(event -> {
            gameplayController.dispose();
            DialogOpener.openExitConfirmationAlert();
        });
    }

}