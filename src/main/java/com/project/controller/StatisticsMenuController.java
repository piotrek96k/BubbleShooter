package com.project.controller;

import java.util.List;

import com.project.dialog.DialogOpener;
import com.project.exception.ReadingFileException;
import com.project.image.GameImage;
import com.project.model.mode.DifficultyLevel;
import com.project.model.mode.GameMode;
import com.project.model.player.Player;
import com.project.resources.Resources;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class StatisticsMenuController implements Returnable {

	@FXML
	private GridPane gridPane;

	@FXML
	private ChoiceBox<GameMode> modeChoiceBox;

	@FXML
	private ChoiceBox<DifficultyLevel> levelChoiceBox;

	@FXML
	private TableView<Player> tableView;

	@FXML
	private Button returnButton;

	@FXML
	private void initialize() {
		gridPane.setStyle(GameImage.getRandomWallpaperStyle());
		MainMenuController.initChoiceBoxes(levelChoiceBox, modeChoiceBox);
		initTableView();
		modeChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::modeChoiceBoxSelectionChanged);
		levelChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::levelChoiceBoxSelectionChanged);
	}

	public void init() {
		modeChoiceBoxSelectionChanged(modeChoiceBox.getSelectionModel().selectedItemProperty(), null,
				modeChoiceBox.getSelectionModel().getSelectedItem());
		levelChoiceBoxSelectionChanged(levelChoiceBox.getSelectionModel().selectedItemProperty(), null,
				levelChoiceBox.getSelectionModel().getSelectedItem());
	}

	private void modeChoiceBoxSelectionChanged(ObservableValue<? extends GameMode> observable, GameMode oldValue,
			GameMode newValue) {
		try {
			if (newValue.equals(GameMode.ARCADE_MODE) && levelChoiceBox.getSelectionModel().getSelectedItem() != null)
				loadTableView(Player.getArcadeModePlayers(levelChoiceBox.getSelectionModel().getSelectedItem()));
			else if (newValue.equals(GameMode.SURVIVAL_MODE))
				loadTableView(Player.getSurvivalModePlayers());
		} catch (ReadingFileException e) {
			DialogOpener.openErrorDialog(e.getMessage());
		}
	}

	private void levelChoiceBoxSelectionChanged(ObservableValue<? extends DifficultyLevel> observable,
			DifficultyLevel oldValue, DifficultyLevel newValue) {
		if (newValue != null)
			try {
				loadTableView(Player.getArcadeModePlayers(newValue));
			} catch (ReadingFileException e) {
				DialogOpener.openErrorDialog(e.getMessage());
			}
	}

	@Override
	public void setReturnButtonAction(EventHandler<ActionEvent> event) {
		returnButton.setOnAction(event);
	}

	private void loadTableView(List<Player> players) {
		tableView.getItems().clear();
		for (Player player : players)
			tableView.getItems().add(player);
	}

	private void initTableView() {
		addColumn(Resources.RESOURCE_BUNDLE.getString("StatisticsMenu.Controller.initTableView.id"), "id");
		addColumn(Resources.RESOURCE_BUNDLE.getString("StatisticsMenu.Controller.initTableView.name"), "name");
		addColumn(Resources.RESOURCE_BUNDLE.getString("StatisticsMenu.Controller.initTableView.points"), "points");
		addColumn(Resources.RESOURCE_BUNDLE.getString("StatisticsMenu.Controller.initTableView.time"), "time");
	}

	private void addColumn(String name, String pointer) {
		TableColumn<Player, String> column = new TableColumn<>(name);
		column.setCellValueFactory(new PropertyValueFactory<>(pointer));
		tableView.getColumns().add(column);
	}

}