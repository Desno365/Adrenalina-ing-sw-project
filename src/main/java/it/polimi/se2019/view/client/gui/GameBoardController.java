package it.polimi.se2019.view.client.gui;

import it.polimi.se2019.model.cards.CardRep;
import it.polimi.se2019.model.cards.ammo.AmmoType;
import it.polimi.se2019.model.gamemap.Coordinates;
import it.polimi.se2019.model.gamemap.GameMapRep;
import it.polimi.se2019.model.gamemap.SpawnSquareRep;
import it.polimi.se2019.model.gamemap.SquareRep;
import it.polimi.se2019.model.player.PlayerRep;
import it.polimi.se2019.utils.Utils;
import it.polimi.se2019.view.client.ModelRep;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameBoardController {
	private Group[][] map;
	private ImageView[][] bansheePosition;
	private ImageView[][] destructorPosition;
	private ImageView[][] sprogPosition;
	private ImageView[][] violetPosition;
	private ImageView[][] dozerPosition;
	private ImageView[][] ammoCardPosition;


	@FXML
	private Group square00;
	@FXML
	private Button button00;
	@FXML
	private ImageView ammoCard00;
	@FXML
	private ImageView banshee00;
	@FXML
	private ImageView sprog00;
	@FXML
	private ImageView dozer00;
	@FXML
	private ImageView destructor00;
	@FXML
	private ImageView violet00;


	@FXML
	private Group square01;
	@FXML
	private Button button01;
	@FXML
	private ImageView ammoCard01;
	@FXML
	private ImageView banshee01;
	@FXML
	private ImageView sprog01;
	@FXML
	private ImageView dozer01;
	@FXML
	private ImageView destructor01;
	@FXML
	private ImageView violet01;

	@FXML
	private Group square02;
	@FXML
	private Button button02;
	@FXML
	private ImageView banshee02;
	@FXML
	private ImageView sprog02;
	@FXML
	private ImageView dozer02;
	@FXML
	private ImageView destructor02;
	@FXML
	private ImageView violet02;

	@FXML
	private Group square03;
	@FXML
	private Button button03;
	@FXML
	private ImageView ammoCard03;
	@FXML
	private ImageView banshee03;
	@FXML
	private ImageView sprog03;
	@FXML
	private ImageView dozer03;
	@FXML
	private ImageView destructor03;
	@FXML
	private ImageView violet03;

	@FXML
	private Group square10;
	@FXML
	private Button button10;
	@FXML
	private ImageView banshee10;
	@FXML
	private ImageView sprog10;
	@FXML
	private ImageView dozer10;
	@FXML
	private ImageView destructor10;
	@FXML
	private ImageView violet10;

	@FXML
	private Group square11;
	@FXML
	private Button button11;
	@FXML
	private ImageView ammoCard11;
	@FXML
	private ImageView banshee11;
	@FXML
	private ImageView sprog11;
	@FXML
	private ImageView dozer11;
	@FXML
	private ImageView destructor11;
	@FXML
	private ImageView violet11;

	@FXML
	private Group square12;
	@FXML
	private Button button12;
	@FXML
	private ImageView ammoCard12;
	@FXML
	private ImageView banshee12;
	@FXML
	private ImageView sprog12;
	@FXML
	private ImageView dozer12;
	@FXML
	private ImageView destructor12;
	@FXML
	private ImageView violet12;


	@FXML
	private Group square13;
	@FXML
	private Button button13;
	@FXML
	private ImageView ammoCard13;
	@FXML
	private ImageView banshee13;
	@FXML
	private ImageView sprog13;
	@FXML
	private ImageView dozer13;
	@FXML
	private ImageView destructor13;
	@FXML
	private ImageView violet13;

	@FXML
	private Group square20;
	@FXML
	private Button button20;
	@FXML
	private ImageView ammoCard20;
	@FXML
	private ImageView banshee20;
	@FXML
	private ImageView sprog20;
	@FXML
	private ImageView dozer20;
	@FXML
	private ImageView destructor20;
	@FXML
	private ImageView violet20;

	@FXML
	private Group square21;
	@FXML
	private Button button21;
	@FXML
	private ImageView ammoCard21;
	@FXML
	private ImageView banshee21;
	@FXML
	private ImageView sprog21;
	@FXML
	private ImageView dozer21;
	@FXML
	private ImageView destructor21;
	@FXML
	private ImageView violet21;

	@FXML
	private Group square22;
	@FXML
	private Button button22;
	@FXML
	private ImageView ammoCard22;
	@FXML
	private ImageView banshee22;
	@FXML
	private ImageView sprog22;
	@FXML
	private ImageView dozer22;
	@FXML
	private ImageView destructor22;
	@FXML
	private ImageView violet22;

	@FXML
	private Group square23;
	@FXML
	private Button button23;
	@FXML
	private ImageView banshee23;
	@FXML
	private ImageView sprog23;
	@FXML
	private ImageView dozer23;
	@FXML
	private ImageView destructor23;
	@FXML
	private ImageView violet23;


	@FXML
	private Button weaponRed0;
	@FXML
	private ImageView weponImageRed0;
	@FXML
	private Button weaponRed1;
	@FXML
	private ImageView weponImageRed1;
	@FXML
	private Button weaponRed2;
	@FXML
	private ImageView weponImageRed2;
	@FXML
	private Button weaponBlue0;
	@FXML
	private ImageView weponImageBlue0;
	@FXML
	private Button weaponBlue1;
	@FXML
	private ImageView weponImageBlue1;
	@FXML
	private Button weaponBlue2;
	@FXML
	private ImageView weponImageBlue2;
	@FXML
	private Button weaponYellow0;
	@FXML
	private ImageView weponImageYellow0;
	@FXML
	private Button weaponYellow1;
	@FXML
	private ImageView weponImageYellow1;
	@FXML
	private Button weaponYellow2;
	@FXML
	private ImageView weponImageYellow2;

	private PlayerInventoryController inventoryController;
	private Stage inventoryStage;
	@FXML
	private ImageView playerIcon0;
	@FXML
	private Button playerIconButton0;
	private PlayerRep playerRep0;
	@FXML
	private ImageView playerIcon1;
	@FXML
	private Button playerIconButton1;
	private PlayerRep playerRep1;
	@FXML
	private ImageView playerIcon2;
	@FXML
	private Button playerIconButton2;
	private PlayerRep playerRep2;
	@FXML
	private ImageView playerIcon3;
	@FXML
	private Button playerIconButton3;
	private PlayerRep playerRep3;
	@FXML
	private ImageView playerIcon4;
	@FXML
	private Button playerIconButton4;
	private PlayerRep playerRep4;

	@FXML
	private ImageView skullToken00;
	@FXML
	private ImageView skullToken10;
	@FXML
	private ImageView skullToken20;
	@FXML
	private ImageView skullToken30;
	@FXML
	private ImageView skullToken40;
	@FXML
	private ImageView skullToken50;
	@FXML
	private ImageView skullToken60;
	@FXML
	private ImageView skullToken70;
	@FXML
	private ImageView skullToken80;
	@FXML
	private ImageView skullToken90;
	@FXML
	private ImageView skullToken100;
	@FXML
	private ImageView skullToken110;
	@FXML
	private ImageView skullToken120;
	@FXML
	private ImageView skullToken130;
	@FXML
	private ImageView skullToken140;
	@FXML
	private ImageView skullToken150;
	@FXML
	private ImageView skullToken01;
	@FXML
	private ImageView skullToken11;
	@FXML
	private ImageView skullToken21;
	@FXML
	private ImageView skullToken31;
	@FXML
	private ImageView skullToken41;
	@FXML
	private ImageView skullToken51;
	@FXML
	private ImageView skullToken61;
	@FXML
	private ImageView skullToken71;
	@FXML
	private ImageView skullToken81;
	@FXML
	private ImageView skullToken91;
	@FXML
	private ImageView skullToken101;
	@FXML
	private ImageView skullToken111;
	@FXML
	private ImageView skullToken121;
	@FXML
	private ImageView skullToken131;
	@FXML
	private ImageView skullToken141;
	@FXML
	private ImageView skullToken151;

	@FXML
	private ImageView doubleKillToken0;
	@FXML
	private ImageView doubleKillToken1;
	@FXML
	private ImageView doubleKillToken2;
	@FXML
	private ImageView doubleKillToken3;
	@FXML
	private ImageView doubleKillToken4;
	@FXML
	private ImageView doubleKillToken5;
	@FXML
	private ImageView doubleKillToken6;
	@FXML
	private ImageView doubleKillToken7;
	@FXML
	private ImageView doubleKillToken8;
	@FXML
	private ImageView doubleKillToken9;
	@FXML
	private ImageView doubleKillToken10;
	@FXML
	private ImageView doubleKillToken11;

	@FXML
	private ImageView skull0;
	@FXML
	private ImageView skull1;
	@FXML
	private ImageView skull2;
	@FXML
	private ImageView skull3;
	@FXML
	private ImageView skull4;
	@FXML
	private ImageView skull5;
	@FXML
	private ImageView skull6;
	@FXML
	private ImageView skull7;

	@FXML
	private ImageView backGround;

	private boolean initialized = false;


	@FXML
	private void showInventory0() {
		inventoryController.initializeInventory(playerRep0);
		inventoryStage.show();
	}

	@FXML
	private void showInventory1() {
		inventoryController.initializeInventory(playerRep1);
		inventoryStage.show();
	}

	@FXML
	private void showInventory2() {
		inventoryController.initializeInventory(playerRep2);
		inventoryStage.show();
	}

	@FXML
	private void showInventory3() {
		inventoryController.initializeInventory(playerRep3);
		inventoryStage.show();
	}

	@FXML
	private void showInventory4() {
		inventoryController.initializeInventory(playerRep4);
		inventoryStage.show();
	}



	public void init_GameMap(ModelRep modelRep) {
		GameMapRep gameMapRep = modelRep.getGameMapRep();
		map = new Group[gameMapRep.getNumOfRows()][gameMapRep.getNumOfColumns()];
		bansheePosition = new ImageView[gameMapRep.getNumOfRows()][gameMapRep.getNumOfColumns()];
		destructorPosition = new ImageView[gameMapRep.getNumOfRows()][gameMapRep.getNumOfColumns()];
		sprogPosition = new ImageView[gameMapRep.getNumOfRows()][gameMapRep.getNumOfColumns()];
		violetPosition = new ImageView[gameMapRep.getNumOfRows()][gameMapRep.getNumOfColumns()];
		dozerPosition = new ImageView[gameMapRep.getNumOfRows()][gameMapRep.getNumOfColumns()];
		ammoCardPosition = new ImageView[gameMapRep.getNumOfRows()][gameMapRep.getNumOfColumns()];
		SquareRep[][] mapRep = gameMapRep.getMapRep();

		map[0][1] = square01;
		map[0][0] = square00;
		map[0][2] = square02;
		map[0][3] = square03;
		map[1][0] = square10;
		map[1][2] = square12;
		map[1][1] = square11;
		map[2][0] = square20;
		map[1][3] = square13;
		map[2][1] = square21;
		map[2][3] = square23;
		map[2][2] = square22;

		bansheePosition[0][1] = banshee01;
		bansheePosition[0][0] = banshee00;
		bansheePosition[0][2] = banshee02;
		bansheePosition[1][0] = banshee10;
		bansheePosition[0][3] = banshee03;
		bansheePosition[1][1] = banshee11;
		bansheePosition[1][3] = banshee13;
		bansheePosition[1][2] = banshee12;
		bansheePosition[2][0] = banshee20;
		bansheePosition[2][2] = banshee22;
		bansheePosition[2][1] = banshee21;
		bansheePosition[2][3] = banshee23;

		destructorPosition[0][1] = destructor01;
		destructorPosition[0][0] = destructor00;
		destructorPosition[0][3] = destructor03;
		destructorPosition[0][2] = destructor02;
		destructorPosition[1][0] = destructor10;
		destructorPosition[1][2] = destructor12;
		destructorPosition[1][1] = destructor11;
		destructorPosition[1][3] = destructor13;
		destructorPosition[2][0] = destructor20;
		destructorPosition[2][1] = destructor21;
		destructorPosition[2][3] = destructor23;
		destructorPosition[2][2] = destructor22;

		sprogPosition[0][0] = sprog00;
		sprogPosition[0][1] = sprog01;
		sprogPosition[0][3] = sprog03;
		sprogPosition[0][2] = sprog02;
		sprogPosition[1][0] = sprog10;
		sprogPosition[1][1] = sprog11;
		sprogPosition[1][2] = sprog12;
		sprogPosition[2][0] = sprog20;
		sprogPosition[1][3] = sprog13;
		sprogPosition[2][1] = sprog21;
		sprogPosition[2][3] = sprog23;
		sprogPosition[2][2] = sprog22;

		violetPosition[0][0] = violet00;
		violetPosition[0][2] = violet02;
		violetPosition[0][1] = violet01;
		violetPosition[0][3] = violet03;
		violetPosition[1][0] = violet10;
		violetPosition[1][1] = violet11;
		violetPosition[1][2] = violet12;
		violetPosition[1][3] = violet13;
		violetPosition[2][1] = violet21;
		violetPosition[2][0] = violet20;
		violetPosition[2][2] = violet22;
		violetPosition[2][3] = violet23;

		dozerPosition[0][0] = dozer00;
		dozerPosition[0][1] = dozer01;
		dozerPosition[0][2] = dozer02;
		dozerPosition[0][3] = dozer03;
		dozerPosition[1][0] = dozer10;
		dozerPosition[1][1] = dozer11;
		dozerPosition[1][2] = dozer12;
		dozerPosition[1][3] = dozer13;
		dozerPosition[2][0] = dozer20;
		dozerPosition[2][1] = dozer21;
		dozerPosition[2][2] = dozer22;
		dozerPosition[2][3] = dozer23;

		ammoCardPosition[0][0] = ammoCard00;
		ammoCardPosition[0][1] = ammoCard01;
		ammoCardPosition[0][3] = ammoCard03;
		ammoCardPosition[1][1] = ammoCard11;
		ammoCardPosition[1][2] = ammoCard12;
		ammoCardPosition[1][3] = ammoCard13;
		ammoCardPosition[2][0] = ammoCard20;
		ammoCardPosition[2][1] = ammoCard21;
		ammoCardPosition[2][2] = ammoCard22;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				if (mapRep[i][j].getRoomID() == -1) {
					map[i][j].setVisible(false);
					map[i][j].setDisable(true);
				} else {
					if (modelRep.getGameMapRep().isSpawn(new Coordinates(i, j))) {
						updateWeapons(mapRep[i][j].getCards(), ((SpawnSquareRep) mapRep[i][j]).getAssociatedAmmo());
					} else {
						ammoCardPosition[i][j].setImage(loadImage("ammo/" + mapRep[i][j].getCards().get(0).getImagePath()));
						ammoCardPosition[i][j].setVisible(true);
					}
				}
			}
		}

		List<PlayerRep> playersRep = new ArrayList<>();
		for (PlayerRep playerRep : modelRep.getPlayersRep()) {
			if (playerRep.isHidden())
				playersRep.add(playerRep);
		}

		playerRep0 = modelRep.getClientPlayerRep();
		playerIcon0.setImage(loadImage("playerBoards/" + playerRep0.getPgName() + "/icon"));

		if (!playersRep.isEmpty()) {
			playerRep1 = playersRep.get(0);
			playerIcon1.setImage(loadImage("playerBoards/" + playerRep1.getPgName() + "/icon"));
		} else {
			playerIcon1.setVisible(false);
			playerIconButton1.setDisable(true);
			playerIconButton1.setVisible(false);
		}

		if (playersRep.size() >= 2) {
			playerRep2 = playersRep.get(1);
			playerIcon2.setImage(loadImage("playerBoards/" + playerRep2.getPgName() + "/icon"));
		} else {
			playerIcon2.setVisible(false);
			playerIconButton2.setDisable(true);
			playerIconButton2.setVisible(false);
		}

		if (playersRep.size() >= 3) {
			playerRep3 = playersRep.get(2);
			playerIcon3.setImage(loadImage("playerBoards/" + playerRep3.getPgName() + "/icon"));
		} else {
			playerIcon3.setVisible(false);
			playerIconButton3.setDisable(true);
			playerIconButton3.setVisible(false);
		}

		if (playersRep.size() >= 4) {
			playerRep4 = playersRep.get(3);
			playerIcon4.setImage(loadImage("playerBoards/" + playerRep4.getPgName() + "/icon"));
		} else {
			playerIcon4.setVisible(false);
			playerIconButton4.setDisable(true);
			playerIconButton4.setVisible(false);
		}
		setMap(gameMapRep.getName());

		inventoryStage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/PlayerInventory.fxml"));
		try {
			Parent root = loader.load();
			inventoryStage.setTitle("Adrenaline");
			inventoryStage.setScene(new Scene(root));
			inventoryController = loader.getController();
		} catch (IOException e) {
			Utils.logError("Error loading loginController", e);
		}
		inventoryStage.hide();


		initialized = true;
	}

	private void updateWeapons(List<CardRep> card, AmmoType associatedAmmo) {
		if (AmmoType.RED_AMMO.equals(associatedAmmo)) {
			if (card.size() >= 2) {
				weponImageRed1.setImage(loadImage("weapons/" + card.get(1).getImagePath()));
			} else {
				weponImageRed1.setVisible(false);
			}
			if (card.size() >= 1) {
				weponImageRed0.setImage(loadImage("weapons/" + card.get(0).getImagePath()));
			} else {
				weponImageRed0.setVisible(false);
			}
			if (card.size() >= 3) {
				weponImageRed2.setImage(loadImage("weapons/" + card.get(2).getImagePath()));
			} else {
				weponImageRed2.setVisible(false);
			}
		}

		if (AmmoType.BLUE_AMMO.equals(associatedAmmo)) {
			if (card.size() >= 1) {
				weponImageBlue0.setImage(loadImage("weapons/" + card.get(0).getImagePath()));
			} else {
				weponImageBlue0.setVisible(false);
			}
			if (card.size() >= 2) {
				weponImageBlue1.setImage(loadImage("weapons/" + card.get(1).getImagePath()));
			} else {
				weponImageBlue1.setVisible(false);
			}
			if (card.size() >= 3) {
				System.out.println(loadImage("weapons/" + card.get(2).getImagePath()));
				weponImageBlue2.setImage(loadImage("weapons/" + card.get(2).getImagePath()));
			} else {
				weponImageBlue2.setVisible(false);
			}
		}

		if (AmmoType.YELLOW_AMMO.equals(associatedAmmo)) {
			if (card.size() >= 3) {
				weponImageYellow2.setImage(loadImage("weapons/" + card.get(2).getImagePath()));
			} else {
				weponImageYellow2.setVisible(false);
			}
			if (card.size() >= 2) {
				weponImageYellow1.setImage(loadImage("weapons/" + card.get(1).getImagePath()));
			} else {
				weponImageYellow1.setVisible(false);
			}
			if (card.isEmpty()) {
				weponImageYellow0.setVisible(false);
			} else {
				weponImageYellow0.setImage(loadImage("weapons/" + card.get(0).getImagePath()));
			}
		}

	}

	public void updateGameBoard() {
		//TODO update playersRep
	}

	public boolean isInitialized() {
		return initialized;
	}

	private void setMap(String mapName) {
		backGround.setImage(loadImage("maps/" + mapName));
	}

	private Image loadImage(String filePath) {
		System.out.println("/graphicassets/" + filePath + ".png");
		return new Image(getClass().getResource("/graphicassets/" + filePath + ".png").toString());
	}
}
