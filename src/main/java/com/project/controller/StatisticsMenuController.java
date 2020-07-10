package com.project.controller;

import java.util.List;

import com.project.model.mode.DifficultyLevel;
import com.project.model.mode.GameMode;
import com.project.model.player.Player;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StatisticsMenuController implements Returnable {

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
		modeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.equals(GameMode.ARCADE_MODE) && levelChoiceBox.getSelectionModel().getSelectedItem() != null)
				loadTableView(Player.getArcadeModePlayers(levelChoiceBox.getSelectionModel().getSelectedItem()));
			else if (newValue.equals(GameMode.SURVIVAL_MODE))
				loadTableView(Player.getSurvivalModePlayers());
		});
		levelChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null)
				loadTableView(Player.getArcadeModePlayers(newValue));
		});
		MainMenuController.initChoiceBoxes(levelChoiceBox, modeChoiceBox);
		initTableView();
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
		addColumn("Id", "id");
		addColumn("Imi�", "name");
		addColumn("Punkty", "points");
		addColumn("Czas", "time");
	}

	private void addColumn(String name, String pointer) {
		TableColumn<Player, String> column = new TableColumn<Player, String>(name);
		column.setCellValueFactory(new PropertyValueFactory<Player, String>(pointer));
		tableView.getColumns().add(column);
	}

}