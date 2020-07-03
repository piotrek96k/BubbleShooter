package com.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;

public class OpcjeMenuController {
    @FXML
    public void initialize(){

    }

    @FXML
    private Button PowrotButton;

    @FXML
    private Label GlosnoscLabel;

    @FXML
    private Label MuzykaLabel;

    @FXML
    private Label EfektyDzwiekoweLabel;

    @FXML
    private RadioButton EfektyDzwiekoweONRadio;

    @FXML
    private ToggleGroup EfektyDzwiekoweGroup;

    @FXML
    private RadioButton EfektyDzwiekoweOFFRadio;

    @FXML
    private RadioButton MuzykaONRadio;

    @FXML
    private ToggleGroup MuzykaGroup;

    @FXML
    private RadioButton MuzykaOFFRadio;

    @FXML
    private Slider GlosnoscSlider;

    @FXML
    void PowrotButtonAction(ActionEvent event) throws IOException {
        Parent mainMenuParent = FXMLLoader.load(getClass().getResource("/FXMLfiles/MainMenu.fxml"));
        Scene mainMenuScene= new Scene(mainMenuParent);
        Stage window= (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
        window.show();
    }

}
