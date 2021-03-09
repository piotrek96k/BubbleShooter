package com.project.controller;

import com.project.dialog.DialogOpener;
import com.project.fxml.FxmlDocument;
import com.project.fxml.Loader;
import com.project.image.GameImage;
import com.project.model.mode.DifficultyLevel;
import com.project.model.mode.GameMode;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class MainMenuController {

    private static DifficultyLevel difficultyLevel;

    private static GameMode gameMode;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button playButton;

    @FXML
    private Button optionsButton;

    @FXML
    private Button bestPlayersButton;

    @FXML
    private Button exitButton;

    static {
        difficultyLevel = DifficultyLevel.EASY;
        gameMode = GameMode.ARCADE_MODE;
    }

    @FXML
    private void initialize() {
        gridPane.setStyle(GameImage.getRandomWallpaperStyle());
        playButton.setOnAction(event -> loadFxml(FxmlDocument.PLAY_MENU, gridPane));
        optionsButton.setOnAction(event -> loadAndInitReturnButton(FxmlDocument.OPTIONS_MENU));
        bestPlayersButton.setOnAction(
                event -> this.<StatisticsMenuController>loadAndInitReturnButton(FxmlDocument.STATISTICS_MENU)
                        .getController().init());
        exitButton.setOnAction(event -> DialogOpener.openExitConfirmationAlert());
    }

    private <T extends Returnable> Loader<T, Pane> loadAndInitReturnButton(FxmlDocument document) {
        Loader<T, Pane> loader = loadFxml(document, gridPane);
        loader.getController().setReturnButtonAction(event -> {
            Pane pane = (Pane) loader.getView().getParent();
            pane.getChildren().remove(loader.getView());
            pane.getChildren().add(new Loader<MainMenuController, Pane>(FxmlDocument.MAIN_MENU).getView());
        });
        return loader;
    }

    public static <T> Loader<T, Pane> loadFxml(FxmlDocument document, Pane child) {
        Pane pane = (Pane) child.getParent();
        pane.getChildren().remove(child);
        Loader<T, Pane> loader = new Loader<T, Pane>(document);
        pane.getChildren().add(loader.getView());
        return loader;
    }

    private static void initModeChoiceBox(ChoiceBox<GameMode> choiceBox) {
        choiceBox.getItems().addAll(GameMode.values());
        choiceBox.getSelectionModel().select(gameMode);
        choiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldMode, newMode) -> gameMode = newMode);
    }

    public static void initChoiceBoxes(ChoiceBox<DifficultyLevel> levelChoiceBox, ChoiceBox<GameMode> modeChoiceBox) {
        levelChoiceBox.getItems().addAll(DifficultyLevel.values());
        levelChoiceBox.getSelectionModel().select(difficultyLevel);
        levelChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldLevel, newLevel) -> difficultyLevel = newLevel);
        levelChoiceBox.disableProperty()
                .bind(modeChoiceBox.getSelectionModel().selectedItemProperty().isNotEqualTo(GameMode.ARCADE_MODE));
        initModeChoiceBox(modeChoiceBox);
    }

}