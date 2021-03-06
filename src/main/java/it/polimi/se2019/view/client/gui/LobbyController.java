package it.polimi.se2019.view.client.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * FXML controller to display the lobby.
 *
 * @author MarcerAndrea
 */
public class LobbyController {

	@FXML
	private Label timerLabel;

	@FXML
	private ListView<String> nicknames;

	void showNicknames(List<String> players) {
		ObservableList<String> playerList = observableArrayList(players);
		nicknames.setItems(playerList);
	}

	void startTimer() {
		timerLabel.setVisible(true);
	}

	void stopTimer() {
		timerLabel.setVisible(false);
	}
}
