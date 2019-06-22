package it.polimi.se2019.view.client.gui;

import it.polimi.se2019.model.cards.weapons.WeaponRep;
import it.polimi.se2019.network.message.IntMessage;
import it.polimi.se2019.network.message.MessageSubtype;
import it.polimi.se2019.network.message.MessageType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.List;

public class WeaponChoiceController {
	@FXML
	private ImageView weapon0;
	@FXML
	private ImageView weapon1;
	@FXML
	private ImageView weapon2;
	@FXML
	private Button weaponButton0;
	@FXML
	private Button weaponButton1;
	@FXML
	private Button weaponButton2;
	@FXML
	private Label title;

	private GUIView guiView;
	private Stage stage;

	@FXML
	public void pressedWeapon0() {
		guiView.sendMessage(new IntMessage(0, MessageType.WEAPON, MessageSubtype.ANSWER));
		stage.close();
	}

	@FXML
	public void pressedWeapon1() {
		guiView.sendMessage(new IntMessage(1, MessageType.WEAPON, MessageSubtype.ANSWER));
		stage.close();
	}

	@FXML
	public void pressedWeapon2() {
		guiView.sendMessage(new IntMessage(2, MessageType.WEAPON, MessageSubtype.ANSWER));
		stage.close();
	}

	public void setWeaponsToChoose(List<Integer> indexesOfTheWeapons, List<WeaponRep> weapons) {
		if (!indexesOfTheWeapons.isEmpty()) {
			weapon0.setVisible(true);
			weaponButton0.setDisable(false);
			weaponButton0.setVisible(true);
			weapon0.setImage(loadImage("weapons/" + weapons.get(indexesOfTheWeapons.get(0)).getImagePath()));
		} else {
			weaponButton0.setVisible(false);
			weapon0.setVisible(false);
			weaponButton0.setDisable(true);
		}
		if (indexesOfTheWeapons.size() >= 2) {
			weapon1.setVisible(true);
			weaponButton1.setVisible(true);
			weapon1.setImage(loadImage("weapons/" + weapons.get(indexesOfTheWeapons.get(1)).getImagePath()));
			weaponButton1.setDisable(false);
		} else {
			weapon1.setVisible(false);
			weaponButton1.setVisible(false);
			weaponButton1.setDisable(true);
		}
		if (indexesOfTheWeapons.size() >= 3) {
			weapon2.setVisible(true);
			weaponButton2.setVisible(true);
			weaponButton2.setDisable(false);
			weapon2.setImage(loadImage("weapons/" + weapons.get(indexesOfTheWeapons.get(2)).getImagePath()));
		} else {
			weapon2.setVisible(false);
			weaponButton2.setVisible(false);
			weaponButton2.setDisable(true);
		}
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void setGuiAndStage(GUIView guiView, Stage stage) {
		this.guiView = guiView;
		this.stage = stage;
	}

	private Image loadImage(String filePath) {
		System.out.println("/graphicassets/" + filePath + ".png");
		return new Image(getClass().getResource("/graphicassets/" + filePath + ".png").toString());
	}
}
