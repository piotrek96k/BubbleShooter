package com.piotrek.controller;

import com.piotrek.image.GameImage;
import com.piotrek.sound.SoundPlayer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class OptionsMenuController implements Returnable {

    @FXML
    private GridPane gridPane;

    @FXML
    private Button returnButton;

    @FXML
    private ToggleGroup effectsGroup;

    @FXML
    private ToggleGroup musicGroup;

    @FXML
    private RadioButton effectsOnRadioButton;

    @FXML
    private RadioButton effectsOffRadioButton;

    @FXML
    private RadioButton musicOnRadioButton;

    @FXML
    private RadioButton musicOffRadioButton;

    @FXML
    private Slider effectsSlider;

    @FXML
    private Slider musicSlider;

    @FXML
    private Label effectsVolumeLabel;

    @FXML
    private Label musicVolumeLabel;

    @FXML
    private void initialize() {
        gridPane.setStyle(GameImage.getWallpaperStyle(GameImage.OPTIONS_MENU_BACKGROUND));
        initMuteRadioButtons();
        initVolumeSlider();
    }

    private void initMuteRadioButtons() {
        SoundPlayer player = SoundPlayer.getInstance();
        if (player.isEffectsMuted())
            effectsGroup.selectToggle(effectsOffRadioButton);
        else
            effectsGroup.selectToggle(effectsOnRadioButton);
        if (player.isMusicMuted())
            musicGroup.selectToggle(musicOffRadioButton);
        else
            musicGroup.selectToggle(musicOnRadioButton);
        effectsGroup.selectedToggleProperty().addListener(event -> player.muteOrUnmuteEffects());
        musicGroup.selectedToggleProperty().addListener(event -> player.muteOrUnmuteMusic());
    }

    private void initVolumeSlider() {
        SoundPlayer player = SoundPlayer.getInstance();
        effectsSlider.valueProperty().addListener(
                (observable, oldValue, newValue) -> effectsVolumeLabel.setText(Integer.toString(newValue.intValue())));
        musicSlider.valueProperty().addListener(
                (observable, oldValue, newValue) -> musicVolumeLabel.setText(Integer.toString(newValue.intValue())));
        effectsSlider.setValue(player.getEffectsVolume() * 100);
        musicSlider.setValue(player.getMusicVolume() * 100);
        effectsSlider.valueProperty()
                .addListener((observable, oldValue, newValue) -> player.setEffectsVolume(newValue.doubleValue() / 100));
        musicSlider.valueProperty()
                .addListener((observable, oldValue, newValue) -> player.setMusicVolume(newValue.doubleValue() / 100));
    }

    @Override
    public void setReturnButtonAction(EventHandler<ActionEvent> event) {
        returnButton.setOnAction(event);
    }

}
