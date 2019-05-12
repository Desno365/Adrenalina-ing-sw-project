package it.polimi.se2019.model.gamemap;

import com.google.gson.*;
import it.polimi.se2019.model.Representable;
import it.polimi.se2019.model.Representation;
import it.polimi.se2019.model.cards.Card;
import it.polimi.se2019.model.cards.ammo.AmmoType;
import it.polimi.se2019.model.gameboard.GameBoard;
import it.polimi.se2019.model.player.Player;
import it.polimi.se2019.utils.CardinalDirection;
import it.polimi.se2019.utils.Color;
import it.polimi.se2019.utils.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the game map
 *
 * @author MarcerAndrea
 */
public class GameMap extends Representable {

	private int numOfRows;
	private int numOfColumns;
	private Square[][] map;
	private List<List<Coordinates>> rooms = new ArrayList<>();
	private HashMap<Player, Coordinates> playersPositions = new HashMap<>();
	private ArrayList<Coordinates> spawnSquaresCoordinates = new ArrayList<>();
	private GameMapRep gameMapRep;

	public GameMap(String mapName, List<Player> players, GameBoard gameBoard) {

		generateMapJson(mapName, gameBoard);
		connectSquares();
		addSquaresToRooms();
		fillMap();

		for (Player playerToAdd : players) {
			playersPositions.put(playerToAdd, null);
		}

		setChanged();
	}

	/**
	 * For each square in the map if it has an empty slot
	 * it gets refilled.
	 */
	public void fillMap() {
		for (int i = 0; i < numOfRows; i++) {
			for (int j = 0; j < numOfColumns; j++) {
				map[i][j].refillCards();
			}
		}
		Utils.logInfo("GameMap -> fillMap(): Map completely filled");
	}

	/**
	 * Returns the number of rows.
	 *
	 * @return the number of rows
	 */
	int getNumOfRows() {
		return numOfRows;
	}

	/**
	 * Returns the number of columns.
	 *
	 * @return the number of columns
	 */
	int getNumOfColumns() {
		return numOfColumns;
	}

	/**
	 * Returns the player position.
	 *
	 * @return the player position
	 */
	public Map<Player, Coordinates> getPlayersCoordinates() {
		return new HashMap<>(playersPositions);
	}

	/**
	 * Returns the player's coordinates.
	 *
	 * @param playerToFind player to find
	 * @return the player's coordinates
	 * @throws PlayerNotInTheMapException when the player is not in the map
	 */
	public Coordinates getPlayerCoordinates(Player playerToFind) {
		Coordinates playerCoordinates = playersPositions.get(playerToFind);
		if (playerCoordinates != null)
			return playerCoordinates;
		else
			throw new PlayerNotInTheMapException("player position is null");
	}

	/**
	 * Returns the player's square.
	 *
	 * @param playerToFind player to find
	 * @return the player's square
	 * @throws PlayerNotInTheMapException when the player is not in the map
	 */
	public Square getPlayerSquare(Player playerToFind) {
		Coordinates playerCoordinates = playersPositions.get(playerToFind);
		if (playerCoordinates != null)
			return map[playerCoordinates.getRow()][playerCoordinates.getColumn()];
		else
			throw new PlayerNotInTheMapException("player position is null");
	}

	/**
	 * Moves the player in the specified coordinates.
	 *
	 * @param playerToMove player to move
	 * @param coordinates  coordinates where the player has to be moved to
	 * @throws OutOfBoundariesException when the player is moved to a square not in the map
	 */
	public void movePlayerTo(Player playerToMove, Coordinates coordinates) {
		if (isIn(coordinates)) {
			playersPositions.replace(playerToMove, coordinates);
			setChanged();
			Utils.logInfo("GameMap -> movePlayerTo(): " + playerToMove.getPlayerName() + " moved to " + coordinates);
		}
		else
			throw new OutOfBoundariesException("tried to move the player out of the map" + coordinates.toString());
	}

	/**
	 * Returns the list of all reachable coordinates by a player within the specified distance.
	 * @param player the player that wants to move.
	 * @param maxDistance distance the player wants to move.
	 * @return the list of reachable coordinates.
	 */
	public List<Coordinates> reachableCoordinates(Player player, int maxDistance) {
		List<Coordinates> reachableCoordinates = new ArrayList<>();
		reachableSquares(getSquare(playersPositions.get(player)), maxDistance, reachableCoordinates);
		Utils.logInfo("GameMap -> reachableCoordinates(): " + player.getPlayerName() + " can reach in " + maxDistance + " moves: " + reachableCoordinates);
		return reachableCoordinates;
	}


	/**
	 * Returns the list of all reachable coordinates from the specified coordinates within the specified distance.
	 * @param coordinates starting coordinates.
	 * @param maxDistance distance the player wants to move.
	 * @return the list of reachable coordinates.
	 */
	public List<Coordinates> reachableCoordinates(Coordinates coordinates, int maxDistance) {
		ArrayList<Coordinates> reachableCoordinates = new ArrayList<>();
		reachableSquares(getSquare(coordinates), maxDistance, reachableCoordinates);
		Utils.logInfo("GameMap -> reachableCoordinates(): From " + coordinates + " is possible to reach in " + maxDistance + " moves: " + reachableCoordinates);
		return reachableCoordinates;
	}

	/**
	 * Returns the set of all reachable squares from the coordinates and distance at most max distance.
	 *
	 * @param square   square of the starting point
	 * @param maxDistance maximum distance
	 * @return the set of all reachable squares from the coordinates and distance at most max distance
	 */
	private List<Coordinates> reachableSquares(Square square, int maxDistance, List<Coordinates> reachableCoordinates) {

		if (maxDistance != 0) {
			for (Square adjacentSquare : square.getAdjacentSquares()) {
				reachableSquares(adjacentSquare, maxDistance - 1, reachableCoordinates);
			}
		}

		if (!(reachableCoordinates.contains(getCoordinates(square))))
			reachableCoordinates.add(getCoordinates(square));
		return reachableCoordinates;
	}

	/**
	 * Returns the set of all the squares belonging to the same room of the specified square.
	 *
	 * @param square a square
	 * @return the set of all the squares belonging to the same room of the specified square
	 * @throws OutOfBoundariesException when the square does not belong to the map
	 */
	public List<Coordinates> getRoomCoordinates(Square square) {
		if (isIn(square))
			return new ArrayList<>(rooms.get(square.getRoomID()));
		else
			throw new OutOfBoundariesException("the square does not belong to the map " + getCoordinates(square));
	}

	/**
	 * Returns the set of all the squares belonging to the same room of the specified square.
	 *
	 * @param coordinates a coordinates
	 * @return the set of all the squares belonging to the same room of the specified square
	 * @throws OutOfBoundariesException the coordinates do not belong to the map
	 */
	public List<Coordinates> getRoomCoordinates(Coordinates coordinates) {
		if (isIn(coordinates))
			return new ArrayList<>(rooms.get(getSquare(coordinates).getRoomID()));
		else
			throw new OutOfBoundariesException("the coordinates do not belong to the map " + coordinates);
	}

	/**
	 * Returns true if and only if the player2 is visible from the player1. This is done looking at the rooms ID of the squares where the player are.
	 *
	 * @param player1 player how is observing
	 * @param player2 player target
	 * @return true if and only if the player2 is visible from the player1
	 */
	public boolean isVisible(Player player1, Player player2) {
		Square squarePlayer1 = getSquare(playersPositions.get(player1));
		Square squarePlayer2 = getSquare(playersPositions.get(player2));

		if (squarePlayer1.getRoomID() == squarePlayer2.getRoomID()) {
			Utils.logInfo("GameMap -> isVisible(): Player1 in " + squarePlayer1.getCoordinates() + " " + squarePlayer1.getRoomID() + " can see Player2 in " + squarePlayer2.getCoordinates() + " " + squarePlayer2.getRoomID());
			return true;
		}


		for (Square adjacentSquare : squarePlayer1.getAdjacentSquares()) {
			if (adjacentSquare.getRoomID() == squarePlayer2.getRoomID()) {
				Utils.logInfo("GameMap -> isVisible(): Player1 in " + squarePlayer1.getCoordinates() + " " + squarePlayer1.getRoomID() + " can see Player2 in " + squarePlayer2.getCoordinates() + " " + squarePlayer2.getRoomID());
				return true;
			}
		}
		Utils.logInfo("GameMap -> isVisible():  Player1 in " + squarePlayer1.getCoordinates() + " " + squarePlayer1.getRoomID() + " cannot see Player2 in " + squarePlayer2.getCoordinates() + " " + squarePlayer2.getRoomID());
		return false;
	}

	/**
	 * Returns the coordinates of the spawn square associated with the ammo type or null if it could not find it
	 *
	 * @param ammoType ammo type of the spawn we want
	 * @return the coordinates of the spawn square associated with the ammo type or null if it could not find it
	 */
	public Coordinates getSpawnSquare(AmmoType ammoType) {
		for (Coordinates spawnCoordinates : spawnSquaresCoordinates) {
			if (((SpawnSquare) getSquare(spawnCoordinates)).getAmmoType().equals(ammoType))
				return spawnCoordinates;
		}
		return null;
	}

	/**
	 * Used to know if in some coordinates there is a spawn square
	 *
	 * @param coordinates coordinates of the square to check
	 * @return true if and only if at the coordinates there is a Spawn square
	 */
	public boolean isSpawnSquare(Coordinates coordinates) {
		return spawnSquaresCoordinates.contains(coordinates);
	}

	/**
	 * Removes from the square where the player is the card in the specified index.
	 *
	 * @param coordinates coordinates of the square.
	 * @param index       index of the card's solt to grab.
	 * @return the card grabbed.
	 */
	public Card grabCard(Coordinates coordinates, int index) {
		return getSquare(coordinates).grabCard(index);
	}

	public void addCard(Coordinates coordinates, Card cardToAdd) {
		getSquare(coordinates).addCard(cardToAdd);
	}

	/**
	 * Given the square returns its coordinates in the map.
	 *
	 * @param square square we want to know thw coordinates of
	 * @return the coordinates of the square
	 * @throws OutOfBoundariesException if the square does not belong to the map.
	 */
	Coordinates getCoordinates(Square square) {
		if (isIn(square))
			return square.getCoordinates();
		else
			throw new OutOfBoundariesException("square does not belong to the map");
	}

	/**
	 * Given the coordinates of the map returns the associated square.
	 *
	 * @param coordinates coordinates of the square we want
	 * @return the square in the specified coordinates
	 * @throws OutOfBoundariesException if the coordinates do not belong to the map
	 */
	Square getSquare(Coordinates coordinates) {
		if (isIn(coordinates))
			return map[coordinates.getRow()][coordinates.getColumn()];
		else
			throw new OutOfBoundariesException("the coordinates do not belong to the map " + coordinates);
	}


	public List<Player> reachablePlayers(Player player, int distance) {
		List<Player> reachablePlayers = new ArrayList<>();
		List<Coordinates> reachableCoordinates = reachableCoordinates(player, distance);
		for (Coordinates coordinates : reachableCoordinates) {
			reachablePlayers.addAll(getPlayersFromCoordinates(coordinates));
		}
		return reachablePlayers;
	}

	public List<Player> reachableAndVisiblePlayers(Player player, int distance) {
		List<Player> reachablePlayers = reachablePlayers(player, distance);
		List<Player> reachableAndVisiblePlayers = new ArrayList<>();
		for (Player otherPlayer : reachablePlayers) {
			if (isVisible(player, otherPlayer))
				reachableAndVisiblePlayers.add(otherPlayer);
		}
		return reachableAndVisiblePlayers;
	}

	public List<Player> getPlayersFromCoordinates(Coordinates coordinates) {
		List<Player> players = new ArrayList<>();
		for (Map.Entry position : playersPositions.entrySet()) {
			if (position.getValue().equals(coordinates))
				players.add((Player) position.getKey());
		}
		return players;
	}

	/**
	 * Returns true if and only if the coordinates belong to the map
	 *
	 * @param coordinates coordinates to check
	 * @return true if and only if the coordinates belong to the map
	 */
	private boolean isIn(Coordinates coordinates) {
		return (!((coordinates.getRow() >= numOfRows) || (coordinates.getColumn() >= numOfColumns))) &&
				map[coordinates.getRow()][coordinates.getColumn()].getRoomID() != -1;
	}

	/**
	 * Returns true if and only if the square belong to the map
	 *
	 * @param square square to check
	 * @return true if and only if the square belong to the map
	 */
	private boolean isIn(Square square) {
		Coordinates coordinates = square.getCoordinates();
		return isIn(square.getCoordinates()) && map[coordinates.getRow()][coordinates.getColumn()].equals(square);
	}

	/**
	 * Adds a Square to the map according to the specified coordinates.
	 * @param square JsonObject to parse.
	 */
	private void addSquareToMap(JsonObject square, GameBoard gameBoard) throws IOException {

		Coordinates squareCoordinates = new Coordinates(square.get("row").getAsInt(), square.get("column").getAsInt());

		switch (square.get("type").getAsString()) {
			case "AmmoSquare":
				map[squareCoordinates.getRow()][squareCoordinates.getColumn()] = new AmmoSquare(square.get("roomID").getAsInt(), Color.CharacterColorType.valueOf(square.get("squareColor").getAsString()), getBooleanArray(square.getAsJsonArray("possibleDirection")), squareCoordinates, gameBoard);
				break;

			case "SpawnSquare":
				map[squareCoordinates.getRow()][squareCoordinates.getColumn()] = new SpawnSquare(square.get("roomID").getAsInt(), AmmoType.valueOf(square.get("ammoType").getAsString()), Color.CharacterColorType.valueOf(square.get("squareColor").getAsString()), getBooleanArray(square.getAsJsonArray("possibleDirection")), squareCoordinates, gameBoard);
				spawnSquaresCoordinates.add(squareCoordinates);
				break;

			case "VoidSquare":
				map[squareCoordinates.getRow()][squareCoordinates.getColumn()] = new VoidSquare(squareCoordinates);
				break;

			default:
				throw new IOException("failed to add the map");
		}
	}

	/**
	 * Creates the structure to memorize the squares belonging to each room
	 */
	private void addSquaresToRooms() {
		for (int i = 0; i < numOfRows; i++)
			for (int j = 0; j < numOfColumns; j++) {
				int roomID = map[i][j].getRoomID();
				if (roomID >= 0)
					rooms.get(roomID).add(new Coordinates(i, j));
			}
	}

	/**
	 * Links each square to the adjacent ones
	 */
	private void connectSquares() {
		Square square;
		for (int i = 0; i < numOfRows; i++) {
			for (int j = 0; j < numOfColumns; j++) {
				for (CardinalDirection direction : CardinalDirection.values()) {
					square = map[i][j];
					if (square.getRoomID() != -1 && square.getPossibleDirections()[direction.ordinal()]) {
						square.addAdjacentSquare(getSquare(Coordinates.getDirectionCoordinates(new Coordinates(i, j), direction)));
						Utils.logInfo("GameMap -> connectSquares(): Adding " + getSquare(Coordinates.getDirectionCoordinates(new Coordinates(i, j), direction)).getCoordinates() + " to adjacent squares of " + square.getCoordinates());
					}

				}
			}
		}
	}

	/**
	 * Receives the name of the map to generate and initialize the squares according to the file.
	 *
	 * @param mapName: name of the map to load
	 */
	private void generateMapJson(String mapName, GameBoard gameBoard) {

		try (Reader reader = new FileReader(new File(Thread.currentThread().getContextClassLoader().getResource("maps/" + mapName + ".json").getFile()))) {
			JsonParser parser = new JsonParser();
			JsonObject rootObject = parser.parse(reader).getAsJsonObject();
			numOfRows = rootObject.get("row").getAsInt();
			numOfColumns = rootObject.get("column").getAsInt();

			map = new Square[numOfRows][numOfColumns];

			for (int i = 0; i < Color.CharacterColorType.values().length; i++)
				rooms.add(new ArrayList<>());

			JsonArray squares = rootObject.getAsJsonArray("squares");
			for (JsonElement entry : squares) {
				addSquareToMap(entry.getAsJsonObject(), gameBoard);
			}
		} catch (IOException | JsonParseException e) {
			Utils.logError("Cannot parse " + mapName, e);
		}
	}

	private boolean[] getBooleanArray(JsonArray ja) {
		boolean[] result = new boolean[4];

		for (int i = 0; i < ja.size(); i++) {
			result[i] = ja.get(i).getAsBoolean();
		}

		return result;
	}

	/**
	 * Returns the squares's representation in the specified coordinates.
	 *
	 * @param coordinates coordinates of the square.
	 * @return the squares's representation in the specified coordinates.
	 */
	SquareRep getSquareRep(Coordinates coordinates) {
		return map[coordinates.getRow()][coordinates.getColumn()].getRep();
	}

	/**
	 * Updates the game map's representation.
	 */
	public void updateRep() {
		if (gameMapRep == null || hasChanged()) {
			gameMapRep = new GameMapRep(this);
			Utils.logInfo("GameMap -> updateRep(): The game map representation has been updated");
		} else
			Utils.logInfo("GameBoard -> updateRep(): The game map representation is already up to date");
	}

	/**
	 * Returns the representation of the game map.
	 * @return the representation of the game map.
	 */
	public Representation getRep() {
		return gameMapRep;
	}
}

/**
 * Thrown when some coordinates are out of the map.
 *
 * @author MarcerAndrea
 */
class OutOfBoundariesException extends RuntimeException {

	/**
	 * Constructs an OutOfBoundariesException with the specified message.
	 *
	 * @param message the detail message.
	 */
	public OutOfBoundariesException(String message) {
		super(message);
	}
}

/**
 * Thrown when a player is not in the Map.
 *
 * @author MarcerAndrea
 */
class PlayerNotInTheMapException extends RuntimeException {

	/**
	 * Constructs an PlayerNotInTheMapException with the specified message.
	 *
	 * @param message the detail message.
	 */
	public PlayerNotInTheMapException(String message) {
		super(message);
	}
}