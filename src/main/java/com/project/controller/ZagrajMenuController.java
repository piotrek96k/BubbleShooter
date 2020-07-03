package com.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ZagrajMenuController {
    @FXML
    public void initialize(){

    }

    @FXML
    private Button PowrotButton;

    @FXML
    private Button GrajButton;

    @FXML
    private Label PoziomLabel;

    @FXML
    private ChoiceBox<?> PoziomChociceBox;

    @FXML
    private Label TrybGryLabel;

    @FXML
    private ChoiceBox<?> TrybGryChociceBox;

    @FXML
    private Label ImieLabel;

    @FXML
    private TextField ImieTextField;

    @FXML
    void GrajButtonAction(ActionEvent event) throws IOException {
        Parent mainMenuParent = FXMLLoader.load(getClass().getResource("/FXMLfiles/Zagraj.fxml"));
        Scene mainMenuScene= new Scene(mainMenuParent);
        Stage window= (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
        window.show();
    }

    @FXML
    void PowrotButtonAction(ActionEvent event) throws IOException {
        Parent mainMenuParent = FXMLLoader.load(getClass().getResource("/FXMLfiles/MainMenu.fxml"));
        Scene mainMenuScene= new Scene(mainMenuParent);
        Stage window= (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
        window.show();
    }

}
