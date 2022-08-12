package com.piotrek.controller;

import com.piotrek.dialog.DialogOpener;
import com.piotrek.fxml.FxmlDocument;
import com.piotrek.fxml.Loader;
import com.piotrek.model.gameplay.Gameplay;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class PauseMenuController {

    private GameplayController gameplayController;

    @FXML
    private VBox vBox;

    @FXML
    private Button resumeButton;

    @FXML
    private Button restartButton;

    @FXML
    private Button optionsButton;

    @FXML
    private Button bestPlayersButton;

    @FXML
    private Button menuButton;

    public void setGameplayController(GameplayController gameplayController) {
        this.gameplayController = gameplayController;
        resumeButton.setOnAction(event -> gameplayController.pauseOrResumeGame());
        restartButton.setOnAction(event -> DialogOpener.openStartNewGameConfirmationAlert(this::startNewGame));
        optionsButton.setOnAction(event -> loadAndInitReturnButton(FxmlDocument.OPTIONS_MENU));
        bestPlayersButton.setOnAction(
                event -> this.<StatisticsMenuController>loadAndInitReturnButton(FxmlDocument.STATISTICS_MENU)
                        .getController().init());
        menuButton.setOnAction(event -> DialogOpener.openBackToMenuConfirmationAlert(this::loadPlayMenu));
    }

    private void startNewGame() {
        gameplayController.getGameplay().finishGame();
        gameplayController.dispose();
        Pane gridPane = gameplayController.getGridPane();
        Loader<GameplayController, Pane> loader = MainMenuController.loadFxml(FxmlDocument.GAMEPLAY_MENU, gridPane);
        loader.getController().setGameplay(new Gameplay(gameplayController.getGameplay().getGameMode()));
    }

    private <T extends Returnable> Loader<T, Pane> loadAndInitReturnButton(FxmlDocument document) {
        GridPane gridPane = gameplayController.getGridPane();
        Pane pane = (Pane) vBox.getParent().getParent();
        pane.getChildren().remove(gridPane);
        Loader<T, Pane> loader = new Loader<>(document);
        Pane optionsPane = loader.getView();
        pane.getChildren().add(optionsPane);
        loader.getController().setReturnButtonAction(event -> {
            pane.getChildren().remove(optionsPane);
            pane.getChildren().add(gridPane);
        });
        return loader;
    }

    private void loadPlayMenu() {
        gameplayController.getGameplay().finishGame();
        gameplayController.dispose();
        Pane gridPane = gameplayController.getGridPane();
        MainMenuController.loadFxml(FxmlDocument.PLAY_MENU, gridPane);
    }

}