package com.piotrek.fxml;

import java.io.IOException;

import com.piotrek.resources.Resources;

import javafx.fxml.FXMLLoader;

public class Loader<T, U> {

    private T controller;

    private U view;

    public Loader(FxmlDocument document) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(document.getDocument()));
        try {
            loader.setResources(Resources.RESOURCE_BUNDLE);
            view = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public T getController() {
        return controller;
    }

    public U getView() {
        return view;
    }

}