package com.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private Button ZakonczButton;

    @FXML
    private Button NajlepsiGraczeButton;

    @FXML
    private Button OpcjeButton;

    @FXML
    private Button ZagrajButton;


    @FXML
    public void initialize(){

    }

    @FXML
    void NajlepsiGraczeButtonAction(ActionEvent event) throws IOException {
        Parent opcjeMenuParent = FXMLLoader.load(getClass().getResource("/FXMLfiles/OpcjeMenu.fxml"));
        Scene opcjeMenuScene= new Scene(opcjeMenuParent);
        Stage window= (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(opcjeMenuScene);
        window.show();
    }

    @FXML
    void OpcjeButtonAction(ActionEvent event) throws IOException{
        Parent opcjeMenuParent = FXMLLoader.load(getClass().getResource("/FXMLfiles/OpcjeMenu.fxml"));
        Scene opcjeMenuScene= new Scene(opcjeMenuParent);
        Stage window= (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(opcjeMenuScene);
        window.show();
    }

    @FXML
    void ZagrajButtonAction(ActionEvent event) throws IOException {
        Parent zagrajMenuParent = FXMLLoader.load(getClass().getResource("/FXMLfiles/ZagrajMenu.fxml"));
        Scene zagrajMenuScene= new Scene(zagrajMenuParent);
        Stage window= (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(zagrajMenuScene);
        window.show();
    }

    @FXML
    void ZakonczButtonAction(ActionEvent event) {

    }
}
