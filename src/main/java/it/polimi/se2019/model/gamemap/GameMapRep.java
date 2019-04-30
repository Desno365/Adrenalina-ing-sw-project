package it.polimi.se2019.model.gamemap;

import it.polimi.se2019.network.message.Message;
import it.polimi.se2019.network.message.MessageSubtype;
import it.polimi.se2019.network.message.MessageType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A sharable version of the game map.
 * @author MarcerAndrea
 */
public class GameMapRep extends Message {

	private int numOfRows;
	private int	numOfColumns;
	private MapSquareRep[][] mapRep;
	private HashMap<String, Coordinates> playersPositions;
	private ArrayList<Coordinates> spawns;

	public GameMapRep(GameMap gameMapToRepresent) {
		super(MessageType.GAME_MAP_REP, MessageSubtype.INFO);
		this.numOfColumns = gameMapToRepresent.getNumOfColumns();
		this.numOfRows = gameMapToRepresent.getNumOfRows();

		mapRep = new MapSquareRep[gameMapToRepresent.getNumOfRows()][gameMapToRepresent.getNumOfColumns()];

		for (int i = 0; i < numOfRows; i++) {
			for (int j = 0; j < numOfColumns; j++) {
				try{
					this.mapRep[i][j] = new MapSquareRep(gameMapToRepresent.getSquare(new Coordinates(i,j)));
				}catch (OutOfBoundariesException e){
					this.mapRep[i][j] = new MapSquareRep(new AmmoSquare(-1, new boolean[4], new Coordinates(i,j)));
				}
			}
		}

		playersPositions = new HashMap<>();
		gameMapToRepresent.getPlayersCoordinates().forEach((player, coordinates) -> playersPositions.put(player.getPlayerName(), coordinates));
	}

	public int getNumOfRows() {
		return numOfRows;
	}

	public int getNumOfColumns() {
		return numOfColumns;
	}

	public MapSquareRep[][] getMapRep() {
		return mapRep;
	}

	public HashMap<String, Coordinates> getPlayersCoordinates() {
		return playersPositions;
	}

	public boolean equals(Object object){
		boolean temp = (object instanceof GameMapRep &&
				this.numOfColumns == ((GameMapRep) object).numOfColumns &&
				this.numOfRows == ((GameMapRep) object).numOfRows &&
				this.playersPositions.equals(object));
		if (!temp)
			return false;
		temp = true;
		MapSquareRep[][] mapRepToCompare = ((GameMapRep) object).mapRep;
		for (int i = 0; i < numOfRows; i++) {
			for (int j = 0; j < numOfColumns; j++) {
				if(!mapRep[i][j].equals(mapRepToCompare[i][j]))
					return false;
			}
		}
		return true;
	}
}